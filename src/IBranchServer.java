import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBranchServer extends Remote{
	public void deposit(String sender,int ser,String accNumber,int amount) throws RemoteException;
	public void withdraw(String sender,int ser,String accNumber,int amount) throws RemoteException;
	public void query(String sender,int ser,String accNumber) throws RemoteException;
	public void transfer(String sender,int ser,String accNumberSource,String accNumberDest,int amount) throws RemoteException;
	public void depositTransfer(String sender,int ser,String accNumber,int amount) throws RemoteException;
	public void error(String sender,int ser,String errorMessage) throws RemoteException;
	public void snapShot(String sender,int ser) throws RemoteException;
	public void marker(String sender)throws RemoteException;
}
