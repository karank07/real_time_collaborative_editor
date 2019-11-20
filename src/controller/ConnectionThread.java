package controller;

import java.io.IOException;

import javax.swing.JOptionPane;

import view.ConnectView;


/**
 * @author 
 * ConnectionThread class extends Thread, creates thread for each client on successful connection, to process incoming request 
 *
 */
public class ConnectionThread extends Thread {
    private ConnectView connectView;

	public ConnectionThread(ConnectView connectView) {
		this.connectView=connectView;
	}

	@Override
	public void run() {
		
		try {
			connectView.getClient().start();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
				    e.getMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		} catch (IllegalArgumentException e1){
			JOptionPane.showMessageDialog(null,
				    "Illegal arguments",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
	}
}


