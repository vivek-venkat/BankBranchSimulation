
public class Request{
	private RequestType requestType;
	private String accNumber;
	private int ser;
	private int amount;
	private String des_accNumber;
	String sender;
	
	public RequestType getRequestType() {
		return requestType;
	}
	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}
	public String getAccNumber() {
		return accNumber;
	}
	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}
	public int getSer() {
		return ser;
	}
	public void setSer(int ser) {
		this.ser = ser;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getDes_accNumber() {
		return des_accNumber;
	}
	public void setDes_accNumber(String desAccNumber) {
		des_accNumber = desAccNumber;
	}
	public Request(String sender,RequestType requestType, String accNumber, int ser,
			int amount) {
		this.requestType = requestType;
		this.accNumber = accNumber;
		this.ser = ser;
		this.amount = amount;
		this.sender=sender;
	}
	public Request(String sender,RequestType requestType, String accNumber, int ser) {
		this.requestType = requestType;
		this.accNumber = accNumber;
		this.ser = ser;
		this.sender=sender;
	}
	public Request(String sender,RequestType requestType, String accNumber, int ser,
			int amount, String des_accNumber) {
		this.requestType = requestType;
		this.accNumber = accNumber;
		this.ser = ser;
		this.amount = amount;
		this.des_accNumber = des_accNumber;
		this.sender=sender;
	}
	public Request(String sender,RequestType requestType,int ser){
		this.requestType = requestType;
		this.ser = ser;
		this.sender=sender;
	}
	public Request(String sender,RequestType requestType){
		this.requestType = requestType;
		this.sender=sender;
	}
	
	public String getMessageString(){
		String message="";
		switch(requestType){
			case Deposit:
				message += "Deposit amount: "+amount+" in account: "+accNumber;
				break;
			case DepositTransfer:
				message += "Deposit amount: "+amount+" in account: "+accNumber;
				break;
			case Query:
				message += "Check Balance in account: "+accNumber;
				break;
			case Transfer:
				message += "Transfer amount: "+amount+" from account: "+accNumber+" to account: "+des_accNumber;
				break;
			case Withdraw:
				message += "Withdraw amount: "+amount+" from account: "+accNumber;
				break;
			case SnapShot:
				message += "Request Snapshot";
				break;
			case Marker:
				message += "Send Marker";
				break;
		}
		return message;
	}
}
