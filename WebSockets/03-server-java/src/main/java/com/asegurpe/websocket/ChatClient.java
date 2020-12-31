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
	
	private void broadcast(String message) {
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