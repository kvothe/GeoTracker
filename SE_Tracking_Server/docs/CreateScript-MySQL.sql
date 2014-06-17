CREATE TABLE `geolocation` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`user_fk` BIGINT NOT NULL,
	`timestamp` BIGINT NOT NULL,
	`longitude` DOUBLE NOT NULL,
	`latitude` DOUBLE NOT NULL,
	`accuracy` DOUBLE NULL,
	`altitude` DOUBLE NULL,
	`altitude-accuracy` DOUBLE NULL,
	`heading` DOUBLE NULL,
	`speed` DOUBLE NULL,
	PRIMARY KEY (`id`)
)

CREATE TABLE `trackingsession` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`observed` BIGINT NOT NULL DEFAULT '0',
	`observer` BIGINT NOT NULL DEFAULT '0',
	`starttime` BIGINT NOT NULL DEFAULT '0',
	`endtime` BIGINT NULL DEFAULT '0',
	`canceled_by` BIGINT NULL DEFAULT '0',
	PRIMARY KEY (`id`)
)

CREATE TABLE `user` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`username` VARCHAR(512) NOT NULL DEFAULT '0',
	`encryptedPassword` VARBINARY(20) NOT NULL DEFAULT '0',
	`salt` VARBINARY(8) NOT NULL DEFAULT '0',
	`observable` BIT NOT NULL DEFAULT b'0',
	PRIMARY KEY (`id`)
)

ALTER TABLE `geolocation`
	ADD CONSTRAINT `FK_geolocation_user` FOREIGN KEY (`user_fk`) REFERENCES `user` (`id`);
ALTER TABLE `trackingsession`
	ADD CONSTRAINT `FK_trackingsession_user_canceled` FOREIGN KEY (`canceled_by`) REFERENCES `user` (`id`),
	ADD CONSTRAINT `FK_trackingsession_user_observed` FOREIGN KEY (`observed`) REFERENCES `user` (`id`),
	ADD CONSTRAINT `FK_trackingsession_user_observer` FOREIGN KEY (`observer`) REFERENCES `user` (`id`);
