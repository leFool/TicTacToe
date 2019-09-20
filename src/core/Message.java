package core;

import java.io.Serializable;

import enums.MessageType;

public class Message implements Serializable {

	private static final long serialVersionUID = -3513687731926233121L;

	Object message;
	MessageType type;

	public Message(MessageType type, Object message) {
		this.type = type;
		this.message = message;
	}

	public Message(MessageType type) {
		this(type, null);
	}

	public Message() {
		this(null, null);
	}

	public boolean isEmpty() {
		if (type == MessageType.MOVE && message == null)
			return true;
		return (type == null);
	}

	public void setMessageAndType(MessageType type, Object message) {
		this.type = type;
		this.message = message;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public MessageType getType() {
		return type;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public Object getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return String.format("[Type: %s, Content: %s]", type.toString(), message.toString());
	}

}
