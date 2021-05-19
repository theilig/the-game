-- !Ups
ALTER TABLE Game ADD COLUMN finished BOOLEAN;
-- !Downs
ALTER TABLE Game DROP COLUMN finished;
