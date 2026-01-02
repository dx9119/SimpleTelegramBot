package ru.dx9119.service;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

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

}





























