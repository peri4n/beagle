--liquibase formatted sql

--changeset fbull:1
--comment: Create initial tables
CREATE TABLE IF NOT EXISTS users
(
    id serial PRIMARY KEY,
    username  VARCHAR(255) UNIQUE NOT NULL,
    password  VARCHAR(255),
    email     VARCHAR(255),
    created   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS projects
(
    id       serial PRIMARY KEY,
    name     VARCHAR(255) UNIQUE NOT NULL,
    owner_id INTEGER REFERENCES users (id),
    created  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS datasets
(
    id serial PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    owner_id INTEGER REFERENCES users(id),
    project_id INTEGER REFERENCES projects(id),
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
--rollback drop table users; drop table projects; drop table datasets
