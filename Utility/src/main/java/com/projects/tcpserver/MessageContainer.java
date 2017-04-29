package com.projects.tcpserver;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MessageContainer")
@XmlRootElement(name="ChatMessage")
public class MessageContainer extends ChatMessage {

	@XmlElement(name = "senderIdentifier")
	private String senderIdentifier;
	@XmlElement(name = "sendDate")
	private Date sendDate;
	
	public MessageContainer() {}
	public MessageContainer(final String senderIdentifier, final String targetIdentifier, final String message, final Date sendDate) {
		super(targetIdentifier, message);
		this.senderIdentifier = senderIdentifier;
		this.sendDate = sendDate;
	}

	public String getSenderIdentifier() {
		return senderIdentifier;
	}

	public Date getSendDate() {
		return sendDate;
	}

	@Override
	public String toString() {
		return "MessageContainer [senderIdentifier=" + senderIdentifier + ", sendDate=" + sendDate + ", toString()="
				+ super.toString() + "]";
	}
}
