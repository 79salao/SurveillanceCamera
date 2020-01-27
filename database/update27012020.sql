ALTER TABLE `users`
	ADD UNIQUE INDEX `username` (`username`);

ALTER TABLE `users`
	ADD UNIQUE INDEX `email` (`email`);

