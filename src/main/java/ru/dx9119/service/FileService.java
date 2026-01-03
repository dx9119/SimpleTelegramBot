package ru.dx9119.service;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class FileService {
    TelegramClient telegramClient;

    public FileService(TelegramClient telegramClient) {
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

    public void downloadFile(String path) throws TelegramApiException {
        java.io.File file = telegramClient.downloadFile(path);
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

    public void isValidFile(java.io.File file) throws IOException {

        if (!file.exists()) {
            throw new RuntimeException("Файл не найден: " + file.getAbsolutePath());
        }
        if (!file.isFile()) {
            throw new RuntimeException("Указанный путь не является файлом: " + file.getAbsolutePath());
        }
        if (file.length() == 0) {
            throw new RuntimeException("Файл пустой: " + file.getAbsolutePath());
        }
        if (file.length() > 10 * 1024 * 1024) {
            throw new RuntimeException("Файл слишком большой (>10 МБ): " + file.length());
        }

        BufferedImage img = ImageIO.read(file);
        if (img == null) {
            throw new RuntimeException("ImageIO не смог прочитать изображение: " + file.getAbsolutePath());
        }

        System.out.println("Изображение валидно: " + img.getWidth() + "x" + img.getHeight());
    }

}





























