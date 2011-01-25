DROP TABLE IF EXISTS `MESSAGES`;

-- -----------------------------------------------------
-- Table `MESSAGES`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `MESSAGES` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `datetime` BIGINT NOT NULL ,
  `user` VARCHAR(45) NOT NULL ,
  `action` VARCHAR(10) NOT NULL ,
  `resource` VARCHAR(200) NOT NULL ,
  `message` VARCHAR(2000) NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;
