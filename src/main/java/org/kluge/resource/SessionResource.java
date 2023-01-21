package org.kluge.resource;

import io.smallrye.mutiny.Multi;
import org.jboss.resteasy.reactive.RestStreamElementType;
import org.kluge.dto.DomainInfo;
import org.kluge.dto.SessionData;
import org.kluge.repository.SessionsRepository;
import org.kluge.utils.KafkaMutinyConsumerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/session")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {
    protected final SessionsRepository sessionsRepository;
    protected final KafkaMutinyConsumerFactory kafkaMutinyConsumerFactory;

    public SessionResource(SessionsRepository sessionsRepository,
                           KafkaMutinyConsumerFactory kafkaMutinyConsumerFactory) {
        this.sessionsRepository = sessionsRepository;
        this.kafkaMutinyConsumerFactory = kafkaMutinyConsumerFactory;
    }

    @GET
    @Path("/domains")
    public Multi<DomainInfo> getDomainsInfo() {
        return sessionsRepository.getDomainsInfo();
    }

    @GET
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    @Path("/listen/{sessionId}")
    public Multi<SessionData> streamUserInfo(@PathParam("sessionId") String sessionId) {
        String topicPrefix = "session-";

        return kafkaMutinyConsumerFactory.getMultiConsumer(topicPrefix + sessionId, SessionData.class);
    }

}
