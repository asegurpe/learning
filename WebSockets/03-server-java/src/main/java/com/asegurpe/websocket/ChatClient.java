package com.asegurpe.websocket;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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

import com.onlaser.designer.core.service.impl.CommandCallServiceImpl;
import com.onlaser.designer.core.utils.ReflectUtils;
import com.onlaser.designer.visio.socket.CameraClient;
import com.onlaser.designer.visio.socket.CameraClient.Command;
import com.onlaser.designer.visio.util.VisioUtils;

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
							int port = 54000;
							int width = 1280;
							int height = 960;
							CommandCallServiceImpl.getInstance().call("taskkill.exe", "/f", "/im", "BaslerService.exe");
							CommandCallServiceImpl.getInstance().execute("BaslerService.exe", port);
							CameraClient cc = new CameraClient("localhost", port);
							cc.open();
							cc.setSerial("22088739");
							cc.connect();
							cc.setParameter(Command.WIDTH, width);
							cc.setParameter(Command.HEIGHT, height);
							
							for (int j = 0; j < 1000; j++) {
								BufferedImage bi = null;
								byte[] fileContent = null;
								if (message.contains("foto1")) {
									File fi = new File("D:\\Temp\\test1_bevel.bmp");
									
//									100%
//									byte[] fileContent = Files.readAllBytes(fi.toPath());
									
//									RESIZED
									bi = ImageIO.read(fi);
//									in = resizeImage(in, 200);
								} else if (message.contains("foto2")) {
									byte[] capture = cc.capture((width * height) + 54);
									VisioUtils.saveImage("D:\\temp\\camera1.bmp", capture);
//									byte[] capture = cc.capture(1229878);
									bi = bytesToImage(capture);
								}
								bi = Scalr.resize(bi, 200);
								
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								ImageIO.write(bi, "bmp", baos);
								fileContent = baos.toByteArray();
								
								ByteBuffer buf = ByteBuffer.wrap(fileContent);
								connection.session.getBasicRemote().sendBinary(buf);
							}
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
	
	public static BufferedImage bytesToImage(byte[] imageData) {
		ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
		try {
			return ImageIO.read(bais);
		} catch (IOException e) {
			throw ReflectUtils.rethrow(e);
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