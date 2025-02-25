CREATE TABLE GAME (
                      gameId SERIAL PRIMARY KEY,
                      gameName VARCHAR(50) not null,
                      windowStart TIMESTAMPTZ not null,
                      windowClose TIMESTAMPTZ not null,
                      guessesClose TIMESTAMPTZ not null,
                      deliveryTime TIMESTAMPTZ null,
                      userId VARCHAR(50) not null,
                      gameActive BOOLEAN not null,
                      gameVoided BOOLEAN not null DEFAULT FALSE,
                      voidedReason VARCHAR(50)
);

CREATE TABLE GUESS (
                       guessId SERIAL PRIMARY KEY,
                       gameId INT not null,
                       userId VARCHAR(50) not null,
                       guessTime TIMESTAMPTZ not null,
                       CONSTRAINT fk_gameid
                           FOREIGN KEY (gameId)
                               REFERENCES GAME(gameId)
                               ON DELETE CASCADE,
                       CONSTRAINT game_and_guess_time UNIQUE (gameId, guessTime),
                       CONSTRAINT game_and_user_id UNIQUE (gameId, userId)
);

CREATE OR REPLACE FUNCTION check_gameid_and_guesstime()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT (NEW.guesstime BETWEEN (SELECT windowstart FROM game WHERE gameid = NEW.gameid) AND (SELECT windowclose FROM game WHERE gameid = NEW.gameid)) THEN
        RAISE EXCEPTION 'Guess time % is not between start and closing window range of the game', NEW.guesstime USING ERRCODE = 'ERRA1';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_gameid_and_guesstime_trigger
    BEFORE INSERT ON GUESS
    FOR EACH ROW
    EXECUTE FUNCTION check_gameid_and_guesstime();

CREATE TABLE WIN (
                     winId SERIAL PRIMARY KEY,
                     gameId INT not null,
                     guessId INT not null,
                     date TIMESTAMPTZ not null,
                     CONSTRAINT fk_gameid
                         FOREIGN KEY (gameId)
                             REFERENCES GAME(gameId)
                             ON DELETE CASCADE,
                     CONSTRAINT fk_guessid
                         FOREIGN KEY (guessId)
                             REFERENCES GUESS(guessId)
                             ON DELETE CASCADE,
                     CONSTRAINT game_and_guess_unique UNIQUE (gameId, guessId)
);


CREATE TABLE POINT
(
    pointId    SERIAL PRIMARY KEY,
    userId     VARCHAR(50) not null,
    played     INT         not null,
    won        INT         not null,
    lost       INT         not null,
    drawn       INT         not null,
    bonus       INT         not null,
    totalPoint DECIMAL(6, 1) not null,
    CONSTRAINT unique_user_id UNIQUE (userId)
);

CREATE USER pklbot WITH PASSWORD 'only_for_local_testing';
GRANT SELECT,INSERT,UPDATE, TRIGGER
      ON GAME, GUESS, WIN, POINT
      TO pklbot;

GRANT USAGE ON SEQUENCE game_gameid_seq, guess_guessid_seq, point_pointid_seq, win_winid_seq TO pklbot;