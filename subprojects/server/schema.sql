CREATE TYPE alphabet AS ENUM ('dna', 'rna', 'amino');

-------------------------------------------
-- Sequences
-------------------------------------------
CREATE TABLE sequences (
    id serial PRIMARY KEY,
    identifier VARCHAR(255),
    sequence TEXT,
    created TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    alphabet alphabet NOT NULL
);

-------------------------------------------
-- Users
-------------------------------------------
CREATE TABLE users (
    id serial PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    email VARCHAR(255),
    created TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-------------------------------------------
-- Projects
-------------------------------------------
CREATE TABLE projects (
    id serial PRIMARY KEY,
    name VARCHAR(255),
    owner_id INTEGER REFERENCES users(id),
    created TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name, owner_id)
);

-------------------------------------------
-- Data sets
-------------------------------------------
CREATE TABLE datasets (
    id serial PRIMARY KEY,
    name VARCHAR(255),
    project_id INTEGER REFERENCES projects(id),
    created TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name, project_id)
);

-------------------------------------------
-- Associations
-------------------------------------------
CREATE TABLE sequences_datasets_assoc (
    sequence_id INTEGER REFERENCES sequences(id),
    dataset_id INTEGER REFERENCES datasets(id)
);

CREATE TABLE datasets_projects_assoc (
    dataset_id INTEGER REFERENCES datasets(id),
    project_id INTEGER REFERENCES projects(id)
);


