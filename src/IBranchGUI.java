import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBranchGUI extends Remote{
	public void sendBalance(int ser,String accNumber,int balance)throws RemoteException;
	public void sendError(int ser,String errorMessage)throws RemoteException;
	public void sendSnapShot(String snapShotString)throws RemoteException;
}
