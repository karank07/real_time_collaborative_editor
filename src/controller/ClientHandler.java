package controller;
import javax.swing.SwingUtilities;

import view.MainView;


public class ClientHandler {
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	MainView main = new MainView();
				main.setVisible(true);
		    }
		});

	} 
}
