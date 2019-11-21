package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import model.Edit;
import model.Server;
import model.Edit.Type;
import util.Encoding;

/**
 * @author ServerThreadHandler Class extends Thread
 *
 */
public class ServerThreadHandler extends Thread {

	final Socket socket;
	private boolean alive;
	private String username;
	private Server server;
	private String regex = "(bye)|(new [\\w\\d]+)|(look)|(open [\\w\\d]+)|(change .+)|(name [\\w\\d]+)";
	private String error1 = "Error: Document already exists.";
	private String error2 = "Error: No such document.";
	private String error3 = "Error: No documents exist yet.";
	private String error4 = "Error: Insert at invalid position.";
	private String error5 = "Error: You must enter a name when creating a new document.";
	private String error6 = "Error: Invalid arguments";
	private String error7 = "Error: Username is not available";
	private boolean sleep = false;

	public ServerThreadHandler(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
		this.alive = true;
	}

	@Override
	public void run() {
		try {
			handleConnection(socket);
		} catch (IOException e) {
		}
	}

	private void handleConnection(Socket socket) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

		try {
			for (String line = in.readLine(); line != null; line = in.readLine()) {
				String output = handleRequest(line);
				// for debugging only:
				if (sleep) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// If it's the bye message, terminate the connection
				if (output != null && output.equals("bye")) {
					out.close();
					in.close();
					server.removeThread(this);

				}

				// if it's the ChangText message,return the message to all other
				// alive clients
				else if (output != null && output.startsWith("change")) {
					server.returnMessageToEveryOtherClient(output, this);
				}

				// Otherwise, only send the message to the client that sends
				// this request.
				if (output != null) {
					out.println(output);
				}
			}
		} finally {
			out.close();
			in.close();
		}
	}

	public String handleRequest(String input) {
		if (!alive) {
			throw new RuntimeException("Should not get here since the client already disconnects.");
		}
		String returnMessage = "";
		input = input.trim();
		String[] tokens = input.split(" ");

		if (!input.matches(regex)) {
			// for invalid input
			// empty documentName
			if (tokens.length == 1 && tokens[0].equals("new")) {
				return error5;
			} else {
				return error6;
			}
		} else {
			if (tokens[0].equals("bye")) {
				// 'bye' request
				alive = false;
				returnMessage = "bye";

			} else if (tokens[0].equals("new")) {
				// 'new' request, make a new document if the name is valid. else, return a error
				// message.
				String documentName = tokens[1];

				if (server.getDocumentMap().containsKey(documentName)) {
					returnMessage = error1;
				} else {
					server.addNewDocument(documentName);
					returnMessage = "new " + documentName;
				}
			} else if (tokens[0].equals("name")) {
				if (server.nameIsAvailable(tokens[1])) {
					this.username = tokens[1];
					server.addUsername(this, tokens[1]);
					returnMessage = "name " + tokens[1];
				} else {
					returnMessage = error7;
				}

			} else if (tokens[0].equals("look")) {
				// 'look' request,
				// if server does not have any documents, return error message
				// else, return a string of names separated by a space
				String result = "alldocs";
				if (server.documentMapisEmpty()) {
					returnMessage = error3;
				} else {
					result = result + server.getAllDocuments();
					returnMessage = result;
				}

			} else if (tokens[0].equals("open")) {
				// 'open' request, must open a document if it exists on server
				String documentName = tokens[1];
				if (!server.getDocumentMap().containsKey(documentName)
						|| !server.getDocumentVersionMap().containsKey(documentName)) {
					returnMessage = error2;
				} else {
					int version = server.getVersion(documentName);
					String documentText = Encoding.encode(server.getDocumentText(documentName));
					returnMessage = "open " + documentName + " " + version + " " + documentText;
				}

			} else if (tokens[0].equals("change")) {
				// 'change' request, must change the string stored on the server if applicable
				int version = Integer.parseInt(tokens[3]);
				int offset, changeLength;
				Edit edit;
				String documentName = tokens[1];
				String editType = tokens[4];
				String username = tokens[2];
				if (!server.getDocumentMap().containsKey(documentName)
						|| !server.getDocumentVersionMap().containsKey(documentName)) {
					// if the server does not have the document
					returnMessage = error2;
				} else {
					Object lock = new Object();
					// only one thread should be in below because as we change the
					// version number, there might be race condition.
					// I.e., a thread check the version number is correct, but in
					// face that number is changed later on by another thread.
					synchronized (lock) {
						if (server.getVersion(documentName) != version) {
							// the client's document version is out of date
							// update the index relative to the previous inserts so that the change can be
							// inserted
							if (editType.equals("insert")) {
								offset = Integer.parseInt(tokens[6]);
							} else {
								offset = Integer.parseInt(tokens[5]);
							}
							String updates = server.manageEdit(documentName, version, offset);
							String[] updatedTokens = updates.split(" ");
							version = Integer.parseInt(updatedTokens[1]);
							offset = Integer.parseInt(updatedTokens[2]);
						}
						// then, the server could apply the (transformed) edit on document and return
						// messages.
						int length = server.getDocumentLength(documentName);
						if (editType.equals("remove")) {
							offset = Integer.parseInt(tokens[5]);
							int endPosition = Integer.parseInt(tokens[6]);
							// The server changes the document text:
							server.delete(documentName, offset, endPosition);
							changeLength = offset - endPosition; // negative
							edit = new Edit(documentName, Type.REMOVE, "", version, offset, changeLength);
							server.logEdit(edit);
							// server updates version number:
							server.updateVersion(documentName, version + 1);
							returnMessage = createMessage(documentName, username, version + 1, offset, changeLength,
									Encoding.encode(server.getDocumentText(documentName)));// encode the message!
						} else if (editType.equals("insert")) {
							Type type = Type.INSERT;
							offset = Integer.parseInt(tokens[6]);
							String text = Encoding.decode(tokens[5]);
							if (offset > length) {
								offset = length;
							}
							// the server updates the document text:
							server.insert(documentName, offset, text);
							changeLength = text.length();
							edit = new Edit(documentName, type, text, version, offset, changeLength);
							server.logEdit(edit);
							// the server updated the document version
							server.updateVersion(documentName, version + 1);
							returnMessage = createMessage(documentName, username, version + 1, offset, changeLength,
									Encoding.encode(server.getDocumentText(documentName)));
						}
					}
				}
			}
		}
		return returnMessage;
	}

	private String createMessage(String documentName, String username, int version, int offset, int changeLength,
			String documentText) {
		String message = "change " + documentName + " " + username + " " + version + " " + offset + " " + changeLength
				+ " " + documentText;
		return message;
	}

	public Socket getSocket() {
		return socket;
	}

	public String getUsername() {
		return username;
	}

	public boolean getAlive() {
		return alive;
	}
}
