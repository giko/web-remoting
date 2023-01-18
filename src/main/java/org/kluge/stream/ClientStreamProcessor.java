package org.kluge.stream;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.kluge.dto.SessionInfo;
import org.kluge.repository.SessionInfoRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Created on 16/01/22
 * Bean reactively processing clients data, saving it to cassandra
 *
 * @author Nikita Chudakov
 */
@ApplicationScoped
public class ClientStreamProcessor {
    protected SessionInfoRepository sessionInfoRepository;

    public ClientStreamProcessor(SessionInfoRepository sessionInfoRepository) {
        this.sessionInfoRepository = sessionInfoRepository;
    }

    @Incoming("session-info")
    public Uni<Void> processSessionInfo(SessionInfo sessionInfo) {
        return sessionInfoRepository.save(sessionInfo);
    }
}
