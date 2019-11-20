package controller;

import java.io.IOException;

import javax.swing.JOptionPane;

import view.ConnectView;


public class ConnectionThread extends Thread {
    private final ConnectView connectView;

	public ConnectionThread(ConnectView connectView) {
		this.connectView=connectView;
	}

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


