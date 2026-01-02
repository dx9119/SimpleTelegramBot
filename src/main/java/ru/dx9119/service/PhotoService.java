package ru.dx9119.service;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;

import java.util.Comparator;
import java.util.List;

public class PhotoService {

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

}
