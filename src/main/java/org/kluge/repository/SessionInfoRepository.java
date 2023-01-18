package org.kluge.repository;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import org.kluge.dto.DomainInfo;
import org.kluge.dto.SessionInfo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@ApplicationScoped
public class SessionInfoRepository {
    protected PgPool pgClient;

    public SessionInfoRepository(PgPool pgClient) {
        this.pgClient = pgClient;
    }

    public Uni<Void> save(SessionInfo sessionInfo) {
        return pgClient.preparedQuery("""
                        INSERT INTO session_info (id, user_id, location, domain, last_seen)
                            VALUES ($1, $2, $3, $4, $5)
                            ON CONFLICT (id) DO UPDATE SET location = $3, domain = $4, last_seen = $5;
                        """)
                .execute(Tuple.of(
                        sessionInfo.getId(),
                        sessionInfo.getUserId(),
                        sessionInfo.getLocation(),
                        sessionInfo.getDomain(),
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