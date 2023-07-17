package com.amazonegiftcardapplication;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import com.amazonegiftcardapplication.model.EGiftCard;
import com.amazonegiftcardapplication.model.Payment;
import com.amazonegiftcardapplication.model.User;
import com.amazonegiftcardapplication.repository.EGiftCardDAO;
import com.amazonegiftcardapplication.repository.EGiftCardDAOImpl;
import com.amazonegiftcardapplication.repository.PaymentDAO;
import com.amazonegiftcardapplication.repository.PaymentDAOImpl;
import com.amazonegiftcardapplication.repository.UserDAO;
import com.amazonegiftcardapplication.repository.UserDAOImpl;

public class EGiftCardApplication {

	private static final String DB_PROPERTIES_FILE = "application.properties";
	private static final String DB_URL_PROPERTY = "db.url";
	private static final String DB_USERNAME_PROPERTY = "db.username";
	private static final String DB_PASSWORD_PROPERTY = "db.password";

	private UserDAO userDAO;
	private EGiftCardDAO giftCardDAO;
	private PaymentDAO paymentDAO;
	private Connection connection;

	private Scanner scanner;

	public EGiftCardApplication() {
		scanner = new Scanner(System.in);
		initializeDAO();
		createDatabaseIfNotExists();
		createTableIfNotExists();
	}

	private void initializeDAO() {
		try {
			Properties properties = loadProperties(DB_PROPERTIES_FILE);

			String url = properties.getProperty(DB_URL_PROPERTY);
			String username = properties.getProperty(DB_USERNAME_PROPERTY);
			String password = properties.getProperty(DB_PASSWORD_PROPERTY);

			connection = DriverManager.getConnection(url, username, password);

			userDAO = new UserDAOImpl(connection);
			giftCardDAO = new EGiftCardDAOImpl(connection);
			paymentDAO = new PaymentDAOImpl(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return this.connection;
	}

	private Properties loadProperties(String fileName) {
		Properties properties = new Properties();
		try (InputStream inputStream = EGiftCardApplication.class.getClassLoader().getResourceAsStream(fileName)) {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	private void createDatabaseIfNotExists() {
		Properties properties = loadProperties(DB_PROPERTIES_FILE);
		String url = properties.getProperty(DB_URL_PROPERTY);
		String username = properties.getProperty(DB_USERNAME_PROPERTY);
		String password = properties.getProperty(DB_PASSWORD_PROPERTY);

		try (Connection connection = DriverManager.getConnection(url, username, password)) {
			DatabaseMetaData metadata = connection.getMetaData();
			String databaseName = getDatabaseNameFromURL(url);

			ResultSet resultSet = metadata.getCatalogs();
			boolean databaseExists = false;

			while (resultSet.next()) {
				String existingDatabase = resultSet.getString(1);
				if (existingDatabase.equalsIgnoreCase(databaseName)) {
					databaseExists = true;
					break;
				}
			}

			if (!databaseExists) {
				Statement statement = connection.createStatement();
				statement.executeUpdate("CREATE DATABASE " + databaseName);
				System.out.println("Database created: " + databaseName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
	}

	private String getDatabaseNameFromURL(String url) {
		int lastSlashIndex = url.lastIndexOf("/");
		int lastQuestionMarkIndex = url.lastIndexOf("?");

		if (lastQuestionMarkIndex == -1) {
			return url.substring(lastSlashIndex + 1);
		} else {
			return url.substring(lastSlashIndex + 1, lastQuestionMarkIndex);
		}
	}

	private void createTableIfNotExists() {
		try {
			// Create users table if not exists
			String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), age INT, gender VARCHAR(10))";
			Statement statement = userDAO.getConnection().createStatement();
			statement.executeUpdate(createUsersTableSQL);

			// Create egift_cards table if not exists
			String createEGiftCardsTableSQL = "CREATE TABLE IF NOT EXISTS egift_cards (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), code VARCHAR(255), amount DOUBLE, message VARCHAR(255), is_redeemed BOOLEAN)";
			statement = giftCardDAO.getConnection().createStatement();
			statement.executeUpdate(createEGiftCardsTableSQL);

			// Create payments table if not exists
			String createPaymentsTableSQL = "CREATE TABLE IF NOT EXISTS payments (id INT PRIMARY KEY AUTO_INCREMENT, user_id INT, card_id INT, payment_method VARCHAR(255))";
			statement = paymentDAO.getConnection().createStatement();
			statement.executeUpdate(createPaymentsTableSQL);

			System.out.println("Tables created if not exists.");
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exception
		}
	}

	private void showOptions() {
		System.out.println("Please select an option:");
		System.out.println("1. Create a new user");
		System.out.println("2. Update an existing user");
		System.out.println("3. Delete a user");
		System.out.println("4. Get user details by ID");
		System.out.println("5. Get all users");
		System.out.println("6. Create a new eGift Card");
		System.out.println("7. Update an existing eGift Card");
		System.out.println("8. Delete an eGift Card");
		System.out.println("9. Get eGift Card details by ID");
		System.out.println("10. Search eGift Cards By Name, Code or Amount");
		System.out.println("11. Create a new payment");
		System.out.println("12. Update an existing payment");
		System.out.println("13. Delete a payment");
		System.out.println("14. Get payment details by ID");
		System.out.println("15. Get all payments");
		System.out.println("16. Get suggestions for eGift cards based on user's last purchase");
		System.out.println("17. Show list of purchased gift cards grouped by amount");
		System.out.println("18. Get list of all shared gift cards shared by user");
		System.out.println("19. Get percentage of redeemed gift cards shared by user");
		System.out.println("0. Exit");
	}

	public void start() {
		boolean exit = false;
		while (!exit) {
			showOptions();
			int option = scanner.nextInt();
			scanner.nextLine(); // Consume newline character

			switch (option) {
			case 1:
				createUser();
				break;
			case 2:
				updateUser();
				break;
			case 3:
				deleteUser();
				break;
			case 4:
				getUserDetails();
				break;
			case 5:
				getAllUsers();
				break;
			case 6:
				createEGiftCard();
				break;
			case 7:
				updateEGiftCard();
				break;
			case 8:
				deleteEGiftCard();
				break;
			case 9:
				getEGiftCardDetails();
				break;
			case 10:
				searchEGiftCards();
				break;
			case 11:
				createPayment();
				break;
			case 12:
				updatePayment();
				break;
			case 13:
				deletePayment();
				break;
			case 14:
				getPaymentDetails();
				break;
			case 15:
				getAllPayments();
				break;
			case 16:
				getEGiftCardSuggestions();
				break;
			case 17:
				showGiftCardsGroupedByAmount();
				break;
			case 18:
				showSharedGiftCardsByUser();
				break;
			case 19:
				showRedeemedGiftCardPercentage();
				break;
			case 0:
				exit = true;
				break;
			default:
				System.out.println("Invalid option! Please try again.");
				break;
			}

			System.out.println();
		}
	}

	private void createUser() {
		System.out.println("Enter user name:");
		String name = scanner.nextLine();

		System.out.println("Enter user age:");
		int age = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		System.out.println("Enter user gender:");
		String gender = scanner.nextLine();

		User user = new User();
		user.setName(name);
		user.setAge(age);
		user.setGender(gender);

		userDAO.createUser(user);

		System.out.println("User created successfully.");
	}

	private void updateUser() {
		System.out.println("Enter user ID to update:");
		int userId = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		User user = userDAO.getUserById(userId);
		if (user == null) {
			System.out.println("User not found with ID: " + userId);
			return;
		}

		System.out.println("Enter new user name:");
		String name = scanner.nextLine();

		System.out.println("Enter new user age:");
		int age = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		System.out.println("Enter new user gender:");
		String gender = scanner.nextLine();

		user.setName(name);
		user.setAge(age);
		user.setGender(gender);

		userDAO.updateUser(user);

		System.out.println("User updated successfully.");
	}

	private void deleteUser() {
		System.out.println("Enter user ID to delete:");
		int userId = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		User user = userDAO.getUserById(userId);
		if (user == null) {
			System.out.println("User not found with ID: " + userId);
			return;
		}

		userDAO.deleteUser(user);

		System.out.println("User deleted successfully.");
	}

	private void getUserDetails() {
		System.out.println("Enter user ID to get details:");
		int userId = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		User user = userDAO.getUserById(userId);
		if (user == null) {
			System.out.println("User not found with ID: " + userId);
			return;
		}

		System.out.println("User ID: " + user.getId());
		System.out.println("Name: " + user.getName());
		System.out.println("Age: " + user.getAge());
		System.out.println("Gender: " + user.getGender());
	}

	private void getAllUsers() {
		List<User> users = userDAO.getAllUsers();

		if (users == null || users.isEmpty()) {
			System.out.println("No users found.");
			return;
		}

		for (User user : users) {
			System.out.println("User ID: " + user.getId());
			System.out.println("Name: " + user.getName());
			System.out.println("Age: " + user.getAge());
			System.out.println("Gender: " + user.getGender());
			System.out.println("------------------------");
		}
	}

	private void createEGiftCard() {
		System.out.println("Enter eGift Card name:");
		String name = scanner.nextLine();

		System.out.println("Enter eGift Card code:");
		String code = scanner.nextLine();

		System.out.println("Enter eGift Card amount:");
		double amount = scanner.nextDouble();
		scanner.nextLine(); // Consume newline character

		System.out.println("Enter eGift Card message:");
		String message = scanner.nextLine();

		System.out.println("Is eGift Card redeemed? (true/false):");
		boolean isRedeemed = scanner.nextBoolean();
		scanner.nextLine(); // Consume newline character

		EGiftCard giftCard = new EGiftCard();
		giftCard.setName(name);
		giftCard.setCode(code);
		giftCard.setAmount(amount);
		giftCard.setMessage(message);
		giftCard.setRedeemed(isRedeemed);

		giftCardDAO.createEGiftCard(giftCard);

		System.out.println("eGift Card created successfully.");
	}

	private void updateEGiftCard() {
		System.out.println("Enter eGift Card ID to update:");
		int cardId = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		EGiftCard giftCard = giftCardDAO.getEGiftCardById(cardId);
		if (giftCard == null) {
			System.out.println("eGift Card not found with ID: " + cardId);
			return;
		}

		System.out.println("Enter new eGift Card name:");
		String name = scanner.nextLine();

		System.out.println("Enter new eGift Card code:");
		String code = scanner.nextLine();

		System.out.println("Enter new eGift Card amount:");
		double amount = scanner.nextDouble();
		scanner.nextLine(); // Consume newline character

		System.out.println("Enter new eGift Card message:");
		String message = scanner.nextLine();

		System.out.println("Is new eGift Card redeemed? (true/false):");
		boolean isRedeemed = scanner.nextBoolean();
		scanner.nextLine(); // Consume newline character

		giftCard.setName(name);
		giftCard.setCode(code);
		giftCard.setAmount(amount);
		giftCard.setMessage(message);
		giftCard.setRedeemed(isRedeemed);

		giftCardDAO.updateEGiftCard(giftCard);

		System.out.println("eGift Card updated successfully.");
	}

	private void deleteEGiftCard() {
		System.out.println("Enter eGift Card ID to delete:");
		int cardId = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		EGiftCard giftCard = giftCardDAO.getEGiftCardById(cardId);
		if (giftCard == null) {
			System.out.println("eGift Card not found with ID: " + cardId);
			return;
		}

		giftCardDAO.deleteEGiftCard(giftCard);

		System.out.println("eGift Card deleted successfully.");
	}

	private void getEGiftCardDetails() {
		System.out.println("Enter eGift Card ID to get details:");
		int cardId = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		EGiftCard giftCard = giftCardDAO.getEGiftCardById(cardId);
		if (giftCard == null) {
			System.out.println("eGift Card not found with ID: " + cardId);
			return;
		}

		System.out.println("eGift Card ID: " + giftCard.getId());
		System.out.println("Name: " + giftCard.getName());
		System.out.println("Code: " + giftCard.getCode());
		System.out.println("Amount: " + giftCard.getAmount());
		System.out.println("Message: " + giftCard.getMessage());
		System.out.println("Redeemed: " + giftCard.isRedeemed());
	}

	private void searchEGiftCards() {
		System.out.println("Enter search keyword:");
		String keyword = scanner.nextLine();

		List<EGiftCard> giftCards = giftCardDAO.searchEGiftCards(keyword);

		if (giftCards == null || giftCards.isEmpty()) {
			System.out.println("No eGift Cards found.");
			return;
		}

		for (EGiftCard giftCard : giftCards) {
			System.out.println("eGift Card ID: " + giftCard.getId());
			System.out.println("Name: " + giftCard.getName());
			System.out.println("Code: " + giftCard.getCode());
			System.out.println("Amount: " + giftCard.getAmount());
			System.out.println("Message: " + giftCard.getMessage());
			System.out.println("Redeemed: " + giftCard.isRedeemed());
			System.out.println("------------------------");
		}
	}

	private void createPayment() {
		System.out.println("Enter user ID for the payment:");
		int userId = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		System.out.println("Enter eGift Card ID for the payment:");
		int cardId = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		System.out.println("Enter payment method:");
		String paymentMethod = scanner.nextLine();

		Payment payment = new Payment();
		payment.setUserId(userId);
		payment.setCardId(cardId);
		payment.setPaymentMethod(paymentMethod);

		paymentDAO.createPayment(payment);

		System.out.println("Payment created successfully.");
	}

	private void updatePayment() {
		System.out.println("Enter payment ID to update:");
		int paymentId = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		Payment payment = paymentDAO.getPaymentById(paymentId);
		if (payment == null) {
			System.out.println("Payment not found with ID: " + paymentId);
			return;
		}

		System.out.println("Enter new user ID for the payment:");
		int userId = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		System.out.println("Enter new eGift Card ID for the payment:");
		int cardId = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		System.out.println("Enter new payment method:");
		String paymentMethod = scanner.nextLine();

		payment.setUserId(userId);
		payment.setCardId(cardId);
		payment.setPaymentMethod(paymentMethod);

		paymentDAO.updatePayment(payment);

		System.out.println("Payment updated successfully.");
	}

	private void deletePayment() {
		System.out.println("Enter payment ID to delete:");
		int paymentId = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		Payment payment = paymentDAO.getPaymentById(paymentId);
		if (payment == null) {
			System.out.println("Payment not found with ID: " + paymentId);
			return;
		}

		paymentDAO.deletePayment(payment);

		System.out.println("Payment deleted successfully.");
	}

	private void getPaymentDetails() {
		System.out.println("Enter payment ID to get details:");
		int paymentId = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		Payment payment = paymentDAO.getPaymentById(paymentId);
		if (payment == null) {
			System.out.println("Payment not found with ID: " + paymentId);
			return;
		}

		System.out.println("Payment ID: " + payment.getId());
		System.out.println("User ID: " + payment.getUserId());
		System.out.println("eGift Card ID: " + payment.getCardId());
		System.out.println("Payment Method: " + payment.getPaymentMethod());
	}

	private void getAllPayments() {
		List<Payment> payments = paymentDAO.getAllPayments();

		if (payments == null || payments.isEmpty()) {
			System.out.println("No payments found.");
			return;
		}

		for (Payment payment : payments) {
			System.out.println("Payment ID: " + payment.getId());
			System.out.println("User ID: " + payment.getUserId());
			System.out.println("eGift Card ID: " + payment.getCardId());
			System.out.println("Payment Method: " + payment.getPaymentMethod());
			System.out.println("------------------------");
		}
	}

	private void getEGiftCardSuggestions() {
		System.out.println("Enter user ID to get suggestions:");
		int userId = scanner.nextInt();
		scanner.nextLine();

		List<EGiftCard> suggestions = userDAO.getSuggestionsForUser(userId);

		if (suggestions.isEmpty()) {
			System.out.println("No eGift card suggestions found for user ID: " + userId);
			return;
		}

		System.out.println("eGift Card Suggestions:");
		for (EGiftCard giftCard : suggestions) {
			System.out.println("ID: " + giftCard.getId());
			System.out.println("Name: " + giftCard.getName());
			System.out.println("Code: " + giftCard.getCode());
			System.out.println("Amount: " + giftCard.getAmount());
			System.out.println("Message: " + giftCard.getMessage());
			System.out.println("Is Redeemed: " + giftCard.isRedeemed());
			System.out.println("------------------------");
		}
	}

	private void showGiftCardsGroupedByAmount() {
		List<EGiftCard> giftCards = paymentDAO.getGiftCardsGroupedByAmount();

		if (giftCards.isEmpty()) {
			System.out.println("No gift cards found.");
			return;
		}

		System.out.println("Gift Cards Grouped by Amount:");
		double currentAmount = giftCards.get(0).getAmount();
		System.out.println("Amount: " + currentAmount);
		for (EGiftCard giftCard : giftCards) {
			if (giftCard.getAmount() != currentAmount) {
				System.out.println();
				System.out.println("Amount: " + giftCard.getAmount());
				currentAmount = giftCard.getAmount();
			}
			System.out.println("ID: " + giftCard.getId());
			System.out.println("Name: " + giftCard.getName());
			System.out.println("Code: " + giftCard.getCode());
			System.out.println("Message: " + giftCard.getMessage());
			System.out.println("Is Redeemed: " + giftCard.isRedeemed());
			System.out.println("------------------------");
		}
	}

	public static void main(String[] args) {
		EGiftCardApplication main = new EGiftCardApplication();
		main.start();
	}

	private void showSharedGiftCardsByUser() {
		System.out.println("Enter user ID to get shared gift cards:");
		int userId = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		List<EGiftCard> sharedGiftCards = userDAO.getSharedGiftCardsByUser(userId);

		if (sharedGiftCards.isEmpty()) {
			System.out.println("No shared gift cards found for user ID: " + userId);
			return;
		}

		System.out.println("Shared Gift Cards for User ID " + userId + ":");
		for (EGiftCard giftCard : sharedGiftCards) {
			System.out.println("ID: " + giftCard.getId());
			System.out.println("Name: " + giftCard.getName());
			System.out.println("Code: " + giftCard.getCode());
			System.out.println("Amount: " + giftCard.getAmount());
			System.out.println("Message: " + giftCard.getMessage());
			System.out.println("Is Redeemed: " + giftCard.isRedeemed());
			System.out.println("------------------------");
		}
	}

	private void showRedeemedGiftCardPercentage() {
		System.out.println("Enter user ID to get redeemed gift card percentage:");
		int userId = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		double percentage = userDAO.getRedeemedGiftCardPercentage(userId);

		if (percentage == 0.0) {
			System.out.println("No redeemed gift cards found for user ID: " + userId);
		} else {
			System.out.println("Redeemed Gift Card Percentage for User ID " + userId + ": " + percentage + "%");
		}
	}
}
