-- cockroachDB init script

CREATE TABLE users
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_visit timestamptz NOT NULL
);

CREATE TABLE sessions
(
    id        UUID          NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id   UUID          NOT NULL REFERENCES users (id),
    location  varchar(1023) NOT NULL,
    domain    varchar(255)  not null,
    last_seen TIMESTAMPTZ   NOT NULL
);

create table events
(
    id         uuid         not null primary key default gen_random_uuid(),
    event_name varchar(255) not null unique
);

CREATE table session_events
(
    session_id UUID        NOT NULL REFERENCES sessions (id),
    event_id   UUID        NOT NULL REFERENCES events (id),
    event_time timestamptz not null,
    event_data jsonb
);

CREATE INDEX ON sessions (last_seen) STORING (domain);

CREATE INDEX ON events (event_name);

CREATE VIEW IF NOT EXISTS session_info_actual AS
SELECT id, user_id, location, domain, last_seen
FROM sessions
WHERE last_seen > (now() - interval '30 seconds');