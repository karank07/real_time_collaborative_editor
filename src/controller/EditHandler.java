package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Edit;

/**
 * @author EditHandler Class has methods for handling conflicts and maintains
 *         concurrency by calculating edit offset position on each edit
 *         operation by checking the current version.(Operational Transformation
 *         technique)
 */
public class EditHandler {
	private Map<String, List<Edit>> editLogger;

	public EditHandler() {
		editLogger = Collections.synchronizedMap(new HashMap<String, List<Edit>>());

	}

	/**
	 * Thread Safe method to calculate new offset based on version change 
	 * @param documentName
	 * @param version
	 * @param offset
	 * @return result- calculated offset
	 * 
	 */
	public synchronized String editHandler(String documentName, int version, int offset) {
		List<Edit> list = editLogger.get(documentName);
		int newOffset = offset;
		for (Edit edit : list) {
			if (edit.getVersion() >= version) {
				newOffset = caclulateNewOffset(newOffset, edit.getOffset(), edit.getLength());
				version = edit.getVersion();
			}
		}
		String result = documentName + " " + (version + 1) + " " + offset;
		return result;
	}

	/**
	 * Edits current log
	 * @param edit
	 */
	public synchronized void editLog(Edit edit) {
		String documentName = edit.getDocumentName();
		editLogger.get(documentName).add(edit);
	}

	/**
	 * Adds new log for new edits
	 * @param documentName
	 */
	public synchronized void createLog(String documentName) {
		editLogger.put(documentName, new ArrayList<Edit>());
	}

	private int caclulateNewOffset(int currentOffset, int otherOffset, int length) {
		if (currentOffset < otherOffset + length && currentOffset >= otherOffset) {
			return otherOffset;
		} else if (currentOffset < otherOffset) {
			return currentOffset;
		} else {
			return currentOffset + length;
		}
	}

}
