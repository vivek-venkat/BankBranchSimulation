import java.io.Serializable;

public class Account implements Serializable{

	private static final long serialVersionUID = -2895163045186496452L;
	private String accountNumber;
	private int balance;
	
	public Account(String accountNumber) {
		this.accountNumber = accountNumber;
		balance=0;
	}
	
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
}