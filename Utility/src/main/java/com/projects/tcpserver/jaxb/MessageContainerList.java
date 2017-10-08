package com.projects.tcpserver.jaxb;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MessageContainerList")
@XmlRootElement(name="MessageContainerList")
public class MessageContainerList {

	public MessageContainerList(final List<MessageContainer> messages) {
		this.messages = messages;
	}

	@XmlElement(name = "messages")
	private List<MessageContainer> messages = new ArrayList<>();

	public MessageContainerList() {
	}

	public List<MessageContainer> getMessages() {
		return messages;
	}

	@Override
	public String toString() {
		return "MessageContainerList [messages=" + messages.stream().map(MessageContainer::toString).collect(Collectors.joining(", ")) + "]";
	}
}
