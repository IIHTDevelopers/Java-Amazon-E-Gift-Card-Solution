package com.amazonegiftcardapplication.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonegiftcardapplication.model.EGiftCard;
import com.amazonegiftcardapplication.model.Payment;

public class PaymentDAOImpl implements PaymentDAO {

	private Connection connection;

	public PaymentDAOImpl(Connection connection) {
		this.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}

	@Override
	public void createPayment(Payment payment) {
		String sql = "INSERT INTO payments (user_id, card_id, payment_method) VALUES (?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, payment.getUserId());
			statement.setInt(2, payment.getCardId());
			statement.setString(3, payment.getPaymentMethod());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
	}

	@Override
	public void updatePayment(Payment payment) {
		String sql = "UPDATE payments SET user_id = ?, card_id = ?, payment_method = ? WHERE id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, payment.getUserId());
			statement.setInt(2, payment.getCardId());
			statement.setString(3, payment.getPaymentMethod());
			statement.setInt(4, payment.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
	}

	@Override
	public Payment getPaymentById(int paymentId) {
		String sql = "SELECT * FROM payments WHERE id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, paymentId);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return extractPaymentFromResultSet(resultSet);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
		return null;
	}

	@Override
	public List<Payment> getAllPayments() {
		String sql = "SELECT * FROM payments";
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
			List<Payment> payments = new ArrayList<>();
			while (resultSet.next()) {
				Payment payment = extractPaymentFromResultSet(resultSet);
				payments.add(payment);
			}
			return payments;
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
		return null;
	}

	private Payment extractPaymentFromResultSet(ResultSet resultSet) throws SQLException {
		Payment payment = new Payment();
		payment.setId(resultSet.getInt("id"));
		payment.setUserId(resultSet.getInt("user_id"));
		payment.setCardId(resultSet.getInt("card_id"));
		payment.setPaymentMethod(resultSet.getString("payment_method"));
		return payment;
	}

	@Override
	public void deletePayment(Payment payment) {
		String sql = "DELETE FROM payments WHERE id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, payment.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
	}

	@Override
	public void deleteAllPayments() {
		String sql = "DELETE FROM payments";
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
	}

	@Override
	public List<EGiftCard> getGiftCardsGroupedByAmount() {
		List<EGiftCard> giftCards = new ArrayList<>();
		String sql = "SELECT id, name, code, amount, message, is_redeemed FROM egift_cards ORDER BY amount";

		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {

			Map<Double, List<EGiftCard>> giftCardsByAmount = new HashMap<>();

			while (resultSet.next()) {
				EGiftCard giftCard = new EGiftCard();
				giftCard.setId(resultSet.getInt("id"));
				giftCard.setName(resultSet.getString("name"));
				giftCard.setCode(resultSet.getString("code"));
				giftCard.setAmount(resultSet.getDouble("amount"));
				giftCard.setMessage(resultSet.getString("message"));
				giftCard.setRedeemed(resultSet.getBoolean("is_redeemed"));

				double amount = giftCard.getAmount();
				if (!giftCardsByAmount.containsKey(amount)) {
					giftCardsByAmount.put(amount, new ArrayList<>());
				}
				giftCardsByAmount.get(amount).add(giftCard);
			}

			for (List<EGiftCard> groupedGiftCards : giftCardsByAmount.values()) {
				giftCards.addAll(groupedGiftCards);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}

		return giftCards;
	}
}
