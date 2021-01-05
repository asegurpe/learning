package com.asegurpe.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage.SendMessageBuilder;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.asegurpe.telegrambot.AsegurpeBot;
import com.asegurpe.telegrambot.BotConstants;
import com.asegurpe.telegrambot.TelegramBot;

public class RestClient {

	private static RestClient instance;

	private RestClient() {
	}

	public static RestClient getInstance() {
		if (instance == null) {
			instance = new RestClient();
		}
		return instance;
	}

	public void get() {
		try {
			URL url = new URL("http://localhost:8080/websocket/api/service1");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			conn.disconnect();

			AsegurpeBot bot = TelegramBot.getInstance().getBot();
			SendMessageBuilder chat = SendMessage.builder().chatId(BotConstants.CHAT_ID);
			SendMessage message = chat.text("Get message sent").build();
			bot.execute(message);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	public void post() {
		try {

			URL url = new URL("http://localhost:8080/websocket/api/service2");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

//			String input = "{\"qty\":100,\"name\":\"iPad 4\"}";

//			OutputStream os = conn.getOutputStream();
//			os.write(input.getBytes());
//			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			conn.disconnect();
			
			AsegurpeBot bot = TelegramBot.getInstance().getBot();
			SendMessageBuilder chat = SendMessage.builder().chatId(BotConstants.CHAT_ID);
			SendMessage message = chat.text("Post message sent").build();
			bot.execute(message);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
