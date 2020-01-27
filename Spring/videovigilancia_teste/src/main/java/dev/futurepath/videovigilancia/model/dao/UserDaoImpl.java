package dev.futurepath.videovigilancia.model.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import dev.futurepath.videovigilancia.model.entity.User;

@Repository
public class UserDaoImpl implements IUserDao {

	@PersistenceContext
	private EntityManager em;
	
	@Transactional(readOnly=true)
	@Override
	public Long findId(User user) {
		String userName = user.getUsername();
		String userPassword = user.getPassword();
		String query = "SELECT u.id FROM User u WHERE u.username = '" + 
							userName + "' AND u.password = '" + userPassword	+ "'";
		try {
			return (Long) em.createQuery(query).getSingleResult();
		}catch(NoResultException | EmptyResultDataAccessException e){
			return null;
		}		
	}
	

}
