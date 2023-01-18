CREATE TABLE IF NOT EXISTS session_info
(
    id        varchar(255) NOT NULL PRIMARY KEY,
    user_id   varchar(255),
    location  varchar(1023),
    domain    varchar(255),
    last_seen TIMESTAMPTZ
);

CREATE INDEX ON session_info (last_seen) STORING (domain);

CREATE VIEW IF NOT EXISTS session_info_actual AS
SELECT id, user_id, location, domain, last_seen
FROM session_info
WHERE last_seen > (now() - interval '30 seconds');