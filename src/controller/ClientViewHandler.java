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

/**
 * @author 
 * 
 * ClientViewHandler class listens to data stream for incoming commands from server, and performs create,edit document operations for each client. 
 *
 */

public class ClientViewHandler {

	private Client client;
	private Socket socket;
	private BufferedReader in;
	private MainView mainView;
	
	private int groupLengthChange = 10;
	private int groupTextChange = 11;
	private int groupVersionChange = 8;
	private int groupPositionChange = 9;
	private int openVersion = 5;
	private int openText = 6;
	private String regex = "(Error: .+)|" + "(alldocs [\\w|\\d]+)|(new [\\w|\\d]+)|(open [\\w|\\d]+\\s(\\d+)\\s?(.+)?)|"
			+ "(change [\\w|\\d]+\\s[\\w|\\d]+\\s(\\d+)\\s(\\d+)\\s(-?\\d+)\\s?(.+)?)|(name [\\d\\w]+)";
	
	public ClientViewHandler(Client client, Socket socket) {

		this.mainView = client.getMainView();
		this.client = client;
		this.socket = socket;
	}

	/**
	 * Performs setUsername, create & edit documents, show-all documents, open document
	 * @param input command from server
	 * 
	 */
	public void commandHandler(String input) {
		input = input.trim();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);

		if (!matcher.find()) {
			mainView.openErrorView("from CAL: regex failure");
		}
		String[] commands = input.split(" ");

		if (commands[0].equals("Error:")) {
			mainView.openErrorView(input);
		}

		else if (commands[0].equals("open")) {
			client.updateDocumentName(commands[1]);
			client.updateVersion(Integer.parseInt(matcher.group(openVersion)));
			String documentText = matcher.group(openText);
			client.updateText(documentText);
			mainView.switchToDocumentView(commands[1], documentText);

		}

		else if (commands[0].equals("new")) {
			mainView.switchToDocumentView(commands[1], "");
			client.updateDocumentName(commands[1]);
			client.updateVersion(1);
		} else if (commands[0].equals("change")) {

			int version = Integer.parseInt(matcher.group(groupVersionChange));
			if (client.getDocumentName() != null) {
				if (client.getDocumentName().equals(commands[1])) {
					String username = commands[2];
					String documentText = matcher.group(groupTextChange);
					int editPosition = Integer.parseInt(matcher.group(groupPositionChange));
					int editLength = Integer.parseInt(matcher.group(groupLengthChange));
					mainView.updateDocument(documentText, editPosition, editLength, username, version);
					client.updateText(documentText);
					client.updateVersion(version);

				}
			}
		} else if (commands[0].equals("alldocs")) {
			ArrayList<String> docNames = new ArrayList<String>();
			for (int i = 1; i < commands.length; i++) {
				docNames.add(commands[i]);
			}
			mainView.displayOpenDocuments(docNames);

		} else if (commands[0].equals("name")) {
			client.setUserName(commands[1]);

		}

	}

	public void readInputFromServer() throws IOException {
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		try {
			for (String input = in.readLine(); input != null; input = in.readLine()) {
				commandHandler(input); // incoming commands from server
			}
		}

		finally {
			in.close();
		}
	}

}
