CREATE TABLE SEASON
(
    seasonId SERIAL PRIMARY KEY,
    name VARCHAR(100) not null,
    startDate TIMESTAMPTZ not null,
    endDate TIMESTAMPTZ not null,
    active BOOLEAN not null DEFAULT FALSE
);

INSERT INTO SEASON VALUES(
    1,
    'PaketLiga Original Season',
    to_timestamp('22/02/2025', 'DD/MM/YYYY'),
    to_timestamp('21 Dec 2025 15:03 GMT', 'DD Mon YYYY HH24:MI TZR'),
    true
);

ALTER TABLE GAME ADD COLUMN seasonId INT NOT NULL DEFAULT 1;

ALTER TABLE GAME
    ADD CONSTRAINT fk_seasonid
        FOREIGN KEY (seasonId)
            REFERENCES SEASON(seasonId)
            ON DELETE CASCADE;

ALTER TABLE POINT
    ADD COLUMN seasonId INT NOT NULL DEFAULT 1,
    ADD CONSTRAINT fk_seasonid
        FOREIGN KEY (seasonId)
            REFERENCES SEASON(seasonId)
            ON DELETE CASCADE,
    ADD CONSTRAINT unique_userid_and_seasonid UNIQUE (userId, seasonId),
    DROP CONSTRAINT unique_user_id;
