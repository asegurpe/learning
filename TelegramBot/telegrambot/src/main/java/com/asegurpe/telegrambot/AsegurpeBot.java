package com.asegurpe.telegrambot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.asegurpe.rest.RestClient;

public class AsegurpeBot extends TelegramLongPollingBot {
	
	private static AsegurpeBot INSTANCE = new AsegurpeBot();
	private AsegurpeBot() { }
	public static AsegurpeBot getInstance() {
		return INSTANCE;
	}
	
	private List<Chat> chats = new ArrayList<Chat>();
	
	@Override
	public void onUpdateReceived(Update update) {
		try {
			long chatId = update.getMessage().getChatId();
			
			Chat chat = searchChat(chatId);
			if (chat == null) {
				Operations.sendText(update, Utils.getButton(update, "new.email", Utils.Emoji.WELCOME));
				chats.add(new Chat(chatId));
				return;
			}
			
			User from = update.getMessage().getFrom();
			String username = from.getUserName();
			String text = update.getMessage().getText();
			
			System.out.println("Message from " + username + "(" + chat.getChatId() + "): " + text);
			
			RestClient.getInstance().get();
			RestClient.getInstance().post();
			
			if (StringUtils.isBlank(chat.getEmail())) {
				if (EmailValidator.getInstance().isValid(text)) {
					chat.setEmail(text);
					generateRegistryCode(chat);
					Operations.sendText(update, Utils.getButton(update, "new.code", Utils.Emoji.CODE));
				} else {
					Operations.sendText(update, Utils.getButton(update, "error.email", Utils.Emoji.NOTICE));
				}				
				return;
			}
			
			if (chat.isNotEmailValid()) {
				if (!StringUtils.equals(text, chat.getCode())) {
					Operations.sendText(update, Utils.getButton(update, "error.code", Utils.Emoji.NOTICE));
					return;
				}
				chat.setEmailValid(true);
				Operations.sendText(update, Utils.getButton(update, "new.welcome", Utils.Emoji.LOCKED));
				
				fakeResponse(update, chat);
				return;
			}
			
			if (Operations.evalPhoto(update)) {
				Operations.sendWebcamPhoto(update);
			} else if (Operations.evalGif(update)) {
				Operations.sendWebcamGif(update);
			} else if (Operations.evalSupport(update)) {
				Operations.sendText(update, Utils.getMessage(update, "info.contact"));
				Operations.sendContact(update, "Contact", "Contact", "+34000000000");
			} else if (update.getMessage().hasPhoto()){
				BufferedImage photo = Utils.getPhoto(update);
				Operations.sendPhoto(update, photo);
			} else if (Operations.evalDocument(update)) {
				File document = new File("document.pdf");
				Operations.sendDocument(update, document);
			} else if (Operations.evalMachines(update)) {
				Operations.sendMachines(update, chat.getMachines());
			} else if (Operations.evalMachine(update)) {
				Operations.sendMachineOptions(update);
			} else{
				String fullName = from.getFirstName() + " " + from.getLastName();
				String user = fullName + " (" + username + ")";
				
				String message = "Message received by " + user + ": " + text;
				Operations.sendText(update, message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void fakeResponse(Update update, Chat chat) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(10000);
					Operations.sendText(update, Utils.getButton(update, "new.approved", Utils.Emoji.UNLOCKED));
					Operations.sendMachines(update, chat.getMachines());
					
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									Thread.sleep(15000);
									Operations.sendText(update, Utils.getButton(update, "alarm.head.encoder", Utils.Emoji.ALARM, Utils.Emoji.H1, Utils.Emoji.A));
									new Thread(new Runnable() {
										@Override
										public void run() {
											try {
												Thread.sleep(15000);
												Operations.sendText(update, Utils.getButton(update, "alarm.head.nocorks", Utils.Emoji.WARNING, Utils.Emoji.H1));
												
												new Thread(new Runnable() {
													@Override
													public void run() {
														try {
															Thread.sleep(15000);
															Operations.sendText(update, Utils.getButton(update, "alarm.head.working", Utils.Emoji.OK, Utils.Emoji.H1));
														} catch (InterruptedException | TelegramApiException e) {
															e.printStackTrace();
														}
														
													}
												}).start();
											} catch (InterruptedException | TelegramApiException e) {
												e.printStackTrace();
											}											
										}
									}).start();
								} catch (InterruptedException | TelegramApiException e) {
									e.printStackTrace();
								}
								
							}
						}).start();
					
				} catch (InterruptedException | TelegramApiException e) {
					e.printStackTrace();
				}
				
			}
		}).start();
	}

	public void generateRegistryCode(Chat chat) {
		chat.setCode("12345");
	}
	
	private Chat searchChat(long chatId) {
		for (Chat chat : chats) {
			if (chat.getChatId() == chatId) {
				return chat;
			}
		}
		return null;
	}
	
	@Override
	public String getBotUsername() {
		return BotConstants.USERNAME;
	}

	@Override
	public String getBotToken() {
		return BotConstants.TOKEN;
	}

}

class Chat {
	
	private long chatId;
	private String email;
	private String code;
	private boolean emailValid;
	private List<String> machines = new ArrayList<>(); 
	
	public Chat(long chatId) {
		this.chatId = chatId;
		machines = Arrays.asList("OP1", "OP2", "OP3", "OP4", "OP5", "OP6", "OP7", "OP8", "OP9", "OP10", "OP11", "OP12");
	}	
	
	public long getChatId() {
		return chatId;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmail() {
		return email;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	public String getCode() {
		return code;
	}
	
	public void setEmailValid(boolean emailValid) {
		this.emailValid = emailValid;
	}
	public boolean isEmailValid() {
		return emailValid;
	}
	public boolean isNotEmailValid() {
		return !emailValid;
	}
	
	public List<String> getMachines() {
		return machines;
	}
}
