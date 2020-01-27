package dev.futurepath.videovigilancia.model.dao;

import dev.futurepath.videovigilancia.model.entity.User;

public interface IUserDao {
	
	public Long findId(User user);
	
	public Long findIdByUsername(String username);

}
