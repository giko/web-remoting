package org.kluge.repository;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import org.kluge.dto.DomainInfo;
import org.kluge.dto.SessionData;
import org.kluge.resource.ClientResource;

import javax.enterprise.context.ApplicationScoped;
import java.time.OffsetDateTime;

/**
 * This class provides a way to work with sessions persistence layer.
 */
@ApplicationScoped
public class EventsRepository {
    protected PgPool pgClient;

    public EventsRepository(PgPool pgClient) {
        this.pgClient = pgClient;
    }

    public Uni<Void> save(ClientResource.Event event) {
        return pgClient.preparedQuery("""
                        insert into events (event_name)
                        values ($1)
                        on conflict do nothing;
                        insert into session_events(session_id, event_id, event_time, event_data)
                        VALUES ($2, (select id from events where event_name = $1), now(), $3);
                                                """)
                .execute(Tuple.of(
                        event.eventName(),
                        event.sessionId(),
                        event.eventData()
                ))
                .replaceWithVoid();
    }

}
