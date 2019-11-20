package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Client;
import view.MainView;


public class ClientViewHandler {

	private Client client;
	private Socket socket;
	private BufferedReader in;
	private final String regex = "(Error: .+)|"
			+ "(alldocs [\\w|\\d]+)|(new [\\w|\\d]+)|(open [\\w|\\d]+\\s(\\d+)\\s?(.+)?)|"
			+ "(change [\\w|\\d]+\\s[\\w|\\d]+\\s(\\d+)\\s(\\d+)\\s(-?\\d+)\\s?(.+)?)|(name [\\d\\w]+)";
	private final int groupChangeVersion = 8;
	private final int groupChangePosition = 9;
	private final int groupChangeLength = 10;
	private final int groupChangeText = 11;
	private final int groupOpenVersion = 5;
	private final int groupOpenText = 6;
	private MainView mainView;


	public ClientViewHandler(Client client, Socket socket) {
		this.client = client;
		this.socket = socket;
		this.mainView = client.getMainView();
	}
    
	
	public void run() throws IOException {
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		try {
			for (String input = in.readLine(); input != null; input = in
					.readLine()) {
				handleCommandFromServer(input);
			}
		}

		finally {
			in.close();
		}
	}
	
	public void handleCommandFromServer(String input) {
		input = input.trim();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);

		if (!matcher.find()) {
			mainView.openErrorView("from CAL: regex failure");
		}
		String[] tokens = input.split(" ");
		
		if (tokens[0].equals("Error:")) {
			mainView.openErrorView(input);
		}

		else if (tokens[0].equals("alldocs")) {
			ArrayList<String> names = new ArrayList<String>();
			for (int i = 1; i < tokens.length; i++) {
				names.add(tokens[i]);
			}
			mainView.displayOpenDocuments(names);

		}
		else if (tokens[0].equals("name")){
			client.setUserName(tokens[1]);
			
			
		}

		else if (tokens[0].equals("new")) {
			mainView.switchToDocumentView(tokens[1], "");
			client.updateDocumentName(tokens[1]);
			client.updateVersion(1);
		}
		else if (tokens[0].equals("open")) {
			client.updateDocumentName(tokens[1]);
			client.updateVersion(Integer.parseInt(matcher.group(groupOpenVersion)));
			String documentText = matcher.group(groupOpenText);
			client.updateText(documentText);
			mainView.switchToDocumentView(tokens[1], documentText);

			

		}

		else if (tokens[0].equals("change")) {
			
			int version = Integer.parseInt(matcher.group(groupChangeVersion));
			if (client.getDocumentName()!=null) {
				if(client.getDocumentName().equals(tokens[1]) ){
				String username = tokens[2];
				String documentText = matcher.group(groupChangeText);
				int editPosition = Integer.parseInt(matcher.group(groupChangePosition));
				int editLength = Integer.parseInt(matcher.group(groupChangeLength));
				mainView.updateDocument(documentText, editPosition, editLength, username, version);
				client.updateText(documentText);
				client.updateVersion(version);

			}
		}
		}

	}

}
