# --- !Ups

-- -----------------------------------------------------
-- Table `ubeos`.`proposal_state`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`proposal_state` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `proposal_state` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `proposal_state` (`proposal_state` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `ubeos`.`request_state`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`request_state` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `request_state` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `request_state` (`request_state` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;

ALTER TABLE proposal
drop COLUMN state;

ALTER TABLE proposal add column
`proposal_state_id` BIGINT(20) NULL DEFAULT NULL,
 INDEX `FK_proposal_state` (`proposal_state_id` ASC),
  CONSTRAINT `FK_proposal_state`
    FOREIGN KEY (`proposal_state_id`)
    REFERENCES `ubeos`.`proposal_state` (`id`);

ALTER TABLE travel_request
drop COLUMN state;


ALTER TABLE travel_request add column
`request_state_id` BIGINT(20) NULL DEFAULT NULL,
 INDEX `FK_request_state` (`request_state_id` ASC),
  CONSTRAINT `FK_request_state`
    FOREIGN KEY (`request_state_id`)
    REFERENCES `ubeos`.`request_state` (`id`);

    
INSERT INTO country (code, enable) VALUES ('EN', true);

# --- !Downs

ALTER TABLE proposal
DROP COLUMN proposal_state_id;

ALTER TABLE proposal add column
`state` VARCHAR(255) NULL DEFAULT NULL;

ALTER TABLE travel_request
DROP COLUMN request_state_id;

ALTER TABLE travel_request add column
`state` VARCHAR(255) NULL DEFAULT NULL;

DROP TABLE proposal_state
MODIFY COLUMN zipcode BIGINT(20);

ALTER TABLE billing
MODIFY COLUMN zipcode BIGINT(20);