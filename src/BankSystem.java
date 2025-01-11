/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import myHashMap.MyHashMap;
import java.io.Serializable;

public class BankSystem implements Serializable {

	private static volatile BankSystem instance;
	private MyHashMap<String, BankAccount> activeAccounts;

	private BankSystem() {
		activeAccounts = new MyHashMap<>();
	}

	public static BankSystem getInstance() {
		BankSystem result = instance;
		if (result == null) {
			synchronized (BankSystem.class) {
				if (result == null) {
					result = new BankSystem();
				}
			}
		}
		return result;
	}

	/*
		Getter for activeAccounts
	 */
	public MyHashMap<String, BankAccount> getActiveAccounts() {
		return activeAccounts;
	}

	/*
		Creates an account with given parameters and addts it to activeAccounts
	 */
	public void createAccount(String accNo, String name, String address, int day, int month, int year) {
		//Check if given account is already in aciveAccouts
		if (!activeAccounts.containsKey(accNo)) {
			activeAccounts.put(accNo, new BankAccount(accNo, name, address, day, month, year));
		} else {
			throw new IllegalArgumentException("An account with this account number already exists");
		}

	}

	/*
		Adds a transaction with given parameters to the specified bank account
	 */
	public void addTransaction(String accNo, String type, double amount, int day, int month, int year) {
		if (activeAccounts.containsKey(accNo)) {
			activeAccounts.get(accNo).addTransaction(type, amount, day, month, year);
		} else {
			throw new IllegalArgumentException("There is no account with this account number.");
		}
	}

	/*
		Deletes the specified account from activeAccounts
	 */
	public void deleteAccount(String accNo) {
		if (activeAccounts.containsKey(accNo)) {
			activeAccounts.remove(accNo);
		} else {
			throw new IllegalArgumentException("There is no account with this accNo.");
		}
	}

	/*
		Displays all transactions stored in the specified account 
	 */
	public String getAccountTransactions(String accNo) {
		String displayMessage = "";
		if (activeAccounts.isEmpty()) {
			displayMessage = "No accounts available";
		} else {
			if (activeAccounts.containsKey(accNo)) {
				// Retrieve the specified account's stored transactions sorted by amount.
				displayMessage = activeAccounts.get(accNo).getTransactionDetails();
			}
		}
		return displayMessage;
	}

	/*
		Displays all accounts account details.
	 */
	public String getAllAccountDetails() {
		String displayMessage = "";
		if (activeAccounts.isEmpty()) {
			displayMessage = "No accounts in the system.";
		} else {
			for (BankAccount account : activeAccounts.values()) {
				// Call the current account's display method and concats its output to displayMessage.
				displayMessage = displayMessage.concat(account.getAccountGeneralDetails());
			}
		}
		return displayMessage;
	}
}
