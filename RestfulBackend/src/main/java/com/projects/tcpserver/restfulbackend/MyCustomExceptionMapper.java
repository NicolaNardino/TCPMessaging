package com.projects.tcpserver.restfulbackend;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public final class MyCustomExceptionMapper implements ExceptionMapper<CredentialsCheckRuntimeException> {

    @Context
    private HttpHeaders headers;

    public Response toResponse(final CredentialsCheckRuntimeException e) {
        return Response.status(Response.Status.UNAUTHORIZED).
                entity(e.getMessage()).
                type(headers.getMediaType()).
                build();
    }

}
