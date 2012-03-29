# Users schema

# --- !Ups

CREATE TABLE user (
    id VARCHAR(255) NOT NULL,
    pwdhash VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE user;
