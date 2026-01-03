package ru.dx9119;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;

import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.dx9119.service.FileService;
import ru.dx9119.service.MessageService;

import java.io.File;


public class BotCore implements LongPollingSingleThreadUpdateConsumer {
    TelegramClient telegramClient;
    MessageService messageService;
    FileService fileService;

    public BotCore (String key){
        this.telegramClient = new OkHttpTelegramClient(key);
        this.fileService = new FileService(telegramClient);
        this.messageService = new MessageService();
    }


    @Override
    public void consume(Update update) {

        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                telegramClient.execute(messageService.textEcho(update));

                String text = update.getMessage().getText();

                // https://core.telegram.org/bots/api?#sendchataction
                SendChatAction chatAction = SendChatAction.builder()
                        .chatId(update.getMessage().getChatId())
                        .action("typing")
                        .build();
                if(text.equals("/action")){
                    telegramClient.execute(chatAction);
                }
                if (text.equals("/img")){
                    telegramClient.execute(fileService.sendImageFromLocal(
                            "img/album/",
                            "img.png",
                            update.getMessage().getChatId()
                    ));
                }
            }

            if (update.hasMessage() && update.getMessage().hasPhoto()){
                telegramClient.execute(fileService.photoEcho(update));

                // что пользователь загрузил
                PhotoSize photo = fileService.getPhoto(update);
                String filePath = fileService.getFilePath(photo);
                System.out.println("Пользователь загрузил на сервер телеграм файл: " + filePath);

                // скачать с серверов телеграмм
                File downloadFile = telegramClient.downloadFile(filePath);
                System.out.println("Скачали загруженный пользователем файл: "+downloadFile.getAbsolutePath());
            }

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
}
