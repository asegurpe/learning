package com.asegurpe.telegrambot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage.SendMessageBuilder;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramBot {

	private static TelegramBot instance;
	private AsegurpeBot bot;

	private TelegramBot() {
		try {
			bot = AsegurpeBot.getInstance();

			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(bot);

			SendMessageBuilder chat = SendMessage.builder().chatId(BotConstants.CHAT_ID);
			SendMessage message = chat.text("Hi! I'm " + bot.getBotUsername() + " and I'm awake!").build();
			bot.execute(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static TelegramBot getInstance() {
		if (instance == null) {
			instance = new TelegramBot();
		}
		return instance;
	}
	
	public AsegurpeBot getBot() {
		return bot;
	}
}