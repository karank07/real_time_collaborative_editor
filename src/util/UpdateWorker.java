package util;

import javax.swing.SwingWorker;

import client.Client;


@SuppressWarnings("unused")
public class UpdateWorker extends SwingWorker<Void, Void>{
	private Client client;
	private String message;
	private boolean sent;
	
	
	public UpdateWorker(Client client, String message, boolean sent){
		this.client = client;
		this.message = message;
		this.sent = sent;
	}
	
	
	protected Void doInBackground() {
		client.sendMessageToServer(message);
		done();
		return null;
	}
	

	@Override
	protected void done() {
		client.getMainWindow().repaint();
		sent = false;
		
	}
	

}
