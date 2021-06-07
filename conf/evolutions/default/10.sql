-- !Ups
ALTER TABLE UserConfirmation ADD COLUMN expiration DATETIME NOT NULL;

-- !Downs
ALTER TABLE UserConfirmation DROP COLUMN expiration;