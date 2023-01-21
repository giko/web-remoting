package org.kluge.repository;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import org.kluge.dto.DomainInfo;
import org.kluge.dto.SessionData;

import javax.enterprise.context.ApplicationScoped;
import java.time.OffsetDateTime;

/**
 * This class provides a way to work with sessions persistence layer.
 */
@ApplicationScoped
public class SessionsRepository {
    protected PgPool pgClient;

    public SessionsRepository(PgPool pgClient) {
        this.pgClient = pgClient;
    }

    public Uni<Void> save(SessionData sessionData) {
        return pgClient.preparedQuery("""
                        INSERT INTO sessions (id, user_id, location, domain, last_seen)
                            VALUES ($1, $2, $3, $4, $5)
                            ON CONFLICT (id) DO UPDATE SET location = $3, domain = $4, last_seen = $5;
                        """)
                .execute(Tuple.of(
                        sessionData.getId(),
                        sessionData.getUserId(),
                        sessionData.getLocation(),
                        sessionData.getDomain(),
                        OffsetDateTime.now()
                ))
                .replaceWithVoid();
    }

    public Multi<DomainInfo> getDomainsInfo() {
        return pgClient.query("SELECT domain, count(*) as count FROM session_info_actual GROUP BY domain")
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(row -> new DomainInfo(
                        row.getString("domain"),
                        row.getInteger("count")
                ));
    }
}
