/* 
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Queue;
import java.io.Serializable;
import java.util.Iterator;

public class BankAccount implements Serializable {

	private String accNumber;
	private String accHolderName;
	private String accHolderAddress;
	private Calendar accOpeningDate;
	private double balance;
	// Constant for maximum number of transactions stored in transactionHistory
	private static final int MAX_TRANSACTIONS = 4;
	// Queue to store up to MAX_TRANSACTIONS recent transactions
	private final Queue<Transaction> transactionHistory = new ArrayDeque<>(MAX_TRANSACTIONS);
	private int transactionNo = 1; // Tracks the total number of transactions performed by an account. Starts at 1.

	public BankAccount(String accNumber, String name, String address, int day, int month, int year) {

		// Validate and set account number
		if (isValidAccNumber(accNumber)) {
			this.accNumber = accNumber;
		}

		// Validate and set account holder's name
		if (isValidName(name)) {
			this.accHolderName = name;
		}

		// Validate and set account holder's address
		if (isValidAddress(address)) {
			this.accHolderAddress = address;
		}
		//initialize balance	
		this.balance = 0;

		this.accOpeningDate = Calendar.getInstance();
		if (isValidDate(day, month, year)) {
			accOpeningDate.setLenient(false);// Strict date validation
			this.accOpeningDate.set(year, month - 1, day); //Adjust for zero-indexing of month
		}
	}

	/*
		Getters
	 */
	public String getAccNumber() {
		return accNumber;
	}

	public String getName() {
		return accHolderName;
	}

	public String getAddress() {
		return accHolderAddress;
	}

	public Calendar getOpeningDate() {
		return accOpeningDate;
	}

	public double getBalance() {
		return balance;
	}

	public Queue<Transaction> getTransactions() {
		return transactionHistory;
	}

	/*
		Setters. 
		No accNumber or Opening date seters. There should be no way to modify the accNumber and starting date once an account is created.
	 */
	public void setName(String name) {
		if (isValidName(name)) {
			this.accHolderName = name;
		}
	}

	public void setAddress(String address) {
		if (isValidAddress(address)) {
			this.accHolderAddress = address;
		}
	}

	/*
		Used in transactions that add money to account
	 */
	private void addMoney(double amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be greater than 0.");
		} else {
			balance += amount;
		}
	}

	/*
		Used in transactions that take money from the account
	 */
	private void takeMoney(double amount) {
		if (balance - amount < 0) {
			throw new IllegalArgumentException("Not enough money.");
		} else if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be greater than 0.");
		} else {
			balance -= amount;
		}
	}

	/*
		Adds a transaction to the transaction history and updates balance.
		Maintains a maximum size of transactions defined by MAX_TRANSACTIONS.
	 */
	public void addTransaction(String type, double amount, int day, int month, int year) {
		// Create a new transaction to validate the type, amount, and date
		Transaction newTransaction = new Transaction(type, amount, day, month, year, transactionNo);
		
		// Update balance according to transaction type
		if (newTransaction.getType().equalsIgnoreCase("Deposit")) {
			addMoney(amount);
		} else if (newTransaction.getType().equalsIgnoreCase("Withdrawal")) {
			// Check for sufficient balance before withdrawal
			takeMoney(amount);
		}
		// Maintain the queue to only hold the most recent transactions. Up to MAX_TRANSACTIONS
		if (transactionHistory.size() == MAX_TRANSACTIONS) {
			transactionHistory.poll(); // Remove the oldest transaction
		}
		transactionHistory.offer(newTransaction); // Add the new transaction to the queue
		
		transactionNo++;
		
	}

	/*
		This method returns the account details as a String, to be displayed in a JTextArea
	 */
	public String getAccountGeneralDetails() {
		//Format date as a string
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		String formattedDate = dateFormat.format(accOpeningDate.getTime());

		return "Account Number: " + accNumber + "\n"
			+ "Account Holder Name: " + accHolderName + "\n"
			+ "Account Holder Address: " + accHolderAddress + "\n"
			+ "Account Start Date: " + formattedDate + "\n"
			+ "Current Balance: £" + balance + "\n\n";
	}

	/*
		Returns a String with the sorted transaction history by transaction amount in ascending order.
	 */

	public String getTransactionDetails() {

		String displayMessage = "";
		// Only sort if there are transactions stored in transactionHistory
		if (transactionHistory.isEmpty()) {
			displayMessage = ("No transactions available.");
		} else {
			// Sort transactions
			Queue<Transaction> sortedTransactions = mergeSort(transactionHistory);
			Iterator<Transaction> iterator = sortedTransactions.iterator();

			// Display each transaction
			for (int i = 0; i < sortedTransactions.size(); i++) {
				displayMessage = displayMessage.concat(iterator.next().getTransactionDetails());
			}
		}
		return displayMessage;
	}

	/*
		Helper method to validate Account holder name
	 */
	private boolean isValidName(String name) {
		//Default false;
		boolean isValid = false;
		if (!"".equals(name)) {
			isValid = true;
		} else {
			throw new IllegalArgumentException("Please insert a name");
		}
		if (containsSpecialCharacter(name)) {
			throw new IllegalArgumentException("Name cannot contain any of these special characters: '[<>:\"/\\\\|?*#\\[\\]]'");
		}

		return isValid;
	}

	/*
		Helper method to validate account number
	 */
	private boolean isValidAccNumber(String accNumber) {
		//Default false
		boolean isValid = false;
		if (accNumber.length() == 8) {
			try {
				Integer.valueOf(accNumber); //Verify that accNumber is comprised only of digits
				isValid = true;
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Account number must contain only digits.");
			}
		} else {
			throw new IllegalArgumentException("Account number must be 8 digits long.");
		}
		return isValid;
	}

	/*
		Helper method to validate address
	 */
	private boolean isValidAddress(String address) {
		//Default false
		boolean isValid = false;
		if (!address.equals("")) {
			isValid = true;
		} else {
			throw new IllegalArgumentException("Please insert an address");
		}
		return isValid;
	}

	/*
		Helper method to validate a new date
	 */
	private boolean isValidDate(int day, int month, int year) {
		boolean isValid = false;
		try {
			Calendar tempDate = Calendar.getInstance();
			tempDate.setLenient(false); //Strict date validation

			// Set the date and immediately validate by calling getTime()
			tempDate.set(year, month - 1, day); //Adjust for zero-indexing of month
			tempDate.getTime(); // Triggers validation if the date is invalid
			isValid = true;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid opening account date");
		}
		return isValid;
	}

	/*
		Helper method to check whether a string contains the specified special characters
	 */
	private boolean containsSpecialCharacter(String input) {
		Pattern notAllowed = Pattern.compile("[<>:\"/\\\\|?*#\\[\\]]");
		Matcher matcher = notAllowed.matcher(input);
		return matcher.find(); // Returns false if none of the specified specialCharacters were found.
	}

	/*
		Recursive merge sort for sorting transactions by amount
	 */
	private Queue<Transaction> mergeSort(Queue<Transaction> queue) {
		int queueSize = queue.size();
		// Only proceed if there is more than one transaction to sort
		if (queueSize > 1) {
			int midpoint = queueSize / 2;

			// Divide queue into left and right halves without modifying the original
			Queue<Transaction> leftQueue = new ArrayDeque<>();
			Queue<Transaction> rightQueue = new ArrayDeque<>();

			Iterator<Transaction> iterator = queue.iterator();
			for (int i = 0; i < midpoint; i++) {
				leftQueue.add(iterator.next());
			}
			while (iterator.hasNext()) {
				rightQueue.add(iterator.next());
			}

			// Recursively split and sort each half
			leftQueue = mergeSort(leftQueue);
			rightQueue = mergeSort(rightQueue);

			// Merge sorted halves
			return merge(leftQueue, rightQueue);
		}
		return new ArrayDeque<>(queue); // Return a new queue if there is only one element
	}

	/*
		Helper method for use with mergeSort.
		Merges two sorted Queues into a single sorted Queue by transaction amount.
	 */
	private Queue<Transaction> merge(Queue<Transaction> leftQueue, Queue<Transaction> rightQueue) {
		Queue<Transaction> sortedQueue = new ArrayDeque<>();

		// Compare and merge elements from both queues until one is exhausted
		while (!leftQueue.isEmpty() && !rightQueue.isEmpty()) {
			if (leftQueue.peek().getAmount() < rightQueue.peek().getAmount()) {
				sortedQueue.add(leftQueue.poll()); // Add and remove from leftQueue
			} else {
				sortedQueue.add(rightQueue.poll()); // Add and remove from rightQueue
			}
		}

		// Add remaining elements from leftQueue, if any
		while (!leftQueue.isEmpty()) {
			sortedQueue.add(leftQueue.poll());
		}

		// Add remaining elements from rightQueue, if any
		while (!rightQueue.isEmpty()) {
			sortedQueue.add(rightQueue.poll());
		}

		return sortedQueue;
	}
}
