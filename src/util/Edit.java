package util;


public class Edit {

	public static enum Type {INSERT, REMOVE}

	private final String documentName;
	private final Type type;
	private final String text;
	private final int length;
	private final int offset;
	private final int version;

	public Edit(String documentName, Type editType, String text, int version,
			int offset, int length) {
		this.documentName = documentName;
		this.type = editType;
		this.text = text;
		this.offset = offset;
		this.length = length;
		this.version = version;
		checkRep();
	}
    

	private void checkRep() {
		assert documentName != null;
		assert type != null;
	}


	public Type getType() {
		return type;
	}

	
	public String getText() {
		return text;
	}

	
	public int getOffset() {
		return offset;
	}

	
	public int getLength() {
		return length;
	}


	public int getVersion() {
		return version;
	}


	public String getDocumentName() {
		return documentName;
	}

	public String toString() {
		return "Edit: " + documentName + " type: " + type + " v: " + version
				+ " offset: " + offset + " length: " + length + " text: " + text;
	}

}
