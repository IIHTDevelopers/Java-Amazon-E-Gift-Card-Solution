package com.amazonegiftcardapplication.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.amazonegiftcardapplication.model.EGiftCard;
import com.amazonegiftcardapplication.model.User;

public class UserDAOImpl implements UserDAO {

	private Connection connection;

	public UserDAOImpl(Connection connection) {
		this.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}

	@Override
	public void createUser(User user) {
		String sql = "INSERT INTO users (name, age, gender) VALUES (?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, user.getName());
			statement.setInt(2, user.getAge());
			statement.setString(3, user.getGender());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
	}

	@Override
	public void updateUser(User user) {
		String sql = "UPDATE users SET name = ?, age = ?, gender = ? WHERE id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, user.getName());
			statement.setInt(2, user.getAge());
			statement.setString(3, user.getGender());
			statement.setInt(4, user.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
	}

	@Override
	public void deleteUser(User user) {
		String sql = "DELETE FROM users WHERE id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, user.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
	}

	@Override
	public void deleteAllUsers() {
		String sql = "DELETE FROM users";
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
	}

	@Override
	public User getUserById(int userId) {
		String sql = "SELECT * FROM users WHERE id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, userId);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return extractUserFromResultSet(resultSet);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
		return null;
	}

	@Override
	public List<User> getAllUsers() {
		String sql = "SELECT * FROM users";
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
			List<User> users = new ArrayList<>();
			while (resultSet.next()) {
				User user = extractUserFromResultSet(resultSet);
				users.add(user);
			}
			return users;
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
		return null;
	}

	private User extractUserFromResultSet(ResultSet resultSet) throws SQLException {
		User user = new User();
		user.setId(resultSet.getInt("id"));
		user.setName(resultSet.getString("name"));
		user.setAge(resultSet.getInt("age"));
		user.setGender(resultSet.getString("gender"));
		return user;
	}

	@Override
	public List<EGiftCard> getSuggestionsForUser(int userId) {
		List<EGiftCard> suggestions = new ArrayList<>();
		String sql = "SELECT egift_cards.id, egift_cards.name, egift_cards.code, egift_cards.amount, egift_cards.message, egift_cards.is_redeemed "
				+ "FROM egift_cards " + "JOIN payments ON egift_cards.id = payments.card_id "
				+ "JOIN users ON users.id = payments.user_id " + "WHERE users.id = ? " + "ORDER BY payments.id DESC "
				+ "LIMIT 3";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, userId);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				EGiftCard giftCard = new EGiftCard();
				giftCard.setId(resultSet.getInt("id"));
				giftCard.setName(resultSet.getString("name"));
				giftCard.setCode(resultSet.getString("code"));
				giftCard.setAmount(resultSet.getDouble("amount"));
				giftCard.setMessage(resultSet.getString("message"));
				giftCard.setRedeemed(resultSet.getBoolean("is_redeemed"));
				suggestions.add(giftCard);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return suggestions;
	}

	@Override
	public List<EGiftCard> getSharedGiftCardsByUser(int userId) {
		List<EGiftCard> sharedGiftCards = new ArrayList<>();
		String sql = "SELECT egift_cards.id, egift_cards.name, egift_cards.code, egift_cards.amount, egift_cards.message, egift_cards.is_redeemed "
				+ "FROM egift_cards " + "JOIN payments ON egift_cards.id = payments.card_id "
				+ "WHERE payments.user_id = ?";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, userId);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				EGiftCard giftCard = new EGiftCard();
				giftCard.setId(resultSet.getInt("id"));
				giftCard.setName(resultSet.getString("name"));
				giftCard.setCode(resultSet.getString("code"));
				giftCard.setAmount(resultSet.getDouble("amount"));
				giftCard.setMessage(resultSet.getString("message"));
				giftCard.setRedeemed(resultSet.getBoolean("is_redeemed"));
				sharedGiftCards.add(giftCard);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}

		return sharedGiftCards;
	}

	@Override
	public double getRedeemedGiftCardPercentage(int userId) {
		double redeemedPercentage = 0.0;
		String sql = "SELECT COUNT(*) AS total_count, SUM(CASE WHEN is_redeemed THEN 1 ELSE 0 END) AS redeemed_count "
				+ "FROM egift_cards " + "JOIN payments ON egift_cards.id = payments.card_id "
				+ "WHERE payments.user_id = ?";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, userId);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				int totalCount = resultSet.getInt("total_count");
				int redeemedCount = resultSet.getInt("redeemed_count");
				if (totalCount > 0) {
					redeemedPercentage = (redeemedCount * 100.0) / totalCount;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}

		return redeemedPercentage;
	}

}
