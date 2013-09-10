import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IOverseer extends Remote {
	void branchCreated() throws RemoteException;
	void branchDead() throws RemoteException;
}
