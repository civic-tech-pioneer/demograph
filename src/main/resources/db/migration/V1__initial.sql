CREATE TABLE users
(
    id       UUID PRIMARY KEY,
    version  int,
    name     text,
    password text,
    roles    text[]
);

CREATE UNIQUE INDEX username_idx ON users (name);

CREATE TABLE elements
(
    id      UUID PRIMARY KEY,
    version int,
    text    text
);

CREATE TABLE links
(
    id         UUID PRIMARY KEY,
    version    int,
    source_ref UUID,
    target_ref UUID
);

CREATE INDEX link_source_idx ON links (source_ref);
CREATE INDEX link_target_idx ON links (target_ref);