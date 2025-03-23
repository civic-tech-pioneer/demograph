CREATE TABLE users
(
    name     text PRIMARY KEY,
    version  int,
    password text,
    roles    text[]
);

CREATE UNIQUE INDEX username_idx ON users (name);

CREATE TABLE elements
(
    id       UUID PRIMARY KEY,
    version  int,
    text     text,
    owner_id text
);

CREATE TABLE links
(
    id         UUID PRIMARY KEY,
    version    int,
    source_ref UUID,
    target_ref UUID,
    owner_id   text
);

CREATE INDEX link_source_idx ON links (source_ref);
CREATE INDEX link_target_idx ON links (target_ref);

CREATE TABLE attitudes
(
    id                  UUID PRIMARY KEY,
    version             int,
    owner_name          text,
    contestable_id      UUID,
    histogram_centers   double precision[],
    histogram_fractions double precision[]
);

CREATE INDEX attitude_contestable_idx ON attitudes (contestable_id);