package org.kluge.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.kluge.dto.ClientConfigResponse;
import org.kluge.dto.SessionData;

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
    protected final Vertx vertx;
    protected final ObjectMapper objectMapper;
    protected final Emitter<SessionData> sessionDataEmitter;
    protected final Emitter<Event> sessionEventsEmitter;

    public ClientResource(
            Vertx vertx,
            ObjectMapper objectMapper,
            @Channel("session-data") Emitter<SessionData> sessionDataEmitter,
            @Channel("session-events") Emitter<Event> sessionEventsEmitter
    ) {
        this.vertx = vertx;
        this.objectMapper = objectMapper;
        this.sessionDataEmitter = sessionDataEmitter;
        this.sessionEventsEmitter = sessionEventsEmitter;
    }

    @POST
    public Uni<Void> publishSession(
            @HeaderParam("X-session-id") UUID sessionId,
            SessionData sessionData
    ) {
        return Uni.createFrom().completionStage(sessionDataEmitter.send(sessionData));
    }

    @POST
    @Path("/events/{eventName}")
    public Uni<Void> publishEvent(
            @HeaderParam("X-session-id") UUID sessionId,
            @PathParam("eventName") String eventName,
            JsonObject eventData
    ) {
        var event = new Event(sessionId, eventName, eventData);
        return Uni.createFrom().completionStage(sessionEventsEmitter.send(event));
    }

    public record Event(UUID sessionId, String eventName, JsonObject eventData) {
    }

    @GET
    @Path("/config")
    public Response getConfig(
            @CookieParam("clientId") Cookie clientIdCookie
    ) {
        var clientId = (clientIdCookie != null && clientIdCookie.getValue() != null) ?
                UUID.fromString(clientIdCookie.getValue()) :
                UUID.randomUUID();

        return Response
                .ok(new ClientConfigResponse(UUID.randomUUID().toString(), clientId, 500))
                .header("Set-Cookie", "clientId=" + clientId)
                .build();
    }

}
