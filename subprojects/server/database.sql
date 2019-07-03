CREATE TABLE sequence_set (
    id serial PRIMARY KEY,
    name VARCHAR(80), 
    created_date DATE NOT NULL DEFAULT CURRENT_DATE,
    last_modified_date DATE NOT NULL DEFAULT CURRENT_DATE,
    alphabet VARCHAR(4) NOT NULL,
    hash VARCHAR(80)
);

CREATE TABLE sequence (
    id serial PRIMARY KEY,
    identifier VARCHAR(80), 
    sequence TEXT,
    created_date DATE NOT NULL DEFAULT CURRENT_DATE,
    last_modified_date DATE NOT NULL DEFAULT CURRENT_DATE,
    hash VARCHAR(80)
);
