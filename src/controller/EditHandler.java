package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Edit;


public class EditHandler {
	private final Map<String, List<Edit>> editLogger;

	public EditHandler(){
		editLogger = Collections.synchronizedMap(new HashMap<String, List<Edit>>());
	
	}
	
	
	public synchronized void createNewlog(String documentName){
		editLogger.put(documentName, new ArrayList<Edit>());
	}
	
	
	public synchronized void logEdit(Edit edit){
		String documentName = edit.getDocumentName();
		editLogger.get(documentName).add(edit);
	}

	
	public synchronized String manageEdit(String documentName, int version,
			int offset) {
		List<Edit> list = editLogger.get(documentName);
		int updatedOffset = offset;
		for (Edit edit : list) {
			if (edit.getVersion() >= version) {
				updatedOffset = manageOffset(updatedOffset, edit.getOffset(),
						edit.getLength());
				version = edit.getVersion();
			}
		}
		String result = documentName+" "+(version+1)+" "+offset;
		return result;
	}
	
	private int manageOffset(int currentOffset, int otherOffset, int length) {
		if (currentOffset < otherOffset) {
			return currentOffset;
		} else if (currentOffset < otherOffset + length && currentOffset >= otherOffset) {
			return otherOffset;
		} else {
			return currentOffset + length;
		}
	}

	
	
	

}
