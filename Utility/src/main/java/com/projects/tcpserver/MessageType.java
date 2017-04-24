package com.projects.tcpserver;

public enum MessageType {

	IDPrefix("ID:"), ClientConnected("Connected"), MissingIdentifier("MissingIdentifier"), ClientAlreadyConnected("AlreadyConnected"), 
	TargetIdentifier("Target:"), ClientMessage("Message:"), Sender("Sender:"), Disconnect("Disconnect");
	
	private final String messageDescription;
	
	MessageType(final String message) {
		this.messageDescription = message;
	}
	
	public String getMessageDescription() {
		return messageDescription;
	}
	
}
