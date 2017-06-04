package com.projects.webservice.mongodb;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import com.projects.tcpserver.MessageContainer;
import com.projects.tcpserver.Utility;
import com.projects.tcpserver.mongodb.MongoDBManager;
import com.projects.tcpserver.webservice.CredentialsCheckException;

/**
 * It takes the MongoDB connection from a servlet context, initialized from a context listener.
 * */
@WebService()
@HandlerChain(file="soap/handler/handler-chain.xml")
public class BackendWS extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Resource
    WebServiceContext wsctx;
	
	@WebMethod()
	public void storeMessages(final List<MessageContainer> messages) throws CredentialsCheckException {
		final MessageContext messageContext = wsctx.getMessageContext();
		final ServletContext servletContext = (ServletContext) messageContext.get(MessageContext.SERVLET_CONTEXT);
		checkCredentials(messageContext, servletContext);
		((MongoDBManager)servletContext.getAttribute(Utility.MongoDBManagerServletContextAttributeName)).storeMessages(messages, false);
	}
	
	@WebMethod()
	public List<MessageContainer> getMessages(final String senderIdentifier) throws CredentialsCheckException {
		final MessageContext messageContext = wsctx.getMessageContext();
		final ServletContext servletContext = (ServletContext) messageContext.get(MessageContext.SERVLET_CONTEXT);
		checkCredentials(messageContext, servletContext);
		return ((MongoDBManager)servletContext.getAttribute(Utility.MongoDBManagerServletContextAttributeName)).getMessages(senderIdentifier);
	}
	
	private static void checkCredentials(final MessageContext messageContext, final ServletContext servletContext) throws CredentialsCheckException {
		@SuppressWarnings("unchecked")
		final Map<String, List<String>> requestHeaders = (Map<String, List<String>>)messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);
		if (requestHeaders != null) {
			final String username = requestHeaders.get(Utility.BackendWSUsernameHeader).get(0);
			final String password = requestHeaders.get(Utility.BackendWSPasswordHeader).get(0);
			final String servletContextBackendWSUsername = servletContext.getAttribute(Utility.BackendWSUsernameHeader).toString();
			final String servletContextBackendWSPassword = servletContext.getAttribute(Utility.BackendWSPasswordHeader).toString();
			if (!(username.equals(servletContextBackendWSUsername) && password.equals(servletContextBackendWSPassword)))
				throw new CredentialsCheckException("Failed credentials check.");
		}
	}
}
