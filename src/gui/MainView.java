package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import client.Client;

@SuppressWarnings("unused")
public class MainView extends JFrame {

	private static final long serialVersionUID = 1L;
	private WelcomeView welcomeView;
	private EditorView documentView;
	private ConnectView connectView;
	private OpenDocumentDialog openDocumentDialog;
	private ArrayList<String> documentNames;
	private Client client;
	private String username;

	
	public MainView() {
		setTitle("Collaborative Text Editor");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(250, 200));
		connectView = new ConnectView(this);
		add(connectView, BorderLayout.CENTER);
		pack();
	}

	public void switchToWelcomeView() {
		setVisible(false);
		getContentPane().remove(connectView);	

		setPreferredSize(new Dimension(350, 150));
		setMinimumSize(new Dimension(350, 150));
		setMaximumSize(new Dimension(350, 150));
		welcomeView = new WelcomeView(this, client);
		add(welcomeView, BorderLayout.CENTER);

		
		setVisible(true);
	}

	public void openUsernameDialog() {
		String username = JOptionPane.showInputDialog("Enter a username", "");
		if(username==null){
			JOptionPane.showMessageDialog(null, "Please enter a valid username", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		else{
		client.sendMessageToServer("name " + username);
		}
	}
	
	public void setUsername(String name){
		this.username = name;
	}
	
	public String getUsername(){
		return username;
	}

	

	public void switchToDocumentView(String documentName, String documentText) {
	
		setVisible(false);
		removeAllViews();
		setPreferredSize(new Dimension(600, 500));
		setMinimumSize(new Dimension(600, 500));
		setMaximumSize(new Dimension(600, 500));
		documentView = new EditorView(this, documentName, documentText);
		this.addWindowListener(new ExitWindowListener(client));
		getContentPane().add(documentView, BorderLayout.CENTER);
		getContentPane().validate();
		getContentPane().repaint();
		setVisible(true);
	}

	
	private void removeAllViews() {
		if (welcomeView != null) {
			getContentPane().remove(welcomeView);
		}
		if (connectView != null) {
			getContentPane().remove(connectView);
		}
		if (documentView != null) {
			getContentPane().remove(documentView);
		}
	}

	
	public void displayOpenDocuments(ArrayList<String> documentNames) {
		
		openDocumentDialog = new OpenDocumentDialog(documentNames, client);
	}

		
	public void updateDocument(String documentText, int editPosition,
			int editLength, String username, int version) {
		
		if (documentView != null) {
			documentView.updateDocument(documentText, editPosition, editLength,
					username, version);
			getContentPane().repaint();
		}

	}

	public void openVersionErrorView(String error) {
		int n = JOptionPane.showConfirmDialog(null, error, "Error",
				JOptionPane.ERROR_MESSAGE);
		client.sendMessageToServer("open " + client.getDocumentName());
	
	}

	
	public void openErrorView(String error) {
		JOptionPane.showMessageDialog(null, error, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	public void setClient(Client client) {
		this.client = client;
	}

	
	public Client getClient() {
		return client;
	}

}
