package com.projects.tcpserver.restfulbackend;

import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.projects.tcpserver.Utility;
import com.projects.tcpserver.jaxb.MessageContainer;
import com.projects.tcpserver.jaxb.MessageContainerList;
import com.projects.tcpserver.mongodb.MongoDBManager;
import com.projects.tcpserver.restful.MessageRepositoryServerInterface;

@Path("/message")
public final class MessageRepositoryImpl implements MessageRepositoryServerInterface {

	@Context 
	ServletContext sc;
	@Context 
	HttpHeaders httpHeaders;
	
	@POST
	@Path("/storeMessages")
	@Consumes(MediaType.APPLICATION_XML)
	public Response storeMessages(final MessageContainerList messages) {
		checkCredentials();
		((MongoDBManager)sc.getAttribute(Utility.MongoDBManagerServletContextAttributeName)).storeMessages(messages.getMessages(), false);
		return Response.ok("added "+messages.getMessages().size()+" messages").build();
	}
	
	@GET
	@Path("/getMessages/{id:.*}")
	@Produces(MediaType.APPLICATION_XML)
	public List<MessageContainer> getMessages(@PathParam("id") final String id) {
		checkCredentials();
		String temp = id.equals("null") ? null : id;	
		return ((MongoDBManager)sc.getAttribute(Utility.MongoDBManagerServletContextAttributeName)).getMessages(id.equals("null") ? null : id);
	}

	@GET
	@Path("/getMessage/{id}")
	@Override
	public String getMessage(@PathParam("id") final String id) {
		checkCredentials();
		return "Message_"+id+"_"+Utility.getRandom(999);
	}
	
	private void checkCredentials() {
		if (httpHeaders != null) {
			final String username = httpHeaders.getRequestHeader(Utility.BackendWSUsernameHeader).get(0);
			final String password = httpHeaders.getRequestHeader(Utility.BackendWSPasswordHeader).get(0);
			final String servletContextBackendWSUsername = sc.getAttribute(Utility.BackendWSUsernameHeader).toString();
			final String servletContextBackendWSPassword = sc.getAttribute(Utility.BackendWSPasswordHeader).toString();
			if (!(username.equals(servletContextBackendWSUsername) && password.equals(servletContextBackendWSPassword)))
				throw new CredentialsCheckRuntimeException("Failed credentials check.");
		}
	}
}