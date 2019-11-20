package view;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import model.Client;

public class OpenDocumentDialog extends JOptionPane {
	private static final long serialVersionUID = 1L;


	public OpenDocumentDialog(ArrayList<String> documentNames, Client client) {

		
		if (documentNames == null) {
			JOptionPane.showMessageDialog(null, "There is no document on the server yet", "Error",
					JOptionPane.ERROR_MESSAGE);
		}

		else {
		
			Object[] documentsOnServer = new Object[documentNames.size()];
			for (int i = 0; i < documentNames.size(); i++) {
				documentsOnServer[i] = documentNames.get(i);
			}

			String s = (String) JOptionPane.showInputDialog(null, "Choose a document:\n", "Open a document dialog",
					JOptionPane.PLAIN_MESSAGE, icon, documentsOnServer, documentsOnServer[0]);
			
			if (s != null) {
				client.sendCommandToServer("open " + s);
			}
		}
	}
}
