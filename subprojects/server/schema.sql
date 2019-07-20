-------------------------------------------
-- Sequences
-------------------------------------------
CREATE TABLE sequences (
    id serial PRIMARY KEY,
    identifier VARCHAR(80),
    sequence TEXT,
    createdDate DATE NOT NULL DEFAULT CURRENT_DATE,
    lastModifiedDate DATE NOT NULL DEFAULT CURRENT_DATE,
    alphabet VARCHAR(4) NOT NULL
);

-------------------------------------------
-- Collections
-------------------------------------------
CREATE TABLE collections (
    id serial PRIMARY KEY,
    name VARCHAR(80), 
    createdDate DATE NOT NULL DEFAULT CURRENT_DATE,
    lastModifiedDate DATE NOT NULL DEFAULT CURRENT_DATE
);

-------------------------------------------
-- Projects
-------------------------------------------
CREATE TABLE projects (
    id serial PRIMARY KEY,
    name VARCHAR(80),
    createdDate DATE NOT NULL DEFAULT CURRENT_DATE,
);

-------------------------------------------
-- Users
-------------------------------------------
CREATE TABLE users (
    id serial PRIMARY KEY,
    username VARCHAR(80),
    password VARCHAR(80),
    createdDate DATE NOT NULL DEFAULT CURRENT_DATE,
);

-------------------------------------------
-- Associations
-------------------------------------------
CREATE TABLE sequences_collections_assoc (
    sequenceId INTEGER REFERENCES sequences(id),
    collectionId INTEGER REFERENCES collections(id)
);

CREATE TABLE collections_projects_assoc (
    collectionId INTEGER REFERENCES collections(id),
    projectId INTEGER REFERENCES projects(id)
);


