
DROP TABLE IF EXISTS `TICKET_PROPS` ;
DROP TABLE IF EXISTS `PROP_VALUES` ;
DROP TABLE IF EXISTS `PROP_FIELDS` ;
DROP TABLE IF EXISTS `TICKETS` ;

-- -----------------------------------------------------
-- Table `TICKETS`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `TICKETS` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;



-- -----------------------------------------------------
-- Table `PROP_FIELDS`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `PROP_FIELDS` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `valueType` VARCHAR(45) NULL DEFAULT NULL ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `strict` TINYINT(1) NULL DEFAULT false ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `TICKET_PROPS`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `TICKET_PROPS` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `PROP_FIELD_ID` BIGINT NOT NULL ,
  `TICKET_ID` BIGINT NULL DEFAULT NULL ,
  `propType` VARCHAR(15) NULL DEFAULT NULL ,
  `valueString` VARCHAR(45) NULL DEFAULT NULL ,
  `valueInteger` INT NULL DEFAULT NULL ,
  `valueDateTime` DATETIME NULL DEFAULT NULL ,
  `valueBoolean` TINYINT(1) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `fk_TICKET_PROPS_TICKET_ID_to_TICKETS_id`
    FOREIGN KEY (`TICKET_ID` )
    REFERENCES `TICKETS` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_TICKET_PROPS_PROP_FIELD_ID_to_PROP_FIELDS__id`
    FOREIGN KEY (`PROP_FIELD_ID` )
    REFERENCES `PROP_FIELDS` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `PROP_VALUES`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `PROP_VALUES` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `PROP_FIELD_ID` BIGINT NULL DEFAULT NULL ,
  `propValue` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `fk_PropertyValues_PROP_FIELD_ID_to_PropertyFields_id`
    FOREIGN KEY (`PROP_FIELD_ID` )
    REFERENCES `PROP_FIELDS` (`id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_PROP_FIELDS_valueType_id_to_ValueTypes_id` ON `PROP_FIELDS` (`valueType` ASC) ;

CREATE INDEX `fk_PropertyValues_PROP_FIELD_ID_to_PropertyFields_id` ON `PROP_VALUES` (`PROP_FIELD_ID` ASC) ;

CREATE INDEX `fk_TICKET_PROPS_TICKET_ID_to_TICKETS_id` ON `TICKET_PROPS` (`TICKET_ID` ASC) ;

CREATE INDEX `fk_TICKET_PROPS_PROP_FIELD_ID_to_PROP_FIELDS__id` ON `TICKET_PROPS` (`PROP_FIELD_ID` ASC) ;