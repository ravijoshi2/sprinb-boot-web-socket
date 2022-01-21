package org.rj.websocketdemo;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyTextWebSocketHandler extends TextWebSocketHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MyTextWebSocketHandler.class);

	private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

	private ExecutorService es = Executors.newFixedThreadPool(1);

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.add(session);
		Random random = new Random(5);
		while (!sessions.isEmpty()) {
			sessions.forEach(webSocketSession -> {
				try {
					String payload = String.valueOf(random.nextInt());
					System.out.println("Payload : " + payload);
					TextMessage tm = new TextMessage(payload);
					webSocketSession.sendMessage(tm);
				} catch (IOException e) {
					LOGGER.error("Error occurred.", e);
				}
			});
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		super.afterConnectionEstablished(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
		super.afterConnectionClosed(session, status);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		super.handleTextMessage(session, message);
		sessions.forEach(webSocketSession -> {
			try {
				String payload = message.getPayload() + " from server";
				System.out.println("Payload : " + payload);
				TextMessage tm = new TextMessage(payload);
				webSocketSession.sendMessage(tm);
			} catch (IOException e) {
				LOGGER.error("Error occurred.", e);
			}
		});
	}
}