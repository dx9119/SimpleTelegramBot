package ru.dx9119;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;

import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;

public class BotCore implements LongPollingSingleThreadUpdateConsumer {
    TelegramClient telegramClient;
    Utils utils;

    public BotCore(String key) {
        this.telegramClient = new OkHttpTelegramClient(key);
        this.utils = new Utils(telegramClient);
    }


    @Override
    public void consume(Update update) {

        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                telegramClient.execute(utils.textEcho(update));
                String text = update.getMessage().getText();

                chatAction(update, text);
                sendImg(update, text);
                sendSticker(update, text);
                setKeyboard(update, text);
                setButtom(update, text);
                getUserInfo(update, text);

            }

            if (update.hasMessage() && update.getMessage().hasPhoto()) {
                telegramClient.execute(utils.photoEcho(update));

                // что пользователь загрузил
                String filePath = getPathFromTgServer(update);

                // Скачиваем что загрузил себе
                downloadFileFromTgServer(filePath);
            }

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    private void downloadFileFromTgServer(String filePath) throws TelegramApiException {
        File downloadFile = telegramClient.downloadFile(filePath);
        System.out.println("Скачали загруженный пользователем файл: " + downloadFile.getAbsolutePath());
    }

    private String getPathFromTgServer(Update update) {
        PhotoSize photo = utils.getPhoto(update);
        String filePath = utils.getFilePath(photo);
        System.out.println("Пользователь загрузил на сервер телеграм файл: " + filePath);
        return filePath;
    }

    private void getUserInfo(Update update, String text) throws TelegramApiException {
        if (text.equals("/whoiam")){
            String msd = utils.getUserInfo(update);
            telegramClient.execute(utils.message(update,msd));

        }
    }

    private void setButtom(Update update, String text) throws TelegramApiException {
        if (text.equals("/button")){
            telegramClient.execute(utils.functionalButton(update.getMessage().getChatId().toString()));
        }
    }

    private void setKeyboard(Update update, String text) throws TelegramApiException {
        if (text.equals("/keyboard")){
            telegramClient.execute(utils.keyboard(update.getMessage().getChatId().toString()));
        }
    }

    private void sendSticker(Update update, String text) throws TelegramApiException {
        if (text.equals("/sticker")) {
            InputFile sticker = new InputFile("CAACAgIAAxkBAAEQKhFpWSM6R1OSsqzZQDGStSi8B4zjZAACprgAAoJrgErD24zNopReGzgE");

            SendSticker sendSticker = new SendSticker(
                    update.getMessage()
                          .getChatId()
                          .toString(), sticker);
            sendSticker.setReplyToMessageId(update.getMessage().getMessageId());

            telegramClient.execute(sendSticker);
        }
    }

    private void sendImg(Update update, String text) throws TelegramApiException {
        if (text.equals("/img")) {
            telegramClient.execute(utils.sendImageFromLocal(
                    "img/album/",
                    "img.png",
                    update.getMessage().getChatId()
            ));
        }
    }

    private void chatAction(Update update, String text) throws TelegramApiException {
        // https://core.telegram.org/bots/api?#sendchataction
        if (text.equals("/action")) {
            SendChatAction chatAction = SendChatAction.builder()
                    .chatId(update.getMessage().getChatId())
                    .action("typing")
                    .build();

            telegramClient.execute(chatAction);
        }
    }


}
