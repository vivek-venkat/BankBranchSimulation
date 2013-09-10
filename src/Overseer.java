import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Overseer extends UnicastRemoteObject implements IOverseer{
	private static final long serialVersionUID = -718334084826807208L;
	ArrayList<String> branches = new ArrayList<String>();
	static int counter = 0;
	
	public Overseer()throws RemoteException{
		try {
			BranchServer.startRegistry();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		Registry registry = LocateRegistry.getRegistry(BranchServer.REG_PORT);
		registry.rebind("overseer", this);
		readBranches();
		createBranches();
	}

	private void readBranches(){
		try {
			File aFile = new File(System.getProperty("user.dir")+"\\BranchAccounts.txt");
			BufferedReader input =  new BufferedReader(new FileReader(aFile));
			try {
				String line = null;
				while (( line = input.readLine()) != null){
					String[] accs = line.split("\\.");
					if(!branches.contains(accs[0])){
						branches.add(accs[0]);
					}
				}
			}
			finally {
				input.close();
			}
		}
		catch (IOException ex){
			ex.printStackTrace();
		}
	}
	
	public void createBranches(){
		try {
			try {
				for(String b : branches){
					Process p= Runtime.getRuntime().exec("cmd /c CreateBranch.bat "+b);
					p.waitFor();
					System.out.println("Branch Created: "+b);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String argsp[]){
			try {
				new Overseer();
				while(counter>0){
					try {
							Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
//				System.exit(0);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void branchCreated() throws RemoteException {
		counter++;
	}
	
	@Override
	public void branchDead() throws RemoteException {
		counter--;
	}
}
