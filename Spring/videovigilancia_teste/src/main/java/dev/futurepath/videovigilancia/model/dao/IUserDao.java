package dev.futurepath.videovigilancia.model.dao;

import java.util.List;

import dev.futurepath.videovigilancia.model.entity.User;

public interface IUserDao {
	
	public Long findId(User user);
	
	public User findUserByEmail(String email);
	
	public User findUserByID(Long id);
	
	public User findUserByUsername(String username);
	
	public void update(User user);
	
	public List<User> findAllUsers();	

}
