Create the database
DROP DATABASE IF EXISTS `19120469_messenger`;
CREATE DATABASE `19120469_messenger`;

#Create user and grante permission on the created database
DROP USER IF EXISTS '19120469'@'localhost';
CREATE USER '19120469'@'localhost' IDENTIFIED BY '19120469';
GRANT ALL PRIVILEGES ON `19120469_messenger`.* TO '19120469'@'localhost';

USE `19120469_messenger`;

DROP TABLE IF EXISTS `client`;
CREATE TABLE `client` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(70) NOT NULL,
  `encrypted_password` varchar(70) NOT NULL,
  `is_online` boolean DEFAULT TRUE,
  
  UNIQUE (`user_name`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1;

DROP TABLE IF EXISTS `room`;
CREATE TABLE `room` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` nvarchar(20) DEFAULT NULL,
  
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1;

DROP TABLE IF EXISTS `client_room`;
CREATE TABLE `client_room` (
  `room_id` int(11) NOT NULL,
  `client_id` int(11) NOT NULL,

  CONSTRAINT `fk_room_client` FOREIGN KEY(`client_id`) REFERENCES client(`id`),
  CONSTRAINT `fk_client_room` FOREIGN KEY(`room_id`) REFERENCES room(`id`),
  PRIMARY KEY (room_id, client_id)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` nvarchar(1024) DEFAULT NULL,
  `data_type` int(2) NOT NULL,
  `date_time` DATETIME NOT NULL,
   `client_id` int(11) NOT NULL,
   `room_id` int(11) NOT NULL,

  CONSTRAINT `fk_message_client` FOREIGN KEY(`client_id`) REFERENCES client(`id`),
  CONSTRAINT `fk_message_room` FOREIGN KEY(`room_id`) REFERENCES room(`id`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1;