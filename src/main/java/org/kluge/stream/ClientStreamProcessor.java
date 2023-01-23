package org.kluge.stream;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.kluge.dto.SessionData;
import org.kluge.dto.SessionEvent;
import org.kluge.repository.EventsRepository;
import org.kluge.repository.SessionsRepository;

import javax.enterprise.context.ApplicationScoped;

/**
 * Created on 16/01/22
 * Bean reactively processing clients data, saving it to cassandra
 *
 * @author Nikita Chudakov
 */
@ApplicationScoped
public class ClientStreamProcessor {
    protected final SessionsRepository sessionsRepository;
    protected final EventsRepository eventsRepository;

    public ClientStreamProcessor(
            SessionsRepository sessionsRepository,
            EventsRepository eventsRepository
    ) {
        this.sessionsRepository = sessionsRepository;
        this.eventsRepository = eventsRepository;
    }

    @Incoming("session-data")
    public Uni<Void> processSessionData(SessionData sessionData) {
        return sessionsRepository.save(sessionData);
    }

    @Incoming("session-events")
    public Uni<Void> processEvents(SessionEvent event) {
        return eventsRepository.save(event);
    }
}
