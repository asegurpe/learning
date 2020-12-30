package com.asegurpe.websocket;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

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
	public void connect(Session session) {
		this.session = session;
		connections.add(this);
		String message = String.format("* El %s %s", nickname, "se ha unido al chat.");
		broadcastMessage(message);
	}
	

	@OnClose
	public void disconnect() {
		connections.remove(this);
		String message = String.format("* %s %s", nickname, "se ha ido del chat.");
		broadcastMessage(message);
	}
	
	@OnMessage
	public void listen(String message) {
		String messageWithId = String.format("%s: %s", nickname, message);
		broadcastMessage(messageWithId);
	}
	
	@OnError
	public void error(Throwable t){
		System.out.println("Chat error: " + t.toString());
	}
	
	private void broadcastMessage(String message) {
		for (ChatClient connection : connections) {
			try {
				synchronized (connection) {
					if (connection.session.isOpen()) {
						connection.session.getBasicRemote().sendText(message);
					}
				}
			} catch (Exception e) {
				System.out.println("Error");
			}
		}
	}
}