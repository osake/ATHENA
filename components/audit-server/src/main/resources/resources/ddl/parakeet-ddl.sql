
DROP TABLE IF EXISTS `MESSAGES` ;

-- -----------------------------------------------------
-- Table `MESSAGES`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `TICKETS` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;



