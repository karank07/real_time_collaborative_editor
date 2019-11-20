package client;


import gui.MainView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

	private String nameOfDocument;
	private String textOfDocument;
	private int versionOfDocument;
	private String userName;
	private ClientViewHandler actionListener;
	private Socket socket;
	private int port;
	private String host;
	private PrintWriter out;
	private MainView mainWindow;

	
	public Client(int port, String host, MainView main) {
		this.port = port;
		this.host = host;
		mainWindow = main;

	}

	
	public void start() throws IOException {
		socket = new Socket(host, port);
		mainWindow.openUsernameDialog();
		actionListener = new ClientViewHandler(this, socket);
		actionListener.run();
		out = new PrintWriter(socket.getOutputStream());
		

	}

	
	public void setMainWindow(MainView frame) {
		this.mainWindow = frame;
	}

	public void sendMessageToServer(String message) {
		try {
			out = new PrintWriter(socket.getOutputStream());
			if (true) {System.out.println("socket is" + socket.getLocalPort());}
			out.write(message + "\n");
			out.flush();
		} catch (IOException e) {
			mainWindow.openErrorView(e.getMessage());
		}
	}
	
	

	public void setUsername(String name){
		System.out.println("setting username");
		userName = name;
		mainWindow.setUsername(name);
		mainWindow.switchToWelcomeView();
	}
	

	public String getUsername(){
		return userName;
	}

	
	public String getDocumentName() {
		return nameOfDocument;
	}

	
	public String getText() {
		return textOfDocument;
	}
 

	public int getVersion(){
		 return versionOfDocument;
	}

	
	public Socket getSocket() {
		return socket;
	}

	public MainView getMainWindow() {
		return mainWindow;
	}

	
	public void updateDocumentName(String name) {
		System.out.println("updating documentName");
		nameOfDocument = name;
	}

	
	public void updateText(String text) {
		textOfDocument = text;
	}


	public void updateVersion(int newVersion) {
		versionOfDocument = newVersion;
	}

}
