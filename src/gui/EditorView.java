package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

import client.Client;
import util.Encoding;
import util.UpdateWorker;


public class EditorView extends JPanel {

	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private JMenuBar menu;
	private JMenu file, edit;
	private JMenuItem newfile, open, exit; 
	private JLabel documentNameLabel;
	private String documentName, documentText;
	private JTextArea area;
	private JScrollPane scrollpane;
	private DefaultCaret caret;
	private TextDocumentListener documentListener;
	private final Client client;
	private final String username;
	private int currentVersion;
	private boolean sent = false; //used in cursor managing
	
	
	public EditorView(MainView frame) {
		this.frame = frame;
		this.client = null;
		this.username = "";
		documentNameLabel = new JLabel("You are editing document: ");
		createLayout();
	}

	
	public EditorView(MainView frame, String documentName, String text) {
	
		this.frame = frame;
		this.client = frame.getClient();
		this.documentName = documentName;
		this.username = frame.getUsername();
		documentText = Encoding.decode(text);
		documentNameLabel = new JLabel("<html><B>"+documentName+"</B></html>");
		createLayout();
	}

	
	private void createLayout() {
		

		
		caret = new DefaultCaret();
		area = new JTextArea(25, 65);
		area.setLineWrap(true);
		area.setText(documentText);
		area.setWrapStyleWord(true);
		
		
		area.setCaret(caret);
		documentListener = new TextDocumentListener();
		area.getDocument().addDocumentListener(documentListener); 
		
		scrollpane = new JScrollPane(area);
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(documentNameLabel)
				.addComponent(scrollpane));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(documentNameLabel)
				.addComponent(scrollpane));
	}


	
	private class TextDocumentListener implements DocumentListener {
		
		public void insertUpdate(DocumentEvent e) {
			synchronized (area) {
				int changeLength = e.getLength();
				int offset = e.getOffset();
				int insert = caret.getDot();
				String message;
				try {
					String addedText = area.getDocument().getText(offset,
							changeLength);
					String encodedText = Encoding.encode(addedText);					
					currentVersion=client.getVersion();
					message = "change " + documentName + " "+username+" "+ currentVersion+ " insert " + encodedText
							+ " " + insert;
					
					sent = true;
	            	UpdateWorker worker = new UpdateWorker(client,
							message, sent);
					worker.execute(); 
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}
		}
	
		public void removeUpdate(DocumentEvent e) {
			synchronized (area) {
				int changeLength = e.getLength();				
				currentVersion=client.getVersion();			
				int offset = e.getOffset();
				int endPosition = offset + changeLength;
				String message = "change " + documentName +" "+username+" " +currentVersion+" remove " + offset
						+ " " + endPosition;

			
				sent = true;
            	UpdateWorker worker = new UpdateWorker(client,
						message, sent);
				client.updateVersion(currentVersion+1);
				worker.execute();
			}
		}
		
		public void changedUpdate(DocumentEvent e) {
			
		}
	}


	private void manageCursor(int currentPos, int pivotPosition, int amount) {
	
		if (currentPos >= pivotPosition) {
			if (currentPos <= pivotPosition + Math.abs(amount)) {
				caret.setDot(pivotPosition);
			} else {
				caret.setDot(amount+currentPos);
			}
		}
		else{
			caret.setDot(currentPos);
		}
	}

	
	public void updateDocument(String updatedText, int editPosition,
			int editLength, String username, int version) {
		documentText = Encoding.decode(updatedText);
		int pos = caret.getDot();
		synchronized (area) {
			if(this.username!=null && !this.username.equals(username)){
			area.getDocument().removeDocumentListener(documentListener);
			area.setText(documentText);
			area.getDocument().addDocumentListener(documentListener);
			manageCursor(pos, editPosition, editLength);
			}
			else if(this.username!=null && this.username.equals(username)) {
				//check if version matches up
				if(currentVersion<version-1){
					area.getDocument().removeDocumentListener(documentListener);
					area.setText(documentText);
					area.getDocument().addDocumentListener(documentListener);
					caret.setDot(editPosition+editLength);
				}
				
			}

		}
	}
	
		public void actionPerformed(ActionEvent e) {
			area.cut();
		}
	}

