package controller;

import model.Client;

public class WelcomeViewThread extends Thread {

	private final String message;
	private final Client client;
	
	public WelcomeViewThread(Client client, String message) {

		this.message = message;
		this.client = client;
	}


	public void run() {
		client.sendCommandToServer(message);
	}

}
