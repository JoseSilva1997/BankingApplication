/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.time.Month;
import java.time.Year;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class GUI implements ActionListener {

	private BankSystem accountHistory;
	// Components for the main frame
	private JFrame mainFrame;
	private JTextArea accountsArea;
	private JButton addAccountButton, deleteAccountButton, transactionsButton;

	// Components of the JMenuBar
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem saveButton, loadButton;
	
	// Components for the add account frame
	private JFrame addAccountFrame;
	private JTextField accountNumberField, accountHolderNameField, addressField;
	private JComboBox<String> dayComboBox, monthComboBox, yearComboBox;
	private JButton addAddButton, addClearButton;

	// Components for the transaction frame
	private JFrame transactionFrame;
	private JTextArea transactionTextArea;
	private JTextField transactionAmountField;
	private JComboBox<String> transactionTypeComboBox, transactionAccountComboBox, transactionDayComboBox, transactionMonthComboBox, transactionYearComboBox;
	private JButton transactionAddButton, transactionClearButton;

	// Components for the delete account frame
	private JFrame deleteAccountFrame;
	private JComboBox<String> deleteAccountComboBox;
	private JButton deleteButton;

	public static void main(String[] args) {
		GUI gui = new GUI();
		gui.displayAllAccounts();
	}

	public GUI() {
		accountHistory = BankSystem.getInstance();
		mainFrame();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();

		if (command.equals("Add account")) {
			addAccountFrame();
		}
		if (command.equals("Transactions")) {
			transactionFrame();
			displayTransactions(); // Populate transactions frame with available transaction for the currently selected BankAccount
		}
		if (command.equals("Delete account")) {
			deleteAccountFrame();
			displayAllAccounts(); //Refresh the main frame to display the updated list of accounts
		}
		if (command.equals("Delete")) {
			deleteSelectedAccount();
		}
		// Check if command "Add" came from "addAddButton"
		if (command.equals("Add") && addAddButton != null && addAddButton.equals(event.getSource())) {
			addAccount();
			displayAllAccounts(); //Refresh the main frame to display the newly added account
		}
		// Check if command "Clear" came from "addClearButton"
		if (command.equals("Clear") && addClearButton != null && addClearButton.equals(event.getSource())) {
			addAccountClearFields();
		}
		// Check if command "Add" came from "transactionAddButton"
		if (command.equals("Add") && transactionAddButton != null && transactionAddButton.equals(event.getSource())) {
			addTransaction();
			displayTransactions();// Refresh transactions shown in transactions frame
			displayAllAccounts(); //Refresh the main frame to display updated account balance
		}
		// Check if command "Clear" came from "transactionClearButton"
		if (command.equals("Clear") && transactionClearButton != null && transactionClearButton.equals(event.getSource())) {
			addTransactionClearFields();
		}
		if (command.equals("Save")) {
			saveFile();
		}
		if (command.equals("Load")) {
			openFile();
		}
	}

	/*
		Adds a new account to the database with parameters given by the user in the GUI
	 */
	public void addAccount() {
		//Retrieve user inputs from GUI
		String accNo = accountNumberField.getText();
		String accName = accountHolderNameField.getText();
		String accAddress = addressField.getText();
		int day = Integer.parseInt((String) dayComboBox.getSelectedItem());
		int month = monthToNumber((String) monthComboBox.getSelectedItem());
		int year = Integer.parseInt((String) yearComboBox.getSelectedItem());

		try {
			//Create an account with given user inputs and add it to the database
			accountHistory.createAccount(accNo, accName, accAddress, day, month, year);
			addAccountClearFields(); // Reset all fields
			JOptionPane.showMessageDialog(addAccountFrame, "Account created successfully!", "Success!", JOptionPane.INFORMATION_MESSAGE);
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(addAccountFrame, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	/*
		Adds a transaction with the parameters input by the user into the selected account
	 */
	public void addTransaction() {
		String selectedAccNumber = (String) transactionAccountComboBox.getSelectedItem();
		if (selectedAccNumber != null) {
			//Retrieve user inputs
			String type = (String) transactionTypeComboBox.getSelectedItem();
			int day = Integer.parseInt((String) transactionDayComboBox.getSelectedItem());
			int month = monthToNumber((String) transactionMonthComboBox.getSelectedItem());
			int year = Integer.parseInt((String) transactionYearComboBox.getSelectedItem());
			double amount;
			try {
				amount = Double.parseDouble(transactionAmountField.getText());
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(transactionFrame, "Please insert a number for the amount.", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			//Add transaction to selected account
			try {
				accountHistory.addTransaction(selectedAccNumber, type, amount, day, month, year);
				addTransactionClearFields();//Reset all fields
				JOptionPane.showMessageDialog(transactionFrame, "Transaction added successfully!", "Success!", JOptionPane.INFORMATION_MESSAGE);
			} catch (IllegalArgumentException e) {
				JOptionPane.showMessageDialog(transactionFrame, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(transactionFrame, "No account selected.", "ERROR", JOptionPane.ERROR_MESSAGE);
		}

		displayTransactions();
	}

	/*
		Deletes the account with the selected account number from the database
	 */
	public void deleteSelectedAccount() {
		String selectedAccNumber = (String) deleteAccountComboBox.getSelectedItem();
		if (selectedAccNumber != null) {
			int userConfirmation = JOptionPane.showConfirmDialog(deleteAccountFrame, "Are you sure you want to delete this account?");

			if (userConfirmation == 0) {
				// Remove the account from the database
				accountHistory.deleteAccount(selectedAccNumber);

				// Refresh the dropdown after deletion
				populateAccountComboBox(deleteAccountComboBox);

				// Update accounts in main frame
				displayAllAccounts();

				JOptionPane.showMessageDialog(deleteAccountFrame, "Account " + selectedAccNumber + " deleted successfully.");
			}
		} else {
			JOptionPane.showMessageDialog(deleteAccountFrame, "No account selected.", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	/*
		Retrieves transactions to be displayed from currently selected account number and
		displays them in transactionTextArea
	*/
	public void displayTransactions() {
		// Clear the text area before displaying new transactions
		transactionTextArea.setText("");

		//Get the selected accont
		String selectedAccount = (String) transactionAccountComboBox.getSelectedItem();
		//Add a header with the current account being looked at if an account is found
		if (!accountHistory.getActiveAccounts().isEmpty()) {
			transactionTextArea.append("Transactions for acc no " + selectedAccount + " ordered by amount:\n");
		}
		transactionTextArea.append(accountHistory.getAccountTransactions(selectedAccount));
	}

	/*
		Method to initialize the main frame
	 */
	private void mainFrame() {
		mainFrame = new JFrame("Account Management");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(800, 500);
		mainFrame.setLayout(new BorderLayout());
		
		// MenuBar, menu, and save and open file options
		menuBar = new JMenuBar();
		menu = new JMenu("File");
		menuBar.add(menu);
		
		saveButton = new JMenuItem("Save");
		loadButton = new JMenuItem("Load");
		menu.add(saveButton);
		menu.add(loadButton);
		
		mainFrame.setJMenuBar(menuBar);
		
		// Title label
		JLabel titleLabel = new JLabel("Currently available accounts:");
		titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainFrame.add(titleLabel, BorderLayout.NORTH);

		// Text area to display accounts
		accountsArea = new JTextArea();
		accountsArea.setEditable(false);
		accountsArea.setBackground(new Color(200, 190, 190));  // Light gray background
		JScrollPane scrollPane = new JScrollPane(accountsArea);
		mainFrame.add(scrollPane, BorderLayout.CENTER);

		// Panel with buttons
		JPanel buttonPanel = new JPanel(new FlowLayout());
		addAccountButton = new JButton("Add account");
		deleteAccountButton = new JButton("Delete account");
		transactionsButton = new JButton("Transactions");

		// Add buttons to panel
		buttonPanel.add(addAccountButton);
		buttonPanel.add(transactionsButton);
		buttonPanel.add(deleteAccountButton);
		mainFrame.add(buttonPanel, BorderLayout.SOUTH);

		// Set up action listeners
		addAccountButton.addActionListener(this);
		transactionsButton.addActionListener(this);
		deleteAccountButton.addActionListener(this);
		saveButton.addActionListener(this);
		loadButton.addActionListener(this);

		mainFrame.setVisible(true);
	}

	/*
		Method to initialise and open the "Add Account" frame
	 */
	private void addAccountFrame() {
    // Allow only 1 addAccountFrame at a time
    if (addAccountFrame != null) {
        addAccountFrame.dispose();
    }

    addAccountFrame = new JFrame("Add a new account");
    addAccountFrame.setSize(400, 300);
    addAccountFrame.setLayout(new GridBagLayout());
    addAccountFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    // Account number field
    gbc.gridx = 0;
    gbc.gridy = 0;
    addAccountFrame.add(new JLabel("Account number:"), gbc);
    gbc.gridx = 1;
    accountNumberField = new JTextField(20);
    addAccountFrame.add(accountNumberField, gbc);

    // Account holder name field
    gbc.gridx = 0;
    gbc.gridy = 1;
    addAccountFrame.add(new JLabel("Account holder name:"), gbc);
    gbc.gridx = 1;
    accountHolderNameField = new JTextField(20);
    addAccountFrame.add(accountHolderNameField, gbc);

    // Address field
    gbc.gridx = 0;
    gbc.gridy = 2;
    addAccountFrame.add(new JLabel("Address:"), gbc);
    gbc.gridx = 1;
    addressField = new JTextField(20);
    addAccountFrame.add(addressField, gbc);

    // Start date field
    gbc.gridx = 0;
    gbc.gridy = 3;
    addAccountFrame.add(new JLabel("Account opening date:"), gbc);
    gbc.gridx = 1;
    JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    dayComboBox = new JComboBox<>();
    monthComboBox = new JComboBox<>();
    yearComboBox = new JComboBox<>();
    datePanel.add(dayComboBox);
    datePanel.add(monthComboBox);
    datePanel.add(yearComboBox);
    addAccountFrame.add(datePanel, gbc);

    // Populate day, month, and year ComboBoxes
    populateDateComboBox(dayComboBox, monthComboBox, yearComboBox);

    // Buttons for Add and Clear
    JPanel buttonPanel = new JPanel();
    addAddButton = new JButton("Add");
    addClearButton = new JButton("Clear");
    buttonPanel.add(addAddButton);
    buttonPanel.add(addClearButton);

    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    addAccountFrame.add(buttonPanel, gbc);

    // Setup Action Listeners
    addAddButton.addActionListener(this);
    addClearButton.addActionListener(this);
    if (addAccountFrame != null) {
        // Show the "Add account" frame and ensure it is the only subFrame open
        openFrame(addAccountFrame, transactionFrame, deleteAccountFrame);
    }
}

	/*
		Method to initialise and open the "Add Transaction" frame
	 */
	private void transactionFrame() {
		// Allow only 1 transactionFrame at a time
		if (transactionFrame != null) {
			transactionFrame.dispose();
		}
		transactionFrame = new JFrame("Add a Transaction");
		transactionFrame.setSize(900, 450); // Increased size to fit JTextArea
		transactionFrame.setLayout(new GridBagLayout());
		transactionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;

		// Section: Form on the left
		JPanel formPanel = new JPanel(new GridBagLayout());
		GridBagConstraints formGbc = new GridBagConstraints();
		formGbc.insets = new Insets(5, 5, 5, 5);
		formGbc.anchor = GridBagConstraints.WEST;

		// Account dropdown
		formGbc.gridx = 0;
		formGbc.gridy = 0;
		formPanel.add(new JLabel("Select an account:"), formGbc);
		formGbc.gridx = 1;
		transactionAccountComboBox = new JComboBox<>();
		formPanel.add(transactionAccountComboBox, formGbc);

		// Populate the comboBox with account numbers
		populateAccountComboBox(transactionAccountComboBox);

		// Transaction type ComboBox
		formGbc.gridx = 0;
		formGbc.gridy = 1;
		formPanel.add(new JLabel("Transaction type:"), formGbc);
		formGbc.gridx = 1;
		transactionTypeComboBox = new JComboBox<>(new String[]{"Deposit", "Withdrawal"});
		formPanel.add(transactionTypeComboBox, formGbc);

		// Amount field
		formGbc.gridx = 0;
		formGbc.gridy = 2;
		formPanel.add(new JLabel("Amount £:"), formGbc);
		formGbc.gridx = 1;
		transactionAmountField = new JTextField(15);
		formPanel.add(transactionAmountField, formGbc);

		// Date fields (day, month, year)
		formGbc.gridx = 0;
		formGbc.gridy = 3;
		formPanel.add(new JLabel("Date:"), formGbc);
		formGbc.gridx = 1;
		JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		transactionDayComboBox = new JComboBox<>();
		transactionMonthComboBox = new JComboBox<>();
		transactionYearComboBox = new JComboBox<>();
		datePanel.add(transactionDayComboBox);
		datePanel.add(transactionMonthComboBox);
		datePanel.add(transactionYearComboBox);
		formPanel.add(datePanel, formGbc);

		// Populate date ComboBoxes 
		populateDateComboBox(transactionDayComboBox, transactionMonthComboBox, transactionYearComboBox);

		// Button Panel for Add and Clear
		JPanel buttonPanel = new JPanel();
		transactionAddButton = new JButton("Add");
		transactionClearButton = new JButton("Clear");
		buttonPanel.add(transactionAddButton);
		buttonPanel.add(transactionClearButton);

		formGbc.gridx = 0;
		formGbc.gridy = 4;
		formGbc.gridwidth = 2;
		formGbc.anchor = GridBagConstraints.CENTER;
		formPanel.add(buttonPanel, formGbc);

		// Add formPanel to the main frame
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.VERTICAL;
		transactionFrame.add(formPanel, gbc);

		// Section: Transactions Display Area on the right
		transactionTextArea = new JTextArea(20, 35);
		transactionTextArea.setEditable(false);
		transactionTextArea.setBackground(new Color(200, 190, 190)); // Light grey

		// Wrap transactionTextArea in a JScrollPane
		JScrollPane scrollPane = new JScrollPane(transactionTextArea);
		scrollPane.setPreferredSize(new Dimension(350, 350));
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// Label for the transactions area
		JPanel transactionsPanel = new JPanel(new BorderLayout());
		JLabel transactionsLabel = new JLabel("Currently available transactions from selected account:");
		transactionsPanel.add(transactionsLabel, BorderLayout.NORTH);
		transactionsPanel.add(scrollPane, BorderLayout.CENTER);

		// Add transactionsPanel to the main frame
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		transactionFrame.add(transactionsPanel, gbc);

		// Setup Action Listeners
		transactionAddButton.addActionListener(this);
		transactionClearButton.addActionListener(this);
		transactionAccountComboBox.addActionListener(e -> displayTransactions()); // Show transactions of the currently selected account

		if (transactionFrame != null) {
			// Show the "Add Transaction" frame and ensure it is the only subframe open
			openFrame(transactionFrame, addAccountFrame, deleteAccountFrame);
		}
	}

	/*
		Method to initialise and open the "Delete Account" frame
	 */
	public void deleteAccountFrame() {
		// Allow only 1 deleteAccountFrame at a time
		if (deleteAccountFrame != null) {
			deleteAccountFrame.dispose();
		}
		// Set up the frame
		deleteAccountFrame = new JFrame("Delete account");
		deleteAccountFrame.setSize(400, 200);
		deleteAccountFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		deleteAccountFrame.setLayout(new GridBagLayout());

		// Create components
		JLabel selectLabel = new JLabel("Select an account to delete:");
		deleteAccountComboBox = new JComboBox<>();
		deleteButton = new JButton("Delete");

		// Populate the dropdown with account numbers
		populateAccountComboBox(deleteAccountComboBox);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridx = 0;
		gbc.gridy = 0;
		deleteAccountFrame.add(selectLabel, gbc);

		gbc.gridx = 1;
		deleteAccountFrame.add(deleteAccountComboBox, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		deleteAccountFrame.add(deleteButton, gbc);

		//Setup Action Listeners
		deleteButton.addActionListener(this);
		if (deleteAccountFrame != null) {
			//Show the "Delete Account" frame and ensure it is the only subframe open
			openFrame(deleteAccountFrame, addAccountFrame, transactionFrame);
		}

	}

	/*
		Method to displayActive accounts in the main frame
	 */
	private void displayAllAccounts() {
		// Clear the text Area before displaying
		accountsArea.setText("");

		accountsArea.setText(accountHistory.getAllAccountDetails());
	}

	/*
		Helper method to populate combo box with all available active account numbers
	 */
	private void populateAccountComboBox(JComboBox<String> accountComboBox) {
		// Clear existing items
		accountComboBox.removeAllItems();
		// Get account numbers from accountHistory and add to dropdown
		for (BankAccount account : accountHistory.getActiveAccounts().values()) {
			accountComboBox.addItem(account.getAccNumber());
		}
	}

	/*
		Helper method to populate day, month, and year combo boxes
	 */
	private void populateDateComboBox(JComboBox<String> day, JComboBox<String> month, JComboBox<String> year) {

		// populate days
		for (int i = 1; i <= 31; i++) {
			day.addItem(String.valueOf(i));
		}

		// populate months
		String[] months = {"January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December"};
		for (String Month : months) {
			month.addItem(Month);
		}

		// populate years
		for (int i = 2010; i <= Year.now().getValue() ; i++) {
			year.addItem(String.valueOf(i));
		}

	}

	/*
		Helper method to turn a month name into its equivalent numeric value
	 */
	private int monthToNumber(String month) {

		Month upperMonth = Month.valueOf(month.toUpperCase());
		int monthValue = upperMonth.getValue();

		return monthValue;
	}

	/*
		Method to clear fields from the add account frame
	 */
	private void addAccountClearFields() {

		accountNumberField.setText(null);
		accountHolderNameField.setText(null);
		addressField.setText(null);
		dayComboBox.setSelectedIndex(0);
		monthComboBox.setSelectedIndex(0);
		yearComboBox.setSelectedIndex(0);
	}

	/*
		Method to clear fields from the add transaction frame
	 */
	private void addTransactionClearFields() {
		transactionTypeComboBox.setSelectedIndex(0);
		transactionAmountField.setText(null);
		transactionDayComboBox.setSelectedIndex(0);
		transactionMonthComboBox.setSelectedIndex(0);
		transactionYearComboBox.setSelectedIndex(0);
	}

	/*
		Helper method to only allow one subframe open at a time
	 */
	private void openFrame(JFrame frameToOpen, JFrame... framesToClose) {
		for (JFrame frame : framesToClose) {
			if (frame != null && frame.isVisible()) {
				frame.setVisible(false);
			}
		}
		frameToOpen.setVisible(true);
	}

	/*
		Method to save BankSystem into a file
	*/
	private void saveFile() {

		try {
			int choice = JOptionPane.showConfirmDialog(mainFrame, "Saving will override any previous save.\nAre you sure you want to continue?");
			if (choice == JOptionPane.YES_OPTION) {
			FileOutputStream fileOut = new FileOutputStream("AccountData");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(accountHistory);
			out.close();
			fileOut.close();
			JOptionPane.showMessageDialog(mainFrame, "File saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (IOException i) {
			JOptionPane.showMessageDialog(mainFrame, "Oops, something went wrong!,\nCould not Save the file.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	/*
		Method to load BankSystem from a file
	*/
	private void openFile() {
		
		try {
			int choice = JOptionPane.showConfirmDialog(mainFrame, "Loading will delete any changes made until now.\nAre you sure you want to continue?");
			if (choice == JOptionPane.YES_OPTION) {
			FileInputStream fileIn = new FileInputStream("AccountData");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			accountHistory = (BankSystem) in.readObject();
			in.close();
			fileIn.close();
			displayAllAccounts(); //Refresh available accounts in the mainFrame
			}
		} catch (IOException | ClassNotFoundException i) {
			JOptionPane.showMessageDialog(mainFrame, "Oops, something went wrong!\nCould not load the file.", "Error!", JOptionPane.ERROR_MESSAGE);
		}
	}

}
