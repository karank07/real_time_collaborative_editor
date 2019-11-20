package client;
import gui.MainView;

import javax.swing.SwingUtilities;


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
