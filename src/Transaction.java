/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.Serializable;

public class Transaction implements Serializable {

	private String type;
	private double amount;
	private final Calendar date;
	public int transactionNo;

	public Transaction(String type, double amount, int day, int month, int year, int transactionNo) {
		
		this.date = Calendar.getInstance();
		if (isValidDate(day, month, year)) {			
			date.setLenient(false); // Strict date validation
			date.set(year, month-1, day); // Adjust for zero-indexing of month
		}
		//validate amount
		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be greater than 0.");
		}
		this.amount = amount;

		//validate type
		if (!isValidType(type)) {
			throw new IllegalArgumentException("Not a valid transaction type.");
		}
		this.type = type;
		
		this.transactionNo = transactionNo;
	}

	/*
		Getters
	 */
	public String getType() {
		return type;
	}

	public double getAmount() {
		return amount;
	}

	public Calendar getDate() {
		return date;
	}

	/*
		Setters
	 */
	public void setType(String type) {
		if (!isValidType(type)) {
			throw new IllegalArgumentException("Not a valid transaction type.");
		}
		this.type = type;
	}

	public void setAmount(double amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be greater than 0");
		} else {
			this.amount = amount;
		}
	}


	/*
		Display transaction details
	 */
	public String getTransactionDetails() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		String formatedDate = dateFormat.format(date.getTime());
		return ("\nTransaction no: "+ transactionNo + "\nType: " + type + "\nAmount: £" + amount + "\nDate: " + formatedDate + "\n");
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
			throw new IllegalArgumentException("Invalid date");
		}
		return isValid;
	}

	/*
		Helper method to validate the transaction type
	 */
	private boolean isValidType(String type) {
		boolean isValid = false;
		// Check whether the given type is one of the acceptable types
		isValid = type != null && (type.equalsIgnoreCase("Deposit") || type.equalsIgnoreCase("Withdrawal"));

		return isValid;
	}
}