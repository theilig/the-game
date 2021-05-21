-- !Ups
ALTER TABLE Game CHANGE COLUMN finished finished BOOLEAN NOT NULL;
-- !Downs
ALTER TABLE Game CHANGE COLUMN finished finished BOOLEAN;
