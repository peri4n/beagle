-------------------------------------------
-- Sequences
-------------------------------------------
CREATE TABLE sequences (
    id serial PRIMARY KEY,
    identifier VARCHAR(255),
    sequence TEXT,
    created TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lastModified TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    alphabet VARCHAR(4) NOT NULL
);

-------------------------------------------
-- Data sets
-------------------------------------------
CREATE TABLE datasets (
    id serial PRIMARY KEY,
    name VARCHAR(255),
    created TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lastModified TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
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
    owner INTEGER REFERENCES users(id),
    created TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name, owner)
);

-------------------------------------------
-- Associations
-------------------------------------------
CREATE TABLE sequences_datasets_assoc (
    sequenceId INTEGER REFERENCES sequences(id),
    datasetId INTEGER REFERENCES datasets(id)
);

CREATE TABLE datasets_projects_assoc (
    datasetId INTEGER REFERENCES datasets(id),
    projectId INTEGER REFERENCES projects(id)
);


