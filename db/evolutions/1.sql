# --- !Ups
 
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema ubeos
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `ubeos` DEFAULT CHARACTER SET utf8 ;
USE `ubeos` ;

-- -----------------------------------------------------
-- Table `ubeos`.`accommodation`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`accommodation` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `enable` BIT(1) NULL DEFAULT NULL,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name` (`name` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ubeos`.`user_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`user_role` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name` (`name` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ubeos`.`avatar`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`avatar` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `avatar` VARCHAR(255) NULL DEFAULT NULL,
  `user_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKAC32C15947140EFE` (`user_id` ASC),
  CONSTRAINT `FKAC32C15947140EFE`
    FOREIGN KEY (`user_id`)
    REFERENCES `ubeos`.`ubeos_user` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ubeos`.`country`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`country` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `code` VARCHAR(255) NULL DEFAULT NULL,
  `enable` BIT(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `code` (`code` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ubeos`.`ubeos_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`ubeos_user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `nif` BIGINT(20) NULL,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `address` VARCHAR(255) NULL DEFAULT NULL,
  `birthdate` DATETIME NULL DEFAULT NULL,
  `city` VARCHAR(255) NULL DEFAULT NULL,
  `email` VARCHAR(255) NULL DEFAULT NULL,
  `enable` BIT(1) NOT NULL,
  `first_name` VARCHAR(255) NULL DEFAULT NULL,
  `password` VARCHAR(255) NULL DEFAULT NULL,
  `lang` VARCHAR(255) NULL DEFAULT NULL,
  `last_name` VARCHAR(255) NULL DEFAULT NULL,
  `nrating` BIGINT(20) NULL DEFAULT NULL,
  `rating` DOUBLE NULL DEFAULT NULL,
  `zipcode` BIGINT(20) NULL DEFAULT NULL,
  `avatar_id` BIGINT(20) NULL DEFAULT NULL,
  `country_id` BIGINT(20) NULL DEFAULT NULL,
  `userRole_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email` (`email` ASC),
  INDEX `FK1D1C8ACE6872E55E` (`userRole_id` ASC),
  INDEX `FK1D1C8ACE9B84A56` (`country_id` ASC),
  INDEX `FK1D1C8ACE828FC33E` (`avatar_id` ASC),
  CONSTRAINT `FK1D1C8ACE6872E55E`
    FOREIGN KEY (`userRole_id`)
    REFERENCES `ubeos`.`user_role` (`id`),
  CONSTRAINT `FK1D1C8ACE828FC33E`
    FOREIGN KEY (`avatar_id`)
    REFERENCES `ubeos`.`avatar` (`id`),
  CONSTRAINT `FK1D1C8ACE9B84A56`
    FOREIGN KEY (`country_id`)
    REFERENCES `ubeos`.`country` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ubeos`.`activation`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`activation` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `enable` BIT(1) NOT NULL,
  `token` VARCHAR(255) NULL DEFAULT NULL,
  `user_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK79AA811647140EFE` (`user_id` ASC),
  CONSTRAINT `FK79AA811647140EFE`
    FOREIGN KEY (`user_id`)
    REFERENCES `ubeos`.`ubeos_user` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ubeos`.`budget_range`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`budget_range` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `rangeWindow` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `rangeWindow` (`rangeWindow` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ubeos`.`consumer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`consumer` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `user_id` BIGINT(20) NULL DEFAULT NULL,
  `gender` VARCHAR(255) NOT NULL DEFAULT 'male',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `user_id` (`user_id` ASC),
  INDEX `FKDE2883F647140EFE` (`user_id` ASC),
  CONSTRAINT `FKDE2883F647140EFE`
    FOREIGN KEY (`user_id`)
    REFERENCES `ubeos`.`ubeos_user` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ubeos`.`meal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`meal` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `enable` BIT(1) NULL DEFAULT NULL,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name` (`name` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ubeos`.`transport`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`transport` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `enable` BIT(1) NULL DEFAULT NULL,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name` (`name` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ubeos`.`travel_request`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`travel_request` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `adults` INT(11) NULL DEFAULT NULL,
  `children` INT(11) NULL DEFAULT NULL,
  `city_from` VARCHAR(255) NULL DEFAULT NULL,
  `city_to` VARCHAR(255) NULL DEFAULT NULL,
  `datefrom` DATE NULL DEFAULT NULL,
  `dateto` DATE NULL DEFAULT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `ispublic` BIT(1) NULL DEFAULT NULL,
  `state` VARCHAR(255) NULL DEFAULT NULL,
  `title` VARCHAR(255) NULL DEFAULT NULL,
  `accommodation_id` BIGINT(20) NULL DEFAULT NULL,
  `budget_id` BIGINT(20) NULL DEFAULT NULL,
  `consumer_id` BIGINT(20) NULL DEFAULT NULL,
  `country_from_id` BIGINT(20) NULL DEFAULT NULL,
  `country_to_id` BIGINT(20) NULL DEFAULT NULL,
  `meal_id` BIGINT(20) NULL DEFAULT NULL,
  `transport_id` BIGINT(20) NULL DEFAULT NULL,
  `enable` BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (`id`),
  INDEX `FKD6495E6A17740BD6` (`accommodation_id` ASC),
  INDEX `FKD6495E6AB5021676` (`transport_id` ASC),
  INDEX `FKD6495E6A87BF6BFE` (`meal_id` ASC),
  INDEX `FKD6495E6A9B84A56` (`country_from_id` ASC),
  INDEX `FKD6495E6A9B84_to` (`country_to_id` ASC),
  INDEX `FKD6495E6A4679691E` (`consumer_id` ASC),
  INDEX `FKD6495E6A109097A9` (`budget_id` ASC),
  CONSTRAINT `FKD6495E6A109097A9`
    FOREIGN KEY (`budget_id`)
    REFERENCES `ubeos`.`budget_range` (`id`),
  CONSTRAINT `FKD6495E6A17740BD6`
    FOREIGN KEY (`accommodation_id`)
    REFERENCES `ubeos`.`accommodation` (`id`),
  CONSTRAINT `FKD6495E6A4679691E`
    FOREIGN KEY (`consumer_id`)
    REFERENCES `ubeos`.`consumer` (`id`),
  CONSTRAINT `FKD6495E6A87BF6BFE`
    FOREIGN KEY (`meal_id`)
    REFERENCES `ubeos`.`meal` (`id`),
  CONSTRAINT `FKD6495E6A9B84A56`
    FOREIGN KEY (`country_from_id`)
    REFERENCES `ubeos`.`country` (`id`),
  CONSTRAINT `FKD6495E6A9B84_to`
    FOREIGN KEY (`country_to_id`)
    REFERENCES `ubeos`.`country` (`id`),
  CONSTRAINT `FKD6495E6AB5021676`
    FOREIGN KEY (`transport_id`)
    REFERENCES `ubeos`.`transport` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `ubeos`.`travel_request_tmp`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`travel_request_tmp` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `adults` INT(11) NULL DEFAULT NULL,
  `children` INT(11) NULL DEFAULT NULL,
  `city_from` VARCHAR(255) NULL DEFAULT NULL,
  `city_to` VARCHAR(255) NULL DEFAULT NULL,
  `datefrom` DATE NULL DEFAULT NULL,
  `dateto` DATE NULL DEFAULT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `ispublic` BIT(1) NULL DEFAULT NULL,
  `state` VARCHAR(255) NULL DEFAULT NULL,
  `title` VARCHAR(255) NULL DEFAULT NULL,
  `accommodation_id` BIGINT(20) NULL DEFAULT NULL,
  `budget_id` BIGINT(20) NULL DEFAULT NULL,
  `consumer_id` BIGINT(20) NULL DEFAULT NULL,
  `country_from_id` BIGINT(20) NULL DEFAULT NULL,
  `country_to_id` BIGINT(20) NULL DEFAULT NULL,
  `meal_id` BIGINT(20) NULL DEFAULT NULL,
  `transport_id` BIGINT(20) NULL DEFAULT NULL,
  `enable` BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (`id`),
  INDEX `FKD6495E6A17740BD6` (`accommodation_id` ASC),
  INDEX `FKD6495E6AB5021676` (`transport_id` ASC),
  INDEX `FKD6495E6A87BF6BFE` (`meal_id` ASC),
  INDEX `FKD6495E6A9B84A56` (`country_from_id` ASC),
  INDEX `FKD6495E6A9B84_to` (`country_to_id` ASC),
  INDEX `FKD6495E6A4679691E` (`consumer_id` ASC),
  INDEX `FKD6495E6A109097A9` (`budget_id` ASC),
  CONSTRAINT `FK_budget_id`
    FOREIGN KEY (`budget_id`)
    REFERENCES `ubeos`.`budget_range` (`id`),
  CONSTRAINT `FK_accommodation_id`
    FOREIGN KEY (`accommodation_id`)
    REFERENCES `ubeos`.`accommodation` (`id`),
  CONSTRAINT `FK_consumer_id`
    FOREIGN KEY (`consumer_id`)
    REFERENCES `ubeos`.`consumer` (`id`),
  CONSTRAINT `FK_meal_id`
    FOREIGN KEY (`meal_id`)
    REFERENCES `ubeos`.`meal` (`id`),
  CONSTRAINT `FK_country_from_id`
    FOREIGN KEY (`country_from_id`)
    REFERENCES `ubeos`.`country` (`id`),
  CONSTRAINT `FK_country_to_id`
    FOREIGN KEY (`country_to_id`)
    REFERENCES `ubeos`.`country` (`id`),
  CONSTRAINT `FK_transport_id`
    FOREIGN KEY (`transport_id`)
    REFERENCES `ubeos`.`transport` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `ubeos`.`provider`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`provider` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `companyName` VARCHAR(255) NULL DEFAULT NULL,
  `user_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `user_id` (`user_id` ASC),
  INDEX `FKC52405F1609F1630` (`id` ASC),
  INDEX `FKC52405F147140EFE` (`user_id` ASC),
  CONSTRAINT `FKC52405F147140EFE`
    FOREIGN KEY (`user_id`)
    REFERENCES `ubeos`.`ubeos_user` (`id`),
  CONSTRAINT `FKC52405F1609F1630`
    FOREIGN KEY (`id`)
    REFERENCES `ubeos`.`provider` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ubeos`.`proposal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`proposal` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `accommodationDesc` VARCHAR(255) NULL DEFAULT NULL,
  `adults` INT(11) NULL DEFAULT NULL,
  `children` INT(11) NULL DEFAULT NULL,
  `datefrom` DATE NULL DEFAULT NULL,
  `dateto` DATE NULL DEFAULT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `ispublic` BIT(1) NULL DEFAULT NULL,
  `mealDesc` VARCHAR(255) NULL DEFAULT NULL,
  `price` BIGINT(20) NULL DEFAULT NULL,
  `state` VARCHAR(255) NULL DEFAULT NULL,
  `transportnDesc` VARCHAR(255) NULL DEFAULT NULL,
  `accommodation_id` BIGINT(20) NULL DEFAULT NULL,
  `billing_id` BIGINT(20) NULL DEFAULT NULL,
  `meal_id` BIGINT(20) NULL DEFAULT NULL,
  `provider_id` BIGINT(20) NULL DEFAULT NULL,
  `request_id` BIGINT(20) NULL DEFAULT NULL,
  `transport_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKC4D26AF233164E3C` (`request_id` ASC),
  INDEX `FKC4D26AF2DBAFE33E` (`provider_id` ASC),
  INDEX `FKC4D26AF217740BD6` (`accommodation_id` ASC),
  INDEX `FKC4D26AF2B5021676` (`transport_id` ASC),
  INDEX `FKC4D26AF238742436` (`billing_id` ASC),
  INDEX `FKC4D26AF287BF6BFE` (`meal_id` ASC),
  CONSTRAINT `FKC4D26AF217740BD6`
    FOREIGN KEY (`accommodation_id`)
    REFERENCES `ubeos`.`accommodation` (`id`),
  CONSTRAINT `FKC4D26AF233164E3C`
    FOREIGN KEY (`request_id`)
    REFERENCES `ubeos`.`travel_request` (`id`),
  CONSTRAINT `FKC4D26AF238742436`
    FOREIGN KEY (`billing_id`)
    REFERENCES `ubeos`.`billing` (`id`),
  CONSTRAINT `FKC4D26AF287BF6BFE`
    FOREIGN KEY (`meal_id`)
    REFERENCES `ubeos`.`meal` (`id`),
  CONSTRAINT `FKC4D26AF2B5021676`
    FOREIGN KEY (`transport_id`)
    REFERENCES `ubeos`.`transport` (`id`),
  CONSTRAINT `FKC4D26AF2DBAFE33E`
    FOREIGN KEY (`provider_id`)
    REFERENCES `ubeos`.`provider` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ubeos`.`billing`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`billing` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `address` VARCHAR(255) NULL DEFAULT NULL,
  `city` VARCHAR(255) NULL DEFAULT NULL,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  `nif` BIGINT(20) NULL DEFAULT NULL,
  `zipcode` BIGINT(20) NULL DEFAULT NULL,
  `country_id` BIGINT(20) NULL DEFAULT NULL,
  `proposal_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKF974227BC2DA379E` (`proposal_id` ASC),
  INDEX `FKF974227B9B84A56` (`country_id` ASC),
  CONSTRAINT `FKF974227B9B84A56`
    FOREIGN KEY (`country_id`)
    REFERENCES `ubeos`.`country` (`id`),
  CONSTRAINT `FKF974227BC2DA379E`
    FOREIGN KEY (`proposal_id`)
    REFERENCES `ubeos`.`proposal` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ubeos`.`comment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`comment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `enable` BIT(1) NOT NULL,
  `text` VARCHAR(255) NULL DEFAULT NULL,
  `request_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK38A5EE5F33164E3C` (`request_id` ASC),
  CONSTRAINT `FK38A5EE5F33164E3C`
    FOREIGN KEY (`request_id`)
    REFERENCES `ubeos`.`travel_request` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ubeos`.`consumer_rating`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`consumer_rating` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `value` BIGINT(20) NULL DEFAULT NULL,
  `consumer_id` BIGINT(20) NULL DEFAULT NULL,
  `proposal_id` BIGINT(20) NULL DEFAULT NULL,
  `provider_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKDAB4EA06DBAFE33E` (`provider_id` ASC),
  INDEX `FKDAB4EA06C2DA379E` (`proposal_id` ASC),
  INDEX `FKDAB4EA064679691E` (`consumer_id` ASC),
  CONSTRAINT `FKDAB4EA064679691E`
    FOREIGN KEY (`consumer_id`)
    REFERENCES `ubeos`.`consumer` (`id`),
  CONSTRAINT `FKDAB4EA06C2DA379E`
    FOREIGN KEY (`proposal_id`)
    REFERENCES `ubeos`.`proposal` (`id`),
  CONSTRAINT `FKDAB4EA06DBAFE33E`
    FOREIGN KEY (`provider_id`)
    REFERENCES `ubeos`.`provider` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ubeos`.`experience`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`experience` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `enable` BIT(1) NULL DEFAULT NULL,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name` (`name` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `ubeos`.`experience_tmp`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`experience_tmp` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `enable` BIT(1) NULL DEFAULT NULL,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name` (`name` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `ubeos`.`play_evolutions`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`play_evolutions` (
  `id` INT(11) NOT NULL,
  `hash` VARCHAR(255) NOT NULL,
  `applied_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `apply_script` TEXT NULL DEFAULT NULL,
  `revert_script` TEXT NULL DEFAULT NULL,
  `state` VARCHAR(255) NULL DEFAULT NULL,
  `last_problem` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ubeos`.`proposal_experience`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`proposal_experience` (
  `proposal_id` BIGINT(20) NOT NULL,
  `experiences_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`proposal_id`, `experiences_id`),
  INDEX `FK741A58D7C2DA379E` (`proposal_id` ASC),
  INDEX `FK741A58D7A7CA705F` (`experiences_id` ASC),
  CONSTRAINT `FK741A58D7A7CA705F`
    FOREIGN KEY (`experiences_id`)
    REFERENCES `ubeos`.`experience` (`id`),
  CONSTRAINT `FK741A58D7C2DA379E`
    FOREIGN KEY (`proposal_id`)
    REFERENCES `ubeos`.`proposal` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ubeos`.`provider_rating`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`provider_rating` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NULL DEFAULT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `value` BIGINT(20) NULL DEFAULT NULL,
  `consumer_id` BIGINT(20) NULL DEFAULT NULL,
  `proposal_id` BIGINT(20) NULL DEFAULT NULL,
  `provider_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK84BD47ABDBAFE33E` (`provider_id` ASC),
  INDEX `FK84BD47ABC2DA379E` (`proposal_id` ASC),
  INDEX `FK84BD47AB4679691E` (`consumer_id` ASC),
  CONSTRAINT `FK84BD47AB4679691E`
    FOREIGN KEY (`consumer_id`)
    REFERENCES `ubeos`.`consumer` (`id`),
  CONSTRAINT `FK84BD47ABC2DA379E`
    FOREIGN KEY (`proposal_id`)
    REFERENCES `ubeos`.`proposal` (`id`),
  CONSTRAINT `FK84BD47ABDBAFE33E`
    FOREIGN KEY (`provider_id`)
    REFERENCES `ubeos`.`provider` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ubeos`.`travel_request_experience`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`travel_request_experience` (
  `requests_id` BIGINT(20) NOT NULL,
  `experiences_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`requests_id`, `experiences_id`),
  INDEX `FK38230A5F757AD8E7` (`requests_id` ASC),
  INDEX `FK38230A5FA7CA705F` (`experiences_id` ASC),
  CONSTRAINT `FK38230A5F757AD8E7`
    FOREIGN KEY (`requests_id`)
    REFERENCES `ubeos`.`travel_request` (`id`),
  CONSTRAINT `FK38230A5FA7CA705F`
    FOREIGN KEY (`experiences_id`)
    REFERENCES `ubeos`.`experience` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `ubeos`.`travel_request_tmp_experience_tmp`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ubeos`.`travel_request_tmp_experience_tmp` (
  `requests_id` BIGINT(20) NOT NULL,
  `experiences_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`requests_id`, `experiences_id`),
  INDEX `FK38230A5F757AD8E7` (`requests_id` ASC),
  INDEX `FK38230A5FA7CA705F` (`experiences_id` ASC),
  CONSTRAINT `FK_req_id`
    FOREIGN KEY (`requests_id`)
    REFERENCES `ubeos`.`travel_request_tmp` (`id`),
  CONSTRAINT `FK_exp_id`
    FOREIGN KEY (`experiences_id`)
    REFERENCES `ubeos`.`experience_tmp` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

 
# --- !Downs