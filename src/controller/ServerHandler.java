package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import model.Server;


/**
 * @author 
 * ServerHandler Class Initializes server with default port 9997, documentTextmap, documentVersions
 *
 */
public class ServerHandler {
	private static int defaultPort = 9997;

	
	public static void main(String[] args) {
		
		try {
			runServer(defaultPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void runServer(int port) throws IOException {
		
		Map<String, StringBuffer> documentTextmap = new HashMap<String, StringBuffer>();
		Map<String, Integer> documentVersions = new HashMap<String, Integer>();
		Server server = new Server(port, documentTextmap, documentVersions);
		server.serve();
	}
}
