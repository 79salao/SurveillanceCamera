package dev.futurepath.videovigilancia.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import dev.futurepath.videovigilancia.model.MySQLConnection;
import dev.futurepath.videovigilancia.model.entity.User;
/*
 * Method findId = search in database if the user exists after the user introduced their username and their password and return the id
 * Method findIdByUsername = search in database if the email exists and return the user.
 * Method findUserByID = search in database if the id exists and return the user.
 * Method update = update the all the user data in the database.
 */
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

	@Override
	@Transactional(readOnly=true)
	public User findUserByEmail(String email) {
		String query = "SELECT u.id FROM User u WHERE u.email = '" + email + "'";
		try {			
			Long id = (Long) em.createQuery(query).getSingleResult();
			return em.find(User.class,id);
		}catch(NoResultException | EmptyResultDataAccessException e){
			return null;
		}		
	}

	@Override
	@Transactional(readOnly=true)
	public User findUserByID(Long id) {	
		try {
			return em.find(User.class,id);	
		}catch(NoResultException | EmptyResultDataAccessException e){
			return null;
		}	
	}

	@Override
	public User findUserByUsername(String username) {
		String query = "SELECT u.id FROM User u WHERE u.username = '" + username + "'";
		try {			
			Long id = (Long) em.createQuery(query).getSingleResult();
			return em.find(User.class,id);
		}catch(NoResultException | EmptyResultDataAccessException e){
			return null;
		}	
	}

	@Override
	@Transactional
	public void update(User user) {
		em.merge(user);		
	}

	/*
	 * Returns all users in the DB.
	 */
	@Transactional(readOnly=true)
	@Override
	public List<User> findAllUsers() {
		Connection connection = null;
        PreparedStatement query = null;
        List<User> result = new ArrayList<>();

        try{
            connection = MySQLConnection.getConnection();
            query = connection.prepareStatement("SELECT * FROM users");
            ResultSet rs = query.executeQuery();
            while(rs.next()){
                User user = new User();
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("name"));
                result.add(user);
            }
            rs.close();
        }catch(SQLException ex){
            System.out.println("");
        }finally{
            try{
                query.close();
                connection.close();
            }catch(SQLException ex){
                System.out.println("Error");
            }
        }
        return result;
	}
	

}
