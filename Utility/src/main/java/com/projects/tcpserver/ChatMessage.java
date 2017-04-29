package com.projects.tcpserver;

import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChatMessage")
@XmlRootElement(name="ChatMessage")
public class ChatMessage {
	
	@XmlElement(name = "targetIdentifier")
	private String targetIdentifier;
	@XmlElement(name = "message")
	private String message;
	
	public ChatMessage() {}
	
	public ChatMessage(final String targetIdentifier, final String message) {
		this.targetIdentifier = targetIdentifier;
		this.message = message;
	}
	
	public String getTargetIdentifier() {
		return targetIdentifier;
	}
	public String getMessage() {
		return message;
	}
	
	
	@Override
	public String toString() {
		return "ChatMessage [targetIdentifier=" + targetIdentifier + ", message=" + message + "]";
	}

	public static ChatMessage build(final String rawMessage) {
		final String temp[] = rawMessage.split(Pattern.quote("|"));
		final String targetIdentifier = Utility.getSubString(temp[0], MessageType.TargetIdentifier);
		return new ChatMessage(targetIdentifier, temp[1]);
	}
}
