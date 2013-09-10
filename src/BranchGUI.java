import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class BranchGUI extends UnicastRemoteObject implements IBranchGUI{

	private static final long serialVersionUID = -5304745181983428246L;
	private JFrame frame;
	JLabel status = new JLabel("");
	JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	JButton activeButton;
	public static String branchGUIName;
	IBranchServer server;
	Random random = new Random();
	 
	public static void main(String[] args) throws RemoteException {
		BranchGUI.branchGUIName = args[0];
		final BranchGUI window = new BranchGUI();
		window.bindUI(branchGUIName);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					window.frame.setVisible(true);
					window.frame.setResizable(false);
					window.frame.setTitle("Branch UI - "+branchGUIName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void bindUI(String name){
		try {
			Registry registry = LocateRegistry.getRegistry(BranchServer.REG_PORT);
			registry.rebind(name, this);
			server = (IBranchServer)registry.lookup(name.replace("GUI",""));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BranchGUI() throws RemoteException{
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 360, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(tabbedPane, BorderLayout.NORTH);
		createSimpleOpTab(tabbedPane, "Deposit", "Deposit",
				new SimpleOpActionListener());
		createSimpleOpTab(tabbedPane, "Withdraw", "Withdraw",
				new SimpleOpActionListener());
		createQueryTab(tabbedPane);
		createTransferTab(tabbedPane);
		createSnapShotTab(tabbedPane);
		frame.add(status);
	}
	
	
	
    private void createSnapShotTab(JTabbedPane tabbedPane){
    	JPanel tabSnapShot = new JPanel();
		tabbedPane.addTab("Snapshot", null, tabSnapShot, null);
		GridBagLayout gbl_tabSnapShot = new GridBagLayout();
		gbl_tabSnapShot.columnWidths = new int[] { 0, 0, 0 };
		gbl_tabSnapShot.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_tabSnapShot.columnWeights = new double[] { 1.0, 1.0,
				Double.MIN_VALUE };
		gbl_tabSnapShot.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		tabSnapShot.setLayout(gbl_tabSnapShot);
		
		JLabel lblSourceAccount = new JLabel("\n\n\n\n\n");
		GridBagConstraints gbc_lblSourceAccount = new GridBagConstraints();
		gbc_lblSourceAccount.insets = new Insets(0, 0, 5, 5);
		gbc_lblSourceAccount.weightx = 0.5;
		gbc_lblSourceAccount.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblSourceAccount.anchor = GridBagConstraints.BASELINE;
		gbc_lblSourceAccount.gridx = 0;
		gbc_lblSourceAccount.gridy = 0;
		tabSnapShot.add(lblSourceAccount, gbc_lblSourceAccount);
		
		JButton btnSnapShot = new JButton("Get SnapShot");
		btnSnapShot.addActionListener(new SnapShotActionListener());
		GridBagConstraints gbc_btnSnapShot = new GridBagConstraints();
		gbc_btnSnapShot.gridwidth = 2;
		gbc_btnSnapShot.insets = new Insets(0, 0, 0, 5);
		gbc_btnSnapShot.gridx = 0;
		gbc_btnSnapShot.gridy = 1;
		tabSnapShot.add(btnSnapShot, gbc_btnSnapShot);
    }
    
	private void createTransferTab(JTabbedPane tabbedPane) {
		JPanel tabTransfer = new JPanel();
		tabbedPane.addTab("Transfer", null, tabTransfer, null);
		GridBagLayout gbl_tabTransfer = new GridBagLayout();
		gbl_tabTransfer.columnWidths = new int[] { 0, 0, 0 };
		gbl_tabTransfer.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_tabTransfer.columnWeights = new double[] { 1.0, 1.0,
				Double.MIN_VALUE };
		gbl_tabTransfer.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		tabTransfer.setLayout(gbl_tabTransfer);

		JLabel lblSourceAccount = new JLabel("Source Account:");
		GridBagConstraints gbc_lblSourceAccount = new GridBagConstraints();
		gbc_lblSourceAccount.insets = new Insets(0, 0, 5, 5);
		gbc_lblSourceAccount.weightx = 0.5;
		gbc_lblSourceAccount.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblSourceAccount.anchor = GridBagConstraints.BASELINE;
		gbc_lblSourceAccount.gridx = 0;
		gbc_lblSourceAccount.gridy = 0;
		tabTransfer.add(lblSourceAccount, gbc_lblSourceAccount);

		JTextField txtSource = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.weightx = 0.5;
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 0;
		txtSource.setColumns(8);
		tabTransfer.add(txtSource, gbc_textField_1);

		JLabel lblDestAccount = new JLabel("Destination Account: ");
		GridBagConstraints gbc_lblDestAccount = new GridBagConstraints();
		gbc_lblDestAccount.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblDestAccount.anchor = GridBagConstraints.BASELINE;
		gbc_lblDestAccount.insets = new Insets(0, 0, 5, 5);
		gbc_lblDestAccount.gridx = 0;
		gbc_lblDestAccount.gridy = 1;
		tabTransfer.add(lblDestAccount, gbc_lblDestAccount);

		JTextField txtDestination = new JTextField();
		GridBagConstraints gbc_txtDest = new GridBagConstraints();
		gbc_txtDest.insets = new Insets(0, 0, 5, 0);
		gbc_txtDest.anchor = GridBagConstraints.BASELINE;
		gbc_txtDest.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtDest.gridx = 1;
		gbc_txtDest.gridy = 1;
		txtDestination.setColumns(8);
		tabTransfer.add(txtDestination, gbc_txtDest);

		JButton btnTransfer = new JButton("Transfer");
		btnTransfer.addActionListener(new TransferActionListener());
		JTextField txtAmount = new JTextField();
		GridBagConstraints gbc_txtAmount = new GridBagConstraints();
		gbc_txtAmount.anchor = GridBagConstraints.BASELINE;
		gbc_txtAmount.insets = new Insets(0, 0, 5, 0);
		gbc_txtAmount.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtAmount.gridx = 1;
		gbc_txtAmount.gridy = 2;
		tabTransfer.add(txtAmount, gbc_txtAmount);
		txtAmount.setColumns(10);
		GridBagConstraints gbc_btnTransfer = new GridBagConstraints();
		gbc_btnTransfer.gridwidth = 2;
		gbc_btnTransfer.anchor = GridBagConstraints.BASELINE;
		gbc_btnTransfer.gridx = 0;
		gbc_btnTransfer.gridy = 3;
		tabTransfer.add(btnTransfer, gbc_btnTransfer);

		JLabel lblAmount = new JLabel("Amount: ");
		GridBagConstraints gbc_lblAmount = new GridBagConstraints();
		gbc_lblAmount.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblAmount.anchor = GridBagConstraints.BASELINE;
		gbc_lblAmount.insets = new Insets(0, 0, 5, 5);
		gbc_lblAmount.gridx = 0;
		gbc_lblAmount.gridy = 2;
		tabTransfer.add(lblAmount, gbc_lblAmount);
	}

	private void createQueryTab(JTabbedPane tabbedPane) {
		JPanel tabQuery = new JPanel();
		tabbedPane.addTab("Query", null, tabQuery, null);
		GridBagLayout gbl_tabQuery = new GridBagLayout();
		gbl_tabQuery.columnWidths = new int[] { 0, 0, 0 };
		gbl_tabQuery.rowHeights = new int[] { 0, 0, 0 };
		gbl_tabQuery.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_tabQuery.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		tabQuery.setLayout(gbl_tabQuery);

		JLabel lblAccountNumber = new JLabel("Account Number:");
		GridBagConstraints gbc_lblAccountNumber = new GridBagConstraints();
		gbc_lblAccountNumber.weightx = 0.5;
		gbc_lblAccountNumber.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblAccountNumber.anchor = GridBagConstraints.BASELINE;
		gbc_lblAccountNumber.insets = new Insets(0, 0, 5, 5);
		gbc_lblAccountNumber.gridx = 0;
		gbc_lblAccountNumber.gridy = 0;
		tabQuery.add(lblAccountNumber, gbc_lblAccountNumber);

		JTextField txtAccount = new JTextField();
		GridBagConstraints gbc_textAccountNumber = new GridBagConstraints();
		gbc_textAccountNumber.insets = new Insets(0, 0, 5, 0);
		gbc_textAccountNumber.weightx = 0.5;
		gbc_textAccountNumber.anchor = GridBagConstraints.BASELINE;
		gbc_textAccountNumber.fill = GridBagConstraints.HORIZONTAL;
		gbc_textAccountNumber.gridx = 1;
		gbc_textAccountNumber.gridy = 0;
		txtAccount.setColumns(8);
		tabQuery.add(txtAccount, gbc_textAccountNumber);

		JButton btnQuery = new JButton("Query");
		btnQuery.addActionListener(new QueryActionListener());
		GridBagConstraints gbc_btnQuery = new GridBagConstraints();
		gbc_btnQuery.gridwidth = 2;
		gbc_btnQuery.insets = new Insets(0, 0, 0, 5);
		gbc_btnQuery.gridx = 0;
		gbc_btnQuery.gridy = 1;
		tabQuery.add(btnQuery, gbc_btnQuery);
	}

	private void createSimpleOpTab(JTabbedPane tabbedPane, String title,
			String buttonText, ActionListener actionListener) {
		JPanel tab = new JPanel();
		tabbedPane.addTab(title, null, tab, null);
		GridBagLayout layoutTabDeposit = new GridBagLayout();
		layoutTabDeposit.columnWidths = new int[] { 0, 0, 0 };
		layoutTabDeposit.rowHeights = new int[] { 0, 0, 0, 0 };
		layoutTabDeposit.columnWeights = new double[] { 1.0, 1.0,
				Double.MIN_VALUE };
		layoutTabDeposit.rowWeights = new double[] { 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		tab.setLayout(layoutTabDeposit);

		JLabel lblAccount = new JLabel("Account Number: ");
		GridBagConstraints gbc_lblAccount = new GridBagConstraints();
		gbc_lblAccount.insets = new Insets(0, 0, 5, 5);
		gbc_lblAccount.weightx = 0.5;
		gbc_lblAccount.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblAccount.anchor = GridBagConstraints.BASELINE;
		gbc_lblAccount.gridx = 0;
		gbc_lblAccount.gridy = 0;
		tab.add(lblAccount, gbc_lblAccount);

		JTextField textAmount = new JTextField();
		GridBagConstraints gbc_textAmount = new GridBagConstraints();
		gbc_textAmount.insets = new Insets(0, 0, 5, 0);
		gbc_textAmount.weightx = 0.5;
		gbc_textAmount.fill = GridBagConstraints.HORIZONTAL;
		gbc_textAmount.gridx = 1;
		gbc_textAmount.gridy = 0;
		textAmount.setColumns(8);
		tab.add(textAmount, gbc_textAmount);

		JLabel lblAmount = new JLabel("Amount: ");
		GridBagConstraints gbc_lblAmount = new GridBagConstraints();
		gbc_lblAmount.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblAmount.weightx = 0.5;
		gbc_lblAmount.anchor = GridBagConstraints.BASELINE;
		gbc_lblAmount.insets = new Insets(0, 0, 5, 5);
		gbc_lblAmount.gridx = 0;
		gbc_lblAmount.gridy = 1;
		tab.add(lblAmount, gbc_lblAmount);

		JTextField txtAmount = new JTextField();
		GridBagConstraints gbc_txtAmount = new GridBagConstraints();
		gbc_txtAmount.anchor = GridBagConstraints.BASELINE;
		gbc_txtAmount.insets = new Insets(0, 0, 5, 0);
		gbc_txtAmount.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtAmount.gridx = 1;
		gbc_txtAmount.gridy = 1;
		tab.add(txtAmount, gbc_txtAmount);
		txtAmount.setColumns(10);

		JButton btnDeposit = new JButton(buttonText);
		btnDeposit.addActionListener(actionListener);
		btnDeposit.setName(buttonText);
		GridBagConstraints gbc_btnDeposit = new GridBagConstraints();
		gbc_btnDeposit.gridwidth = 2;
		gbc_btnDeposit.anchor = GridBagConstraints.BASELINE;
		gbc_btnDeposit.insets = new Insets(0, 0, 0, 5);
		gbc_btnDeposit.gridx = 0;
		gbc_btnDeposit.gridy = 2;
		tab.add(btnDeposit, gbc_btnDeposit);
	}

	private class SimpleOpActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			makeUILabelChange("");
			final JButton source = (JButton) e.getSource();
			JPanel panel = (JPanel) source.getParent();
			final String txtAmount = ((JTextField) panel.getComponent(3)).getText().trim();
			final String txtAccNumber = ((JTextField) panel.getComponent(1)).getText().trim();
			if(validateAccountNumber(txtAccNumber)){
				if(validateAmount(txtAmount)){
					if(source.getName().equals("Deposit")){
						disableButton(source);
						disableInactiveTabs(0);
						try {
							server.deposit(branchGUIName,getRandomSerial(),txtAccNumber,Integer.parseInt(txtAmount));
						} catch (Exception e1) {
							e1.printStackTrace();
							enableAllTabs();
							enableActiveButton();
						}
					}else{
						disableButton(source);
						disableInactiveTabs(1);
						try {
							server.withdraw(branchGUIName,getRandomSerial(), txtAccNumber,Integer.parseInt(txtAmount));
						} catch (Exception e1) {
							e1.printStackTrace();
							enableAllTabs();
							enableActiveButton();
						}
					}
				}else{
					makeUILabelChange("Please enter a valid Amount");
				}
			}else{
				makeUILabelChange("Please enter valid Account Number");
			}
		}
	}

	private class TransferActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			makeUILabelChange("");
			JButton source = (JButton) e.getSource();
			JPanel panel = (JPanel) source.getParent();
			String txtsrcAccNumber = ((JTextField) panel.getComponent(1)).getText().trim();
			String txtdestAccNumber = ((JTextField) panel.getComponent(3)).getText().trim();
			String txtAmount = ((JTextField) panel.getComponent(4)).getText().trim();
			if(validateAccountNumber(txtsrcAccNumber)){
				if(validateAccountNumber(txtdestAccNumber)){
					if(validateAmount(txtAmount)){
						disableButton(source);
						disableInactiveTabs(3);
						try {
							server.transfer(branchGUIName,getRandomSerial(),txtsrcAccNumber,txtdestAccNumber,Integer.parseInt(txtAmount));
						} catch (Exception e1) {
							e1.printStackTrace();
							enableAllTabs();
							enableActiveButton();
						}
					}else{
						makeUILabelChange("Please enter a valid Amount");
					}
				}else{
					makeUILabelChange("Please enter valid Destination Account Numberr");
				}
			}else{
				makeUILabelChange("Please enter valid Source Account Number");
			}
		}
	}

	private class SnapShotActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			makeUILabelChange("");
			JButton source = (JButton) e.getSource();
			disableButton(source);
			disableInactiveTabs(4);
			try {
				server.snapShot(branchGUIName,getRandomSerial());
			} catch (Exception e1) {
				e1.printStackTrace();
				enableAllTabs();
				enableActiveButton();
			}
		}
	}
	
	private class QueryActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			makeUILabelChange("");
			JButton source = (JButton) e.getSource();
			JPanel panel = (JPanel) source.getParent();
			String txtAccNumber = ((JTextField) panel.getComponent(1)).getText().trim();
			if(validateAccountNumber(txtAccNumber)){
				disableButton(source);
				disableInactiveTabs(2);
				try {
					server.query(branchGUIName,getRandomSerial(),txtAccNumber);
				} catch (Exception e1) {
					e1.printStackTrace();
					enableAllTabs();
					enableActiveButton();
				}
			}else{
				makeUILabelChange("Please enter valid Account Number");
			}
		}
	}

	private void enableAllTabs(){
		for(int i=0;i<5;i++)
			tabbedPane.setEnabledAt(i, true);
	}

	private void disableInactiveTabs(final int activeTab){
		for(int i=0;i<5;i++)
			if(i!=activeTab)
				tabbedPane.setEnabledAt(i, false);
	}

	private void disableButton(final JButton b){
		activeButton = b;
		activeButton.setEnabled(false);
	}

	private void enableActiveButton(){
		activeButton.setEnabled(true);
	}

	private boolean validateAccountNumber(String accNumber){
		if(accNumber.isEmpty())
			return false;
		if(!accNumber.contains("."))
			return false;
		String[] accParts = accNumber.split("\\.");
		if(accParts.length==2)
			if(accParts[0].length()==2 && accParts[1].length()==5 
					&& accNumber.matches("\\d+(.\\d+)"))
				return true;
		return false;
	}

	private boolean validateAmount(String amount){
		if(amount.isEmpty())
			return false;
		if(amount.matches("\\d+"))
			return true;
		return false;
	}

	private void makeUILabelChange(String labelMessage){
		status.setText(labelMessage);
	}

	private int getRandomSerial(){
		return random.nextInt(100)+(Integer.parseInt(branchGUIName.replace("GUI", ""))*1000);
	}
	
	@Override
	public void sendBalance(int ser, String accNumber,final int balance)
	throws RemoteException {
		makeUILabelChange("Balance: "+balance);
		enableAllTabs();
		enableActiveButton();
	}

	@Override
	public void sendError(int ser, String errorMessage) throws RemoteException {
		makeUILabelChange(errorMessage);
		enableAllTabs();
		enableActiveButton();
	}

	@Override
	public void sendSnapShot(String snapShotString) throws RemoteException {
		// Show Dailog with String here...
		JDialog snapshotDialog = new JDialog(frame);
		snapshotDialog.setSize(200, 300);
		JPanel messagePane = new JPanel();
		messagePane.add(new JLabel(snapShotString));
		snapshotDialog.getContentPane().add(messagePane);
		snapshotDialog.setLocationRelativeTo(frame);
		snapshotDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		snapshotDialog.setVisible(true);
	}
}
