import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.SynchronousQueue;

public class BranchServer extends UnicastRemoteObject implements IBranchServer{

	private static final long serialVersionUID = 1L;
	static int REG_PORT = 3334;
	public boolean shutdown = false;
	public String message;
	public String branchName;
	public ArrayList<Account> accountsList =new ArrayList<Account>();
	private SynchronousQueue<Request> messageQueue = new SynchronousQueue<Request>();
	public ArrayList<String> inNeighbours = new ArrayList<String>();
	public ArrayList<String> outNeighbours = new ArrayList<String>();
	IOverseer overseer;
	IBranchGUI gui;
	boolean isRecording;
	Hashtable<String,ArrayList<String>> channelState = new Hashtable<String,ArrayList<String>>();
	Hashtable<String,Integer> accountState = new Hashtable<String, Integer>();
	ArrayList<String> requests = new ArrayList<String>();
	Hashtable<String,Boolean> channelClosed = new Hashtable<String, Boolean>();
	public static void startRegistry() throws RemoteException{
		LocateRegistry.createRegistry(REG_PORT);
	}

	BranchServer(String branchName)throws RemoteException {
		this.branchName = branchName;
		init();
	}

	public static void main(String args[]){
		try {
			new BranchServer(args[0]);
//			new BranchServer("14");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readBranchNeighbours(){
		try {
			File aFile = new File(System.getProperty("user.dir")+"\\Topology.txt");
			BufferedReader input =  new BufferedReader(new FileReader(aFile));
			try {
				String line = null;
				while (( line = input.readLine()) != null){
					String[] accs = line.split(" ");
					if(branchName.equals(accs[0])){
						outNeighbours.add(accs[1]);
						channelClosed.put(accs[1], false);
						channelState.put(accs[1], new ArrayList<String>());
					}
					if(branchName.equals(accs[1])){
						inNeighbours.add(accs[0]);
						channelClosed.put(accs[0], false);
						channelState.put(accs[0], new ArrayList<String>());
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

	private void readBranchAccounts(){
		try {
			File aFile = new File(System.getProperty("user.dir")+"\\BranchAccounts.txt");
			BufferedReader input =  new BufferedReader(new FileReader(aFile));
			try {
				String line = null;
				while (( line = input.readLine()) != null){
					String[] accs = line.split("\\.");
					if(branchName.equals(accs[0])){
						accountsList.add(new Account(line));
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

	private void init(){
		try {
			Registry registry = LocateRegistry.getRegistry(REG_PORT);
			registry.rebind(branchName, this);
			readBranchNeighbours();
			readBranchAccounts();
			overseer = (IOverseer) registry.lookup("overseer");
			new Thread(new RequestProcessor()).start();
			createBranchUI();
			overseer.branchCreated();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createBranchUI(){
		try {
			ProcessBuilder builder = new ProcessBuilder(System.getProperty("java.home") + "/bin/java.exe",
					"-cp",System.getProperty("java.class.path"), "BranchGUI",
					String.valueOf(branchName+"GUI"));
			final Process UI = builder.start();
			new Thread(new Runnable(){public void run(){try {
				UI.waitFor();
				overseer.branchDead();
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
			}}}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Account getLocalAccount(String accNumber){
		if(accNumber.split("\\.")[0].equals(branchName)){
			for(Account a : accountsList){
				if(a.getAccountNumber().equals(accNumber))
					return a;
			}
			Account a1 = new Account(accNumber);
			accountsList.add(a1);
			return a1;
		}
		return null;
	}

	void printAccountStatus(){
		for(Account a : accountsList){
			System.out.println("Account "+a.getAccountNumber()+" has balance: "+a.getBalance());
		}
	}

	private class RequestProcessor implements Runnable{
		@Override
		public void run() {
			System.out.println("Request Processor Started");
			while (!shutdown) {
				try {
					Request request = messageQueue.take();
					processMessage(request);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		private void processMessage(Request request)throws RemoteException{
			Account a=null;
			if(isRecording){
				addToChannelState(request.sender, request.getMessageString());
			}
			System.out.println("Request : "+request.getRequestType().name());
			if(!request.getRequestType().name().equals("SnapShot")){
				if(!request.getRequestType().name().equals("Marker")){
					a = getLocalAccount(request.getAccNumber());
				}
			}
			checkGUIBound();
			String serialKey = request.getAccNumber()+Integer.toString(request.getSer());
			if(!isDuplicateRequest(serialKey)){
				switch(request.getRequestType()){
				case DepositTransfer:
					if(a!=null){
						a.setBalance(a.getBalance()+request.getAmount());
					}
					break;
				case Deposit:
					if(a!=null){
						a.setBalance(a.getBalance()+request.getAmount());
						gui.sendBalance(request.getSer(), request.getAccNumber(), a.getBalance());
					}else{
						gui.sendError(request.getSer(), "Sorry, Account Not handled by Server");
					}
					break;
				case Query:
					if(a!=null){
						gui.sendBalance(request.getSer(), request.getAccNumber(), a.getBalance());
					}else{
						gui.sendError(request.getSer(), "Sorry, Account Not handled by Server");
					}
					break;
				case Transfer:
					if(a!=null){
						if(a.getBalance()<request.getAmount()){
							gui.sendError(request.getSer(), "Sorry, Insufficient Credit");
							break;
						}
						String destBranch = request.getDes_accNumber().split("\\.")[0];
						if(isOutNeighbour(destBranch)){
							IBranchServer desStub = getBranchStub(destBranch);
							if(desStub!=null){
								desStub.depositTransfer(branchName,request.getSer(), request.getDes_accNumber(), request.getAmount());
								a.setBalance(a.getBalance()-request.getAmount());
								gui.sendBalance(request.getSer(), request.getAccNumber(), a.getBalance());
							}else{
								gui.sendError(request.getSer(), "Sorry,Cannot transfer to that Destination Account");
							}
						}else{
							Account dest_a =getLocalAccount(destBranch);
							if(dest_a!=null){
								dest_a.setBalance(a.getBalance()+request.getAmount());
								a.setBalance(a.getBalance()-request.getAmount());
								gui.sendBalance(request.getSer(), request.getAccNumber(), a.getBalance());
							}else{
								gui.sendError(request.getSer(), "Sorry,Cannot transfer to that Destination Account");
							}
							gui.sendError(request.getSer(), "Sorry,Cannot transfer to that Destination Account");
						}
					}else{
						gui.sendError(request.getSer(), "Sorry, Source Account Not handled by Server");
					}
					break;
				case Withdraw:
					if(a!=null){
						if(a.getBalance()>=request.getAmount()){
							a.setBalance(a.getBalance()-request.getAmount());
							gui.sendBalance(request.getSer(), request.getAccNumber(), a.getBalance());
						}else{
							gui.sendError(request.getSer(), "Sorry, Insufficient Credit");
						}
					}else{
						gui.sendError(request.getSer(), "Sorry, Account Not handled by Server");
					}
					break;
				case SnapShot: //UI Request to take snapshot
					gui.sendError(request.getSer(), "Generating snapshot..Please Wait.");
					isRecording=true;
					for(Account acc : accountsList){
						if(acc.getBalance()!=0)
							addToAccountState(acc.getAccountNumber(), acc.getBalance());
					}
					sendMarkerToNeighbours();
					break;
				case Marker:
					System.out.println("Marker recvd..from "+request.sender);
					channelClosed.put(request.sender, true);
					if(isRecording){
						if(checkAllChannelsClosed())
							isRecording = false;
					}else{
						System.out.println("Not Recoding..Starting Now..");
						flushChannel(request.sender);
						isRecording=true;
						for(Account acc : accountsList){
							if(acc.getBalance()!=0)
								addToAccountState(acc.getAccountNumber(), acc.getBalance());
						}
						sendMarkerToNeighbours();
					}
					printChannelClosed();
					if(checkAllChannelsClosed())
						sendSnapShot();
					break;
				}
				printAccountStatus();
				requests.remove(serialKey);
			}else{
				gui.sendError(request.getSer(), "Don't click the Button too much. It Hurts.");
			}
		}
	}

	private boolean isDuplicateRequest(String serialKey){
		if(requests.contains(serialKey))
			return true;
		requests.add(serialKey);
		return false;
	}

	private void checkGUIBound(){
		try {
			Registry registry = LocateRegistry.getRegistry(REG_PORT);
			if(gui==null) gui = (IBranchGUI) registry.lookup(branchName+"GUI");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isOutNeighbour(String branchName){
		if(outNeighbours.contains(branchName))
			return true;
		return false;
	}

	private IBranchServer getBranchStub(String branchName){
		IBranchServer bs=null;
		try {
			bs = (IBranchServer)LocateRegistry.getRegistry(REG_PORT).lookup(branchName);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return bs;
	}

	private void sendMarkerToNeighbours(){
		try {
			for(String b : outNeighbours){
				IBranchServer bStub = getBranchStub(b);
				bStub.marker(branchName);
				flushChannel(b);
				channelClosed.put(b, true);
				System.out.println("Sent Marker to "+b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean checkAllChannelsClosed(){
		Iterator<String> iterator = channelClosed.keySet().iterator();
		while(iterator.hasNext()){
			if(!channelClosed.get(iterator.next()))
				return false;
		}
		return true;
	}
	
	private void printChannelClosed(){
		Iterator<String> iterator = channelClosed.keySet().iterator();
		while(iterator.hasNext()){
			String channel = iterator.next();
			System.out.println("Channel: "+channel+"is "+channelClosed.get(channel));
		}
	}
	private void sendSnapShot(){
		try {
			System.out.println("Building Snapshot");
			StringBuffer buffer = new StringBuffer();
			Iterator<String> acciter = accountState.keySet().iterator();
			while(acciter.hasNext()){
				String accountNum = acciter.next();
				buffer.append("Account "+accountNum+" has balance: "+accountState.get(accountNum));
				buffer.append("\n");
			}
			Iterator<String> iterator = channelClosed.keySet().iterator();
			while(iterator.hasNext()){
				String channel = iterator.next();
				buffer.append("Channel "+branchName+channel+" recved: "+channelState.get(channel));
				buffer.append("\n");
			}
			flushAllChannels();
			clearAccountState();
			setAllChannelsOpen();
			System.out.println("Sending snapshot: "+buffer.toString());
			gui.sendSnapShot(buffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void clearAccountState(){
		accountState.clear();
	}

	public void addToAccountState(String accountNumber,int amount){
		accountState.put(accountNumber, amount);
	}

	public void flushChannel(String channel){
		channelState.get(channel).clear();
	}

	public void addToChannelState(String channel,String message){
		channelState.get(channel).add(message);
	}
	
	private void flushAllChannels(){
		Iterator<String> iterator = channelClosed.keySet().iterator();
		while(iterator.hasNext()){
			flushChannel(iterator.next());
		}
	}
	
	private void setAllChannelsOpen(){
		Iterator<String> iterator = channelClosed.keySet().iterator();
		while(iterator.hasNext()){
			channelClosed.put(iterator.next(),false);
		}
	}
	
	@Override
	public void deposit(String sender,int ser, String accNumber, int amount)
	throws RemoteException {
		try {
			messageQueue.put(new Request(sender,RequestType.Deposit,accNumber,ser,amount));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void query(String sender,int ser, String accNumber) throws RemoteException {
		try {
			messageQueue.put(new Request(sender,RequestType.Query,accNumber,ser));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void transfer(String sender,int ser, String accNumberSource, String accNumberDest,
			int amount) throws RemoteException {
		try {
			messageQueue.put(new Request(sender,RequestType.Transfer,accNumberSource,ser,amount,accNumberDest));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void withdraw(String sender,int ser, String accNumber, int amount)
	throws RemoteException {
		try {
			messageQueue.put(new Request(sender,RequestType.Withdraw,accNumber,ser,amount));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void error(String sender,int ser, String errorMessage) throws RemoteException {

	}

	@Override
	public void depositTransfer(String sender,int ser, String accNumber, int amount)
	throws RemoteException {
		try {
			messageQueue.put(new Request(sender,RequestType.DepositTransfer,accNumber,ser,amount));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void snapShot(String sender, int ser) throws RemoteException {
		try {
			messageQueue.put(new Request(sender,RequestType.SnapShot,ser));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void marker(String sender) throws RemoteException {
		try {
			messageQueue.put(new Request(sender,RequestType.Marker));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
