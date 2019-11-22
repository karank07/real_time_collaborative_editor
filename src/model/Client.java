package model;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import controller.ClientViewHandler;
import view.MainView;

public class Client {

	private String documentName;
	private String documentText;
	private int documentVersion;
	private String userName;
	private ClientViewHandler clientViewHandler;
	private Socket socket;
	private int port;
	private String host;
	private PrintWriter out;
	private MainView mainView;
	BufferedWriter writer;
	
	public Client(int port, String host, MainView main) {
		this.port = port;
		this.host = host;
		mainView = main;

		try {
			writer= new BufferedWriter(new FileWriter("D:\\\\Project\\\\DS_PROJECT\\\\collaborative_editor\\\\evaluation\\\\lanEvaluation_client2Server.txt"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		

	}

	
	public void start() throws IOException {
		socket = new Socket(host, port);
		mainView.openUsernameDialog();
		clientViewHandler = new ClientViewHandler(this, socket);
		clientViewHandler.readInputFromServer();
		out = new PrintWriter(socket.getOutputStream());
		

	}

	
	public void setMainView(MainView frame) {
		this.mainView = frame;
	}

	public void sendCommandToServer(String message) {
		SimpleDateFormat formatter;
		Date date;
		
		try {
			formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss.SSS z");
			date = new Date(System.currentTimeMillis());
			
			System.out.println("Sending command to server from client: "+ this.userName +" "+message+" at "+formatter.format(date) );
			writer.write("Sending command "
					+ "to server from client: "+ this.userName +" "+message+" at "+formatter.format(date) );
			writer.newLine();
			writer.flush();
			
			out = new PrintWriter(socket.getOutputStream());
			if (true) {System.out.println("socket is" + socket.getLocalPort());}
			out.write(message + "\n");
			out.flush();
		} catch (IOException e) {
			mainView.openErrorView(e.getMessage());
		}
	}
	
	

	public void setUserName(String name){
		System.out.println("setting username");
		userName = name;
		mainView.setUsername(name);
		mainView.switchToWelcomeView();
	}
	

	public String getUserName(){
		return userName;
	}

	
	public String getDocumentName() {
		return documentName;
	}

	
	public String getText() {
		return documentText;
	}
 

	public int getVersion(){
		 return documentVersion;
	}

	
	public Socket getSocket() {
		return socket;
	}

	public MainView getMainView() {
		return mainView;
	}

	
	public void updateDocumentName(String name) {
		System.out.println("updating documentName");
		documentName = name;
	}

	
	public void updateText(String text) {
		documentText = text;
	}


	public void updateVersion(int newVersion) {
		documentVersion = newVersion;
	}

}
