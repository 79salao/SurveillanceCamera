DROP TABLE `users`;
CREATE TABLE `users` (
	`id` INT NOT NULL,
	`username` VARCHAR(16) NOT NULL DEFAULT '',
	`password` VARCHAR(16) NOT NULL DEFAULT '',
	`email` VARCHAR(50) NOT NULL DEFAULT '',
	`name` VARCHAR(50) NOT NULL DEFAULT '',
	PRIMARY KEY (`id`)
)COLLATE='utf8mb4_general_ci';
