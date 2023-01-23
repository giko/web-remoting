package org.kluge.repository;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import org.kluge.dto.DomainInfo;
import org.kluge.dto.SessionData;

import javax.enterprise.context.ApplicationScoped;
import java.time.OffsetDateTime;
import java.util.UUID;

@ApplicationScoped
public class UsersRepository {
    protected PgPool pgClient;

    public UsersRepository(PgPool pgClient) {
        this.pgClient = pgClient;
    }

    public Uni<UUID> createNew() {
        return pgClient.preparedQuery("""
                        INSERT INTO users (first_visit)
                            VALUES (now())
                            returning id;
                        """)
                .execute()
                .onItem()
                .transformToUni(set -> Uni.createFrom().item(set.iterator().next().getUUID("id")));
    }
}
