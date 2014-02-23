package com.robertcboll.dropwizard.daemon;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 *
 */
@Path("/")
public class EmptyResource {

    @GET
    public Response empty() {
        return Response.noContent().build();
    }
}
