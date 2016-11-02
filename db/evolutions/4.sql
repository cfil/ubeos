# --- !Ups

-- -----------------------------------------------------
-- Table `ubeos`.`travel_request`
-- -----------------------------------------------------

ALTER TABLE travel_request add column
 `flexible` BOOLEAN NOT NULL DEFAULT FALSE,
 `min_budget` BIGINT(20) NOT NULL,
 `max_budget` BIGINT(20) NOT NULL,
 `description_two` VARCHAR(255) NULL DEFAULT NULL,
 `description_three` VARCHAR(255) NULL DEFAULT NULL,
 `description_four` VARCHAR(255) NULL DEFAULT NULL,
 `description_people` VARCHAR(255) NULL DEFAULT NULL,
 `nights` INT(11) NOT NULL ;

ALTER TABLE travel_request
drop COLUMN budget_id;
ALTER TABLE travel_request
drop COLUMN adults;
ALTER TABLE travel_request
drop COLUMN children;

drop table budget_range;

ALTER TABLE proposal add column
 `city_from` VARCHAR(255) NULL DEFAULT NULL,
 `city_to` VARCHAR(255) NULL DEFAULT NULL,
 `description_two` VARCHAR(255) NULL DEFAULT NULL,
 `description_three` VARCHAR(255) NULL DEFAULT NULL,
 `description_four` VARCHAR(255) NULL DEFAULT NULL,
 `nights` INT(11) NOT NULL;

ALTER TABLE proposal
drop COLUMN adults;
ALTER TABLE proposal
drop COLUMN children; 
ALTER TABLE proposal
drop COLUMN accommodationDesc; 
ALTER TABLE proposal
drop COLUMN mealDesc; 
ALTER TABLE proposal
drop COLUMN transportDesc; 

insert into experience(enable, name) values(true, 'experience.radical');
insert into experience(enable, name) values(true, 'experience.nature');
insert into experience(enable, name) values(true, 'experience.adventure');
insert into experience(enable, name) values(true, 'experience.relax');
insert into experience(enable, name) values(true, 'experience.snow');
insert into experience(enable, name) values(true, 'experience.sport');
insert into experience(enable, name) values(true, 'experience.business');

insert into transport(enable, name) values(true, 'transport.any');
insert into meal(enable, name) values(true, 'meal.any');
insert into accommodation(enable, name) values(true, 'accommodation.any');

drop table travel_request_tmp;
drop table travel_request_tmp_experience;

# --- !Downs

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

ALTER TABLE travel_request drop column `flexible`;
ALTER TABLE travel_request drop column `min_budget`;
ALTER TABLE travel_request drop column `max_budget`;
ALTER TABLE travel_request drop column `description_two`;
ALTER TABLE travel_request drop column `description_three`;
ALTER TABLE travel_request drop column `description_four`;
ALTER TABLE travel_request drop column `description_people`;
ALTER TABLE travel_request drop column `nights`;

ALTER TABLE travel_request add column
 `adults` INT(11) NULL DEFAULT NULL,
 `children` INT(11) NULL DEFAULT NULL,
 `budget_id` BIGINT(20) NULL DEFAULT NULL,
 INDEX `FK_budget` (`budget_id` ASC),
 CONSTRAINT `FK_budget`
    FOREIGN KEY (`budget_id`)
    REFERENCES `ubeos`.`budget_range` (`id`);

ALTER TABLE proposal drop column `city_from`;
ALTER TABLE proposal drop column `city_to`;
ALTER TABLE proposal drop column `description_two`;
ALTER TABLE proposal drop column `description_three`;
ALTER TABLE proposal drop column `description_four`;
ALTER TABLE proposal drop column `nights`;

ALTER TABLE proposal add column
  `adults` INT(11) NULL DEFAULT NULL,
  `children` INT(11) NULL DEFAULT NULL,
  `mealDesc` VARCHAR(255) NULL DEFAULT NULL,
  `transportnDesc` VARCHAR(255) NULL DEFAULT NULL,
  `accommodationDesc` VARCHAR(255) NULL DEFAULT NULL;

