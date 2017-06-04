package com.projects.tcpserver.webservice.soap.handler;

import java.util.Arrays;
import java.util.List;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

public final class SoapHandlerResolver implements HandlerResolver {

	private final boolean storeOutboundMessages;
	
	public SoapHandlerResolver(final boolean storeOutboundMessages) {
		this.storeOutboundMessages = storeOutboundMessages;
	}
	
    @SuppressWarnings("rawtypes")
	public List<Handler> getHandlerChain(PortInfo portInfo) {
    	return Arrays.asList(new ClientSoapHandler(storeOutboundMessages));
      }
    }
