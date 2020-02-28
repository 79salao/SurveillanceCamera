package dev.futurepath.videovigilancia.controller.camera;

import java.io.IOException;
import java.io.InputStream;

import java.net.Socket;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import dev.futurepath.videovigilancia.model.dao.IRecordDao;
import dev.futurepath.videovigilancia.model.dao.IUserDao;
import dev.futurepath.videovigilancia.model.dao.RecordDaoImpl;
import dev.futurepath.videovigilancia.model.dao.UserDaoImpl;
import dev.futurepath.videovigilancia.model.entity.User;
import dev.futurepath.videovigilancia.model.service.SendHTMLEmail;

@Controller
public class CameraConnectorController extends Thread {
	private Socket socket;
	private InputStream is;
	private static Pattern pattern = Pattern.compile("^RECORD,");
	private static Matcher matcher;
	private IRecordDao recordDao = new RecordDaoImpl();
	private IUserDao userDao = new UserDaoImpl();
	
	@Autowired
	private SendHTMLEmail sendMailService;

	
	public CameraConnectorController() {
	}

	public CameraConnectorController(Socket s) throws IOException {
		//socket = s;
		is = s.getInputStream();
		start();
	}

	/*
	 * In this method we use two ways to evaluate a String. The first one is equals,
	 * bcs we know the whole message that'll be sent. The second is a
	 * pattern+matcher, bcs we only know the beginning of the message sent.
	 */
	public void run() {
		String str = "";

		try {
			while (true) {
				str = readMessageFromClient(is, 5);
				matcher = pattern.matcher(str);

				if (str.equals("EMAIL")) {
					sendEmail();
					System.out.println("Email enviado");
				} else if (matcher.lookingAt()) {
					if (recordDao.saveRecording(str)) {
						System.out.println("Se guardó en la bd");
					} else {
						System.out.println("Hubo un error");
					}
				} else {
					System.out.println("No entiendo");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	/*
	 * Method for the socket to listen
	 */
	private String readMessageFromClient(InputStream is, int lockSeconds) throws IOException {
		lockSeconds *= 1000;
		long lockThreadCheckpoint = System.currentTimeMillis();
		int availableBytes = is.available();
		while (availableBytes < 1 && (System.currentTimeMillis() < lockThreadCheckpoint + lockSeconds)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			availableBytes = is.available();
		}
		byte[] buffer = new byte[availableBytes];
		is.read(buffer, 0, availableBytes);
		return new String(buffer);
	}

	/*
	 * Method to send a email to all users when movement is detected.
	 */
	private void sendEmail() {
		List<User> users = userDao.findAllUsers();

		for (User user : users) {
			System.out.println(user.getEmail());
			String subject = "Alert TB/O";
			String link = "<a style='text-decoration: none; border-radius: 5px; padding: 15px 23px; color: white; background-color: #3498db' "
					+ "href='http://localhost:8080/main' target='_blank'>Click here to see!</a>";

			String body = "    <table style='max-width: 600px; padding: 10px; margin:0 auto; border-collapse: collapse;'>"
					+ "    <tr>" +

					"        <td style='background:white; border:15px solid #0f407a'>"
					+ "            <div style='text-align:center'>"
					+ "                <img  src='https://pbs.twimg.com/profile_images/921275833221828609/emWox-Nh_400x400.jpg' width='20%'>"
					+ "            </div>"
					+ "            <hr style='border-bottom: 3px solid #0f407a; margin-bottom: 30px;margin-top: 30px;font-weight: bold;color: #0f407a;'>"
					+ "            <div style='color: #34495e; margin: 4% 10% 2%; text-align: justify;font-family: sans-serif'>"
					+ "                <h2 style='text-align: center;color: #0f407a; margin: 0 0 7px'>Hello "
					+ user.getName() + " !</h2>" + "                <p style='margin: 2px; font-size: 15px'>"
					+ "                    We detected movement! Click in the link below to check what is happening. "
					+ "                </p><br/>" + "                <div style='width: 100%; text-align: center'>"
					+ link + "<br/><br/>" + "                </div>"
					+ "                <p style='color: #b3b3b3; font-size: 12px; text-align: center;margin: 30px 0 0'>©Copyright 2019, TB/O. All rights reserved.</p>"
					+ "            </div>" + "        </td>" + "    </tr>" + "</table>";
			try {
				sendMailService.sendMail("tbonotifica@hotmail.com", user.getEmail(), subject, body);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

}
