package org.kluge.resource;

import io.smallrye.mutiny.Multi;
import org.jboss.resteasy.reactive.RestStreamElementType;
import org.kluge.dto.DomainInfo;
import org.kluge.dto.SessionInfo;
import org.kluge.repository.SessionInfoRepository;
import org.kluge.utils.KafkaMutinyConsumerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/session")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {
    protected final SessionInfoRepository sessionInfoRepository;
    protected final KafkaMutinyConsumerFactory kafkaMutinyConsumerFactory;

    public SessionResource(SessionInfoRepository sessionInfoRepository,
                           KafkaMutinyConsumerFactory kafkaMutinyConsumerFactory) {
        this.sessionInfoRepository = sessionInfoRepository;
        this.kafkaMutinyConsumerFactory = kafkaMutinyConsumerFactory;
    }

    @GET
    @Path("/domains")
    public Multi<DomainInfo> getDomainsInfo() {
        return sessionInfoRepository.getDomainsInfo();
    }

    @GET
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    @Path("/listen/{sessionId}")
    public Multi<SessionInfo> streamUserInfo(@PathParam("sessionId") String sessionId) {
        String topicPrefix = "session-";

        return kafkaMutinyConsumerFactory.getMultiConsumer(topicPrefix + sessionId, SessionInfo.class);
    }

}
