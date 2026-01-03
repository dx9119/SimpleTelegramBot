package ru.dx9119;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static void main(String[] args) throws Exception {
        try {
            String key = System.getenv("TELEGRAM_TOKEN");

            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
            botsApplication.registerBot(key, new BotCore(key));

        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
