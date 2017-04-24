package com.projects.webservice;

import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import com.projects.tcpserver.MessageContainer;
import com.projects.tcpserver.Utility;
import com.projects.tcpserver.mongodb.MongoDBManager;

@WebService()
public class BackendWS extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Resource
    WebServiceContext wsctx;
	
	@WebMethod()
	public void storeMessages(final List<MessageContainer> messages) {
		((MongoDBManager)((ServletContext) wsctx.getMessageContext().get(MessageContext.SERVLET_CONTEXT)).getAttribute(Utility.MongoDBManagerServletContextAttributeName)).storeMessages(messages, false);
	}
	
	@WebMethod()
	public List<MessageContainer> getMessages(final String senderIdentifier) {
		return ((MongoDBManager)((ServletContext) wsctx.getMessageContext().get(MessageContext.SERVLET_CONTEXT)).getAttribute(Utility.MongoDBManagerServletContextAttributeName)).getMessages(senderIdentifier);
	}
}
