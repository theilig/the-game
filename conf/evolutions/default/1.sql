-- !Ups
CREATE TABLE Game (
    game_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    deck_id INTEGER NOT NULL,
    state TEXT NOT NULL
);

CREATE TABLE GamePlayers (
    user_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    game_id INTEGER NOT NULL,
    seat INTEGER NOT NULL,
    KEY game_id (game_id)
);

-- !Downs
DROP TABLE Game;
DROP TABLE GamePlayers;