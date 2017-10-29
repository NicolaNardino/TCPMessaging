package com.projects.tcpserver.restful;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.tcpserver.Utility;
import com.projects.tcpserver.jaxb.MessageContainer;
import com.projects.tcpserver.jaxb.MessageContainerList;

/**
 * Client class for the MessageRepository restful web service.
 * */
public final class MessageRepositoryRestfulClient implements MessageRepositoryClientInterface {

	private static final Logger logger = LoggerFactory.getLogger(MessageRepositoryRestfulClient.class);
	
	private final ResteasyClient client;
	private final String restfulBaseURL;
	private final String restfulWebserviceUsername;
	private final String restfulWebservicePassword;
	 
	public MessageRepositoryRestfulClient(final String restfulBaseURL, final String restfulWebserviceUsername,
	final String restfulWebservicePassword) {
		client = new ResteasyClientBuilder().build();
		this.restfulBaseURL = restfulBaseURL;
		this.restfulWebserviceUsername = restfulWebserviceUsername;
		this.restfulWebservicePassword = restfulWebservicePassword;
	}
	
	@Override
	public String getMessage(final String id) {
		final ResteasyWebTarget getMessage = client.target(restfulBaseURL + "getMessage/"+id);
		final Builder request = getMessage.request();
		setRequestHeaders(request);
		final Response response = request.get();
		final String randomMessage = response.readEntity(String.class);
		response.close();
		final int status = response.getStatus();
		if (status != Response.Status.OK.getStatusCode()) {
			logger.warn("Unable to get client message due to: "+randomMessage+", status code: "+status+"/ "+Response.Status.fromStatusCode(status));
			return null;
		}
		return randomMessage;
	}

	@Override
	public boolean storeMessages(final List<MessageContainer> messages) {
		final ResteasyWebTarget storeMessages = client.target(restfulBaseURL+"/storeMessages");
		final Builder request = storeMessages.request();
		setRequestHeaders(request);
		final Response response = request.post(Entity.entity(new MessageContainerList(messages), MediaType.APPLICATION_XML));
		response.close();
		return response.getStatus() == Response.Status.OK.getStatusCode();
	}

	@Override
	public List<MessageContainer> getMessages(final String id) {
		final ResteasyWebTarget getMessages = client.target(restfulBaseURL + "getMessages/"+id);
		final Builder request = getMessages.request(MediaType.APPLICATION_XML);
		setRequestHeaders(request);
		final Response response = request.get();
		final List<MessageContainer> messages = response.readEntity(new GenericType<List<MessageContainer>>() {});
		response.close();
		return messages;
	}

	private void setRequestHeaders(final Builder request) {
		request.header(Utility.BackendWSUsernameHeader, restfulWebserviceUsername);
		request.header(Utility.BackendWSPasswordHeader, restfulWebservicePassword);
	}
	
	public static void main(final String[] args) {
		final MessageRepositoryClientInterface cli = new MessageRepositoryRestfulClient("http://localhost:8080/RestfulBackend/rest/message/", "test_username", "test_password");
		System.out.println(cli.getMessage("rr"));
	}
}

