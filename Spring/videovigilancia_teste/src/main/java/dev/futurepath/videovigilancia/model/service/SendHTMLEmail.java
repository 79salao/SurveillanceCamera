package dev.futurepath.videovigilancia.model.service;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.futurepath.videovigilancia.model.dao.IUserDao;
import dev.futurepath.videovigilancia.model.entity.User;

@Service
public class SendHTMLEmail {
	
	@Autowired
	private IUserDao userDao;
	
	public void sendMail(String from, String to, String subject, String body) {

		User user = userDao.findUserByID((long) 0);
		final String password = user.getPassword();
		final String username = from;

		String host = "smtp.office365.com";

		Properties properties = System.getProperties();
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", true);
		properties.put("mail.smtp.host", host);

		SimpleEmailAuthenticator authenticator = new SimpleEmailAuthenticator(username, password);
		Session session = Session.getInstance(properties, authenticator);

		try {
			MimeMessage message = new MimeMessage(session);

			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);
			message.setContent(body, "text/html");

			Transport.send(message);
			System.out.println("Sent message successfully...");

		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
}
