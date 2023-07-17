package com.amazonegiftcardapplication.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.amazonegiftcardapplication.model.EGiftCard;

public class EGiftCardDAOImpl implements EGiftCardDAO {

	private Connection connection;

	public EGiftCardDAOImpl(Connection connection) {
		this.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}

	@Override
	public void createEGiftCard(EGiftCard giftCard) {
		String sql = "INSERT INTO egift_cards (name, code, amount, message, is_redeemed) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, giftCard.getName());
			statement.setString(2, giftCard.getCode());
			statement.setDouble(3, giftCard.getAmount());
			statement.setString(4, giftCard.getMessage());
			statement.setBoolean(5, giftCard.isRedeemed());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
	}

	@Override
	public void updateEGiftCard(EGiftCard giftCard) {
		String sql = "UPDATE egift_cards SET name = ?, code = ?, amount = ?, message = ?, is_redeemed = ? WHERE id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, giftCard.getName());
			statement.setString(2, giftCard.getCode());
			statement.setDouble(3, giftCard.getAmount());
			statement.setString(4, giftCard.getMessage());
			statement.setBoolean(5, giftCard.isRedeemed());
			statement.setInt(6, giftCard.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
	}

	@Override
	public EGiftCard getEGiftCardById(int cardId) {
		String sql = "SELECT * FROM egift_cards WHERE id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, cardId);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return extractEGiftCardFromResultSet(resultSet);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
		return null;
	}

	@Override
	public List<EGiftCard> searchEGiftCards(String keyword) {
		String sql = "SELECT * FROM egift_cards WHERE name LIKE ? OR code LIKE ? OR amount = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, "%" + keyword + "%");
			statement.setString(2, "%" + keyword + "%");
			statement.setString(3, keyword);
			try (ResultSet resultSet = statement.executeQuery()) {
				List<EGiftCard> giftCards = new ArrayList<>();
				while (resultSet.next()) {
					EGiftCard giftCard = extractEGiftCardFromResultSet(resultSet);
					giftCards.add(giftCard);
				}
				return giftCards;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
		return null;
	}

	private EGiftCard extractEGiftCardFromResultSet(ResultSet resultSet) throws SQLException {
		EGiftCard giftCard = new EGiftCard();
		giftCard.setId(resultSet.getInt("id"));
		giftCard.setName(resultSet.getString("name"));
		giftCard.setCode(resultSet.getString("code"));
		giftCard.setAmount(resultSet.getDouble("amount"));
		giftCard.setMessage(resultSet.getString("message"));
		giftCard.setRedeemed(resultSet.getBoolean("is_redeemed"));
		return giftCard;
	}

	@Override
	public void deleteEGiftCard(EGiftCard giftCard) {
		String sql = "DELETE FROM egift_cards WHERE id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, giftCard.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
	}

	@Override
	public void deleteAllEGiftCards() {
		String sql = "DELETE FROM egift_cards";
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
	}

	@Override
	public List<EGiftCard> getAllEGiftCards() {
		List<EGiftCard> eGiftCards = new ArrayList<>();
		String sql = "SELECT id, name, code, amount, message, is_redeemed FROM egift_cards";

		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {

			while (resultSet.next()) {
				EGiftCard eGiftCard = new EGiftCard();
				eGiftCard.setId(resultSet.getInt("id"));
				eGiftCard.setName(resultSet.getString("name"));
				eGiftCard.setCode(resultSet.getString("code"));
				eGiftCard.setAmount(resultSet.getDouble("amount"));
				eGiftCard.setMessage(resultSet.getString("message"));
				eGiftCard.setRedeemed(resultSet.getBoolean("is_redeemed"));
				eGiftCards.add(eGiftCard);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}

		return eGiftCards;
	}
}
