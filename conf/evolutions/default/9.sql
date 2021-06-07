-- !Ups
ALTER TABLE User DROP COLUMN ip,
                 ADD COLUMN email VARCHAR(256) NOT NULL,
                 ADD UNIQUE KEY user_email (email);
CREATE TABLE UserConfirmation (
                                  user_confirmation_id INTEGER PRIMARY KEY AUTO_INCREMENT,
                                  token VARCHAR(256) NOT NULL,
                                  user_id INTEGER NOT NULL,
                                  UNIQUE KEY confirmation_user (user_id),
                                  UNIQUE KEY confirmation_token (token)
);
CREATE TABLE Tokens (
                        token_id INTEGER PRIMARY KEY AUTO_INCREMENT,
                        expires INTEGER NOT NULL,
                        user_id INTEGER NOT NULL,
                        token_value VARCHAR(256) NOT NULL,
                        UNIQUE KEY token_value (token_value),
                        UNIQUE KEY user_id (user_id)
);

-- !Downs
