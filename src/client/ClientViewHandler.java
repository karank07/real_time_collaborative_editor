package client;

import gui.MainView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
	private MainView main;


	public ClientViewHandler(Client client, Socket socket) {
		this.client = client;
		this.socket = socket;
		this.main = client.getMainWindow();
	}
    
	
	public void run() throws IOException {
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		try {
			for (String line = in.readLine(); line != null; line = in
					.readLine()) {
				handleMessageFromServer(line);
			}
		}

		finally {
			in.close();
		}
	}
	
	public void handleMessageFromServer(String input) {
		input = input.trim();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);

		if (!matcher.find()) {
			main.openErrorView("from CAL: regex failure");
		}
		String[] tokens = input.split(" ");
		
		if (tokens[0].equals("Error:")) {
			main.openErrorView(input);
		}

		else if (tokens[0].equals("alldocs")) {
			ArrayList<String> names = new ArrayList<String>();
			for (int i = 1; i < tokens.length; i++) {
				names.add(tokens[i]);
			}
			main.displayOpenDocuments(names);

		}
		else if (tokens[0].equals("name")){
			client.setUsername(tokens[1]);
			
			
		}

		else if (tokens[0].equals("new")) {
			main.switchToDocumentView(tokens[1], "");
			client.updateDocumentName(tokens[1]);
			client.updateVersion(1);
		}
		else if (tokens[0].equals("open")) {
			client.updateDocumentName(tokens[1]);
			client.updateVersion(Integer.parseInt(matcher.group(groupOpenVersion)));
			String documentText = matcher.group(groupOpenText);
			client.updateText(documentText);
			main.switchToDocumentView(tokens[1], documentText);

			

		}

		else if (tokens[0].equals("change")) {
			
			int version = Integer.parseInt(matcher.group(groupChangeVersion));
			if (client.getDocumentName()!=null) {
				if(client.getDocumentName().equals(tokens[1]) ){
				String username = tokens[2];
				String documentText = matcher.group(groupChangeText);
				int editPosition = Integer.parseInt(matcher.group(groupChangePosition));
				int editLength = Integer.parseInt(matcher.group(groupChangeLength));
				main.updateDocument(documentText, editPosition, editLength, username, version);
				client.updateText(documentText);
				client.updateVersion(version);

			}
		}
		}

	}

}
