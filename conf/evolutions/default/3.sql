-- !Ups
DROP TABLE GamePlayers;

CREATE TABLE GamePlayers (
    player_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_id INTEGER NOT NULL,
    name VARCHAR(200) NOT NULL,
    game_id INTEGER NOT NULL,
    seat INTEGER NOT NULL,
    KEY game_key (game_id),
    UNIQUE KEY user_key (user_id)
);

-- !Downs
