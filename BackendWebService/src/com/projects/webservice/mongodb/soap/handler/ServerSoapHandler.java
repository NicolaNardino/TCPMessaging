package com.projects.webservice.mongodb.soap.handler;

import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import com.projects.tcpserver.Utility;

/**
 * Checks whether the received SOAP message contains the sequence header.
 * */
public final class ServerSoapHandler implements SOAPHandler<SOAPMessageContext>{

	@Override
	public boolean handleMessage(final SOAPMessageContext context) {
		boolean headerFound = false;
		//inbound messages.
		if(!(Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)){
			try{
				final SOAPMessage soapMessage = context.getMessage();
				final SOAPHeader soapHeader = soapMessage.getSOAPPart().getEnvelope().getHeader();
				final Iterator<?> headerIterator = soapHeader.extractAllHeaderElements();
				while (headerIterator.hasNext()) {
					final Node node = (Node)headerIterator.next();
					if (node.getNodeName().contains(Utility.SoapHeaderSequenceName)) {
						System.out.println("Message sequence: "+node.getValue());
						headerFound = true;
						break;
					}
				}
				if (!headerFound) {
					System.out.println("Following SOAP message arrived without sequence header:\n"+Utility.buildStringFromSoapMessage(soapMessage));
					final SOAPFault soapFault = soapMessage.getSOAPPart().getEnvelope().getBody().addFault();
					soapFault.setFaultString("Received message is without sequence number.");
					throw new SOAPFaultException(soapFault);
				}
			}
			catch(final SOAPException e) {
				System.err.println(e);
			}
		}
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override
	public void close(MessageContext context) {
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}
}
