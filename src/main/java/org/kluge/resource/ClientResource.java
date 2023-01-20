package org.kluge.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.kluge.dto.ClientConfigResponse;
import org.kluge.dto.SessionInfo;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * This class is a REST resource that provides a way for a client to send session info to the server.
 *
 * @author Nikita Chudakov
 */
@Path("/client")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource {
    protected Vertx vertx;
    protected ObjectMapper objectMapper;
    protected Emitter<SessionInfo> sessionInfoEmitter;

    public ClientResource(
            Vertx vertx,
            ObjectMapper objectMapper,
            @Channel("session-info") Emitter<SessionInfo> sessionInfoEmitter
    ) {
        this.vertx = vertx;
        this.objectMapper = objectMapper;
        this.sessionInfoEmitter = sessionInfoEmitter;
    }

    @POST
    public Uni<Void> publishUserInfo(SessionInfo sessionInfo) {
        return Uni.createFrom().completionStage(sessionInfoEmitter.send(sessionInfo));
    }

    @GET
    @Path("/config")
    public Response getConfig(
            @CookieParam("clientId") Cookie clientIdCookie
    ) {
        var clientId = (clientIdCookie != null && clientIdCookie.getValue() != null) ?
                clientIdCookie.getValue() :
                UUID.randomUUID().toString();

        return Response
                .ok(new ClientConfigResponse(UUID.randomUUID().toString(), 500))
                .header("Set-Cookie", "clientId=" + clientId)
                .build();
    }

}
