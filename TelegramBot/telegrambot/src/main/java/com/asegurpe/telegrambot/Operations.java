package com.asegurpe.telegrambot;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation.SendAnimationBuilder;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendContact.SendContactBuilder;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument.SendDocumentBuilder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage.SendMessageBuilder;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto.SendPhotoBuilder;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.github.sarxos.webcam.Webcam;

public class Operations {

	public static boolean evalSupport(Update update) {
		return evalMenu(update, "menu.support");
	}
	public static boolean evalPhoto(Update update) {
		return evalMenu(update, "menu.photo");
	}
	public static boolean evalGif(Update update) {
		return evalMenu(update, "menu.gif");
	}
	public static boolean evalDocument(Update update) {
		return evalMenu(update, "menu.document");
	}
	public static boolean evalMachines(Update update) {
		return evalMenu(update, "menu.machines");
	}
	public static boolean evalMenu(Update update, String key) {
		String received = update.getMessage().getText();
		String language = Utils.getLanguage(update);
		String ins = LocalisationService.getString(key, language);
		ins = ins.replaceAll("%s ", "");
		return StringUtils.endsWithIgnoreCase(received, ins);
	}
	
	public static boolean evalMachine(Update update) {
		String received = update.getMessage().getText();
		return StringUtils.startsWithAny(received, "SLS", "DLS", "QLS");
	}
	
	public static  void sendContact(Update update, String firstName, String lastName, String phoneNumber) throws TelegramApiException {
		SendContactBuilder contactBuilder = SendContact.builder().chatId(update.getMessage().getChatId()+"");
		AsegurpeBot.getInstance().execute(contactBuilder.firstName(firstName).lastName(lastName).phoneNumber(phoneNumber).build());
	}
	
	public static void sendWebcamPhoto(Update update) throws TelegramApiException, IOException {
		
		for (Webcam webcam : Webcam.getWebcams()) {
			if (webcam.getName().contains("capture")) {
				continue;
			}
			try {
				System.out.println(webcam.getName());
				webcam.open();
				BufferedImage image = webcam.getImage();
				
				sendPhoto(update, image);
				webcam.close();
			} catch (Exception e) { }
		}
	}
	
	public static void sendWebcamGif(Update update) throws TelegramApiException, IOException {
		
		for (Webcam webcam : Webcam.getWebcams()) {
			if (webcam.getName().contains("capture") || webcam.getName().contains("Basler")) {
				continue;
			}
			try {
				webcam.open();
				BufferedImage image = webcam.getImage();
				File gif = File.createTempFile("gif", ".gif");
				ImageOutputStream output = new FileImageOutputStream(gif);
				int delay = 100;
				GifSequenceWriter writer = new GifSequenceWriter(output, 1, delay, false);
				for (int i = 0; i < 50; i++) {
					Thread.sleep(delay);
					image = webcam.getImage();
					writer.writeToSequence(image);
				}
				writer.close();
				output.close();
				webcam.close();
				sendGif(update, gif.getAbsolutePath());
				FileUtils.deleteQuietly(gif);
			} catch (Exception e) { }
		}
	}

	public static void sendPhoto(Update update, BufferedImage image) throws IOException, TelegramApiException {
		SendPhotoBuilder photoBuilder = SendPhoto.builder().chatId(update.getMessage().getChatId()+"");
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(image, "jpeg", os); 
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		
		AsegurpeBot.getInstance().execute(photoBuilder.photo(new InputFile(is, "Status")).build());
	}
	
	public static void sendDocument(Update update, File document) throws IOException, TelegramApiException {
		SendDocumentBuilder documentBuilder = SendDocument.builder().chatId(update.getMessage().getChatId()+"");
		AsegurpeBot.getInstance().execute(documentBuilder.document(new InputFile(document)).build());
	}

	public static void sendImage(Update update, String file) throws TelegramApiException {
		SendPhotoBuilder photoBuilder = SendPhoto.builder().chatId(update.getMessage().getChatId()+"");
		AsegurpeBot.getInstance().execute(photoBuilder.photo(new InputFile(new File(file))).build());
	}
	
	public static void sendGif(Update update, String file) throws TelegramApiException {
		SendAnimationBuilder gifBuilder = SendAnimation.builder().chatId(update.getMessage().getChatId()+"");
		AsegurpeBot.getInstance().execute(gifBuilder.animation(new InputFile(new File(file))).build());
	}

	public static void sendText(Update update, String text) throws TelegramApiException {
		SendMessageBuilder messageBuilder = SendMessage.builder().chatId(update.getMessage().getChatId()+"");
		AsegurpeBot.getInstance().execute(messageBuilder.text(text).build());
	}
	
	public static void sendMachineOptions(Update update) throws TelegramApiException {
		SendMessage message = new SendMessage();
		message.enableMarkdown(true);
		message.setReplyMarkup(Utils.getMenuMachine(update));
//		message.setReplyToMessageId(update.getMessage().getMessageId());
		message.setChatId(update.getMessage().getChatId()+"");
		message.setText(Utils.getMenuMachineDescription(update));
		AsegurpeBot.getInstance().execute(message);
	}
	
	public static void sendMachines(Update update, List<String> machineList) throws TelegramApiException {
		SendMessage message = new SendMessage();
		message.enableMarkdown(true);
		String[] machinesArray = (String[])machineList.toArray();
		message.setReplyMarkup(Utils.getMachines(machinesArray));
//		message.setReplyToMessageId(update.getMessage().getMessageId());
		message.setChatId(update.getMessage().getChatId()+"");
		message.setText(Utils.getMenuMachinesDescription(update));
		AsegurpeBot.getInstance().execute(message);
	}
	
}
