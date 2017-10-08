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
	
	@POST
	@Path("/storeMessages")
	@Consumes(MediaType.APPLICATION_XML)
	public Response storeMessages(final MessageContainerList messages) {
		((MongoDBManager)sc.getAttribute(Utility.MongoDBManagerServletContextAttributeName)).storeMessages(messages.getMessages(), false);
		return Response.ok("added "+messages.getMessages().size()+" messages").build();
	}
	
	@GET
	@Path("/getMessages/{id: .*}")
	@Produces(MediaType.APPLICATION_XML)
	public List<MessageContainer> getMessages(@PathParam("id") final String id) {
		return ((MongoDBManager)sc.getAttribute(Utility.MongoDBManagerServletContextAttributeName)).getMessages(id);
	}

	@GET
	@Path("/getMessage/{id}")
	@Override
	public String getMessage(@PathParam("id") final String id) {
		return "Message_"+id+"_"+Utility.getRandom(999);
	}
}