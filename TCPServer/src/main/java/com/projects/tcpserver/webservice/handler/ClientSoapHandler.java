package com.projects.tcpserver.webservice.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 * Adds a sequence number to each out-bound message and, if enabled by configuration, allows to store message in the local filesystem. 
 * The only instance of this class runs in the MessageStore-Thread.
 * */
public final class ClientSoapHandler implements SOAPHandler<SOAPMessageContext> {

	public static int counter = 1;
	private static String messageStoreFilePath = "log/OutboundSoapMessages.log";
	private static final short messageBufferSize = 10;
	private static final List<String> messageBuffer = new ArrayList<>();
	
	private final boolean storeOutboundMessages;
	
	public ClientSoapHandler(final boolean storeOutboundMessages) {
		this.storeOutboundMessages = storeOutboundMessages;
	}
	
	@Override
	public boolean handleMessage(final SOAPMessageContext context) {
		if ((Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)) {
			try {
				final SOAPMessage soapMessage = context.getMessage();
				final SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
				SOAPHeader soapHeader = soapEnvelope.getHeader();
				if (soapHeader == null) 
					soapHeader = soapEnvelope.addHeader();
				final SOAPHeaderElement soapHeaderElement = soapHeader.addHeaderElement(new QName("http://http://github.com/nicolanardino/tcpmessaging", "sequence"));
				final int sequenceNumber = counter++;
				soapHeaderElement.addTextNode(String.valueOf(sequenceNumber));
				soapMessage.saveChanges();
				messageBuffer.add(new Date()+"/ "+sequenceNumber+"/ "+buildStringFromSoapMessage(soapMessage)+"/n");
				if (storeOutboundMessages && sequenceNumber%messageBufferSize == 0) {
					messageBuffer.add("\n\n");
					Files.write(Paths.get(messageStoreFilePath), messageBuffer, StandardOpenOption.CREATE,StandardOpenOption.APPEND);
					messageBuffer.clear();
				}
					
			} catch (final SOAPException | IOException e) {
				System.err.println(e);
			} 
		}
		return true;
	}
	
	private static String buildStringFromSoapMessage(final SOAPMessage soapMessage) {
		try(final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			soapMessage.writeTo(baos);
			return baos.toString();
		} catch (final IOException | SOAPException e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close(MessageContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
}
