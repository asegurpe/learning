package com.asegurpe.telegrambot;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Utils {

	public enum Emoji {
		ALARM('\uD83D', '\uDD34'),
		CAMERA('\uD83D', '\uDCF7'),
		STATUS('\uD83D', '\uDCC8'),
		HELP('\uD83D', '\uDC68', '\u200D', '\uD83D', '\uDD27'),
		NOTICE('\u2757'),
		WARNING('\uD83D', '\uDFE1'),
		QUESTION('\u2753'),
		OK('\uD83D', '\uDFE2'),
		CODE('\uD83D', '\uDD22'),
		LOCKED('\uD83D', '\uDD12'),
		UNLOCKED('\uD83D', '\uDD13'),
		WELCOME('\uD83D', '\uDC4B'),
		GIF('\uD83C', '\uDF9E', '\uFE0F'),
		H1('1', '\uFE0F', '\u20E3'),
		A('\uD83C', '\uDD70', '\uFE0F'),
		FACTORY('\uD83C', '\uDFED'),
		DOCUMENT('\uD83D', '\uDCD6');

		Character[] characters;

		Emoji(Character... characters) {
			this.characters = characters;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();

			for (Character c : characters) {
				sb.append(c);
			}

			return sb.toString();
		}
	}

	private Utils() { }
	
	public static String getLanguage(Update update) {
		User from = update.getMessage().getFrom();
		return from.getLanguageCode();
	}

	public static BufferedImage getPhoto(Update update) throws TelegramApiException {
		try {
			List<PhotoSize> photos = update.getMessage().getPhoto();
			GetFile getFile = new GetFile(photos.get(0).getFileId());
			AsegurpeBot bot = AsegurpeBot.getInstance();
			File file = bot.execute(getFile);
			String fileUrl = file.getFileUrl(bot.getBotToken());
			URL url = new URL(fileUrl);
			BufferedImage image = ImageIO.read(url);
			ImageIO.write(image, "jpg", new java.io.File("download.jpg"));
			return image;
		} catch (IOException e) {
			return null;
		}
	}

	public static String getButton(Update update, String key, Emoji... emojis) {
		String languageCode = update.getMessage().getFrom().getLanguageCode();
		return String.format("%s " + LocalisationService.getString(key, languageCode), (Object[])emojis);
	}

	public static String getMenuMachineDescription(Update update) {
		String languageCode = update.getMessage().getFrom().getLanguageCode();
		String baseString = LocalisationService.getString("menu.machine.description", languageCode);
		return String.format(baseString, Emoji.CAMERA.toString(), Emoji.GIF.toString(), Emoji.DOCUMENT.toString(), Emoji.HELP.toString());
	}
	
	public static String getMenuMachinesDescription(Update update) {
		String languageCode = update.getMessage().getFrom().getLanguageCode();
		String baseString = LocalisationService.getString("menu.machines.description", languageCode);
		return String.format(baseString, Emoji.FACTORY.toString());
	}

	public static ReplyKeyboardMarkup getMenuMachine(Update update) {
		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
		replyKeyboardMarkup.setSelective(true);
		replyKeyboardMarkup.setResizeKeyboard(true);

		List<KeyboardRow> keyboard = new ArrayList<>();
		KeyboardRow keyboardFirstRow = new KeyboardRow();
		
		keyboardFirstRow.add(Utils.getButton(update, "menu.photo", Emoji.CAMERA));
		keyboardFirstRow.add(Utils.getButton(update, "menu.gif", Emoji.GIF));
		keyboardFirstRow.add(Utils.getButton(update, "menu.document", Emoji.DOCUMENT));
		keyboard.add(keyboardFirstRow);
		
	    KeyboardRow keyboardSecondRow = new KeyboardRow();
	    keyboardSecondRow.add(Utils.getButton(update, "menu.machines", Emoji.FACTORY));
	    keyboardSecondRow.add(Utils.getButton(update, "menu.support", Emoji.HELP));
	    keyboard.add(keyboardSecondRow);

	    replyKeyboardMarkup.setKeyboard(keyboard);
		return replyKeyboardMarkup;
	}
	
	public static ReplyKeyboardMarkup getMachines(String... serialNumbers) {
		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
		replyKeyboardMarkup.setSelective(true);
		replyKeyboardMarkup.setResizeKeyboard(true);

		List<KeyboardRow> keyboard = new ArrayList<>();
		
		int i = 0;
		while(i < serialNumbers.length) {
			KeyboardRow row = new KeyboardRow();
			for (int j = 0; j < 3; j++) {
				if (i >= serialNumbers.length) {
					break;
				}
				row.add(serialNumbers[i]);
				i++;
			}
			keyboard.add(row);
		}

	    replyKeyboardMarkup.setKeyboard(keyboard);
		return replyKeyboardMarkup;
	}

	public static String getMessage(Update update, String key) {
		return LocalisationService.getString(key, Utils.getLanguage(update));
	}
}
