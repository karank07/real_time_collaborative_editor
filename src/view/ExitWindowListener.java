package view;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import model.Client;

public class ExitWindowListener implements WindowListener {
	private final Client client;
	
	public ExitWindowListener(Client client){
		this.client = client;
	}
	
	
	@Override
	public void windowOpened(WindowEvent paramWindowEvent) {
		
	}



	@Override
	public void windowClosing(WindowEvent paramWindowEvent) {
		
		if(client != null && !client.getSocket().isClosed()){
		client.sendCommandToServer("bye");
		System.exit(0);
		}
	}


	@Override
	public void windowClosed(WindowEvent paramWindowEvent) {	
	}

	@Override
	public void windowIconified(WindowEvent paramWindowEvent) {	
	}

	@Override
	public void windowDeiconified(WindowEvent paramWindowEvent) {	
	}

	@Override
	public void windowActivated(WindowEvent paramWindowEvent) {
	}

	@Override
	public void windowDeactivated(WindowEvent paramWindowEvent) {
	}

}
