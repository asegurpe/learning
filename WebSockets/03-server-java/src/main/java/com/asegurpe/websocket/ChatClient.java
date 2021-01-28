package com.asegurpe.websocket;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.imgscalr.Scalr;

@ServerEndpoint(value = "/chat")
public class ChatClient {
	
	private static final String PREFIX_USER = "Guest # ";
	
	private static final AtomicInteger idConnection = new AtomicInteger(0);
	
	private static final Set<ChatClient> connections = new CopyOnWriteArraySet<>();
	
	private final String nickname;
	private Session session;
	
	public ChatClient() {
		this.nickname = PREFIX_USER + idConnection.getAndIncrement();
	}
	
	@OnOpen
	public void open(Session session) {
		this.session = session;
		connections.add(this);
		String message = String.format("* El %s %s", nickname, "se ha unido al chat.");
		broadcast(message);
	}
	

	@OnClose
	public void close() {
		connections.remove(this);
		String message = String.format("* %s %s", nickname, "se ha ido del chat.");
		broadcast(message);
	}
	
	@OnMessage
	public void message(String message) {
		String messageWithId = String.format("%s: %s", nickname, message);
		broadcast(messageWithId);
	}
	
	@OnError
	public void error(Throwable t){
		System.out.println("Chat error: " + t.toString());
	}
	
	public static void broadcast(String message) {
		for (ChatClient connection : connections) {
			try {
				synchronized (connection) {
					if (connection.session.isOpen()) {
						if (message.contains("foto")) {
							File fi = new File("D:\\Temp\\test1_bevel.bmp");
							
//							100%
//							byte[] fileContent = Files.readAllBytes(fi.toPath());
							
//							RESIZED
							BufferedImage in = ImageIO.read(fi);
//							in = resizeImage(in, 200);
							in = Scalr.resize(in, 200);
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							ImageIO.write(in, "jpg", baos);
							byte[] fileContent = baos.toByteArray();
							
							ByteBuffer buf = ByteBuffer.wrap(fileContent);
							connection.session.getBasicRemote().sendBinary(buf);
						} else {
							
							byte[] text = new byte[message.length() + 2];
							char[] charArray = message.toCharArray();
							
							text[0] = 1;
							text[1] = 1;
							for (int i = 0; i < charArray.length; i++) {
								text[i+2] = (byte) charArray[i];
							}
							ByteBuffer buf = ByteBuffer.wrap(text);
							connection.session.getBasicRemote().sendBinary(buf);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
//	Resize using java classes
//	private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth) throws IOException {
//		int targetHeight = (targetWidth * 100 / originalImage.getWidth()) * (originalImage.getHeight() / 100);
//	    BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
//	    Graphics2D graphics2D = resizedImage.createGraphics();
//	    graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
//	    graphics2D.dispose();
//	    return resizedImage;
//	}
}