# --- !Ups

ALTER TABLE ubeos_user
MODIFY COLUMN zipcode varchar(10);

ALTER TABLE billing
MODIFY COLUMN zipcode varchar(10);

# --- !Downs

ALTER TABLE ubeos_user
MODIFY COLUMN zipcode BIGINT(20);

ALTER TABLE billing
MODIFY COLUMN zipcode BIGINT(20);