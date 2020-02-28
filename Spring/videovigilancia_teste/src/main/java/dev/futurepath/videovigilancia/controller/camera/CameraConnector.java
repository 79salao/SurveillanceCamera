package dev.futurepath.videovigilancia.controller.camera;

import java.io.*;
import java.net.*;

public class CameraConnector {
	static final int PORT = 6666;

	//192.168.2.82 IP Angela
	public static void main(String[] args) throws IOException {
		ServerSocket s = new ServerSocket(PORT);		
		try {
			while (true) {
				Socket socket = s.accept();
				try {
					new CameraConnectorController(socket);
				} catch (Exception e) {
					socket.close();
				}
			}
		} finally {
			s.close();
		}
	}
}
