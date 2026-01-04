package ru.dx9119;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Utils {
    TelegramClient telegramClient;

    public Utils(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    public String getFilePath(PhotoSize photo) {
        Objects.requireNonNull(photo);

        GetFile getFile = GetFile.builder()
                        .fileId(photo.getFileId())
                        .build();

        try {
            File file = telegramClient.execute(getFile);
            return file.getFilePath();
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return null;
        }
    }


    public SendPhoto photoEcho(Update update) {
        PhotoSize photo = getPhoto(update);
        if (photo != null) {
            String fileId = photo.getFileId();

            // Пример: переслать то же фото обратно (эхо)
            SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(update.getMessage().getChatId().toString())
                    .photo(new InputFile(fileId))
                    .caption("Вы прислали фото размером " + photo.getWidth() + "x" + photo.getHeight())
                    .build();

            return sendPhoto;
        }
        return null;
    }

    public PhotoSize getPhoto(Update update) {

        //Telegram автоматически генерирует несколько размеров (thumbnails) одного и того же изображения: маленькое, среднее, большое, etc.
        List<PhotoSize> photos = update.getMessage().getPhoto();

        if (!photos.isEmpty()) {

            PhotoSize photoSize = photos.stream()
                    .max(Comparator.comparing(PhotoSize::getFileSize))
                    .orElse(null);

            return photoSize;
        }

        return null;
    }

    public SendPhoto sendImageFromLocal (String filePath, String fileName, Long chatId){
        try {

            InputStream inputStream = getClass().getResourceAsStream("/img/album/img.png");

            SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(chatId)
                    .photo(new InputFile(inputStream,fileName))
                    .caption("test")
                    .build();

            return sendPhoto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SendMessage textEcho(Update update) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(update.getMessage().getText())
                .build();
        return sendMessage;
    }

    public SendMessage message(Update update, String msg){
        SendMessage sendMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(msg)
                .build();
        return sendMessage;
    }

    public String getUserInfo(Update update) {
        User user = update.getMessage().getFrom();
        Long userId = user.getId();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String username = user.getUserName();
        Boolean isBot = user.getIsBot();
        String languageCode = user.getLanguageCode();

        return userId+"|"+username+"|"+
                firstName+"|"+lastName+"|"+
                isBot+"|"+languageCode;
    }


    public SendMessage keyboard (String chatId){
        SendMessage sendMessage = new SendMessage(chatId,"Выберите действие:");

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add("Кнопка 1");
        keyboardRow1.add("Кнопка 2");
        keyboard.add(keyboardRow1);

        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add("Кнопка 1");
        keyboardRow2.add("Кнопка 2");
        keyboard.add(keyboardRow2);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }

    public SendMessage functionalButton(String chatId){
        SendMessage sendMessage = new SendMessage(chatId, "Выребирте действие:");

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardButton buttonTelephone = new KeyboardButton("Отправить номер телефона");
        buttonTelephone.setRequestContact(true);

        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(buttonTelephone);
        keyboard.add(keyboardRow1);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }




}





























