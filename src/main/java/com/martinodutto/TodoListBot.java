package com.martinodutto;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.BotSession;

import javax.annotation.PostConstruct;

@Component
public class TodoListBot extends TelegramLongPollingBot {

    private BotSession session;

    @PostConstruct
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            session.stop();
        }));
    }

    @Override
    public void onUpdateReceived(Update update) {
        String message = null;
        Long chatId = null;

        System.out.println("Update received: " + update);

        if (update != null && update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            if (update.getMessage().getText() != null) {
                message = "Ok, I got your message!";
            } else {
                message = "I'm sorry, but you didn't send me any message!";
            }
        }

        SendMessage reply = new SendMessage();
        reply.setChatId(chatId);
        reply.setText(message);
        try {
            sendMessage(reply);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "DutToDoListBot";
    }

    @Override
    public String getBotToken() {
        return "425079312:AAFVxDvPk-dxLXSs8076oLAbwzkaOjkBMXI";
    }

    /*@Override
    public void onClosing() {
        System.out.println("Stopping session...");
        session.stop();
    }*/

    public void setSession(BotSession session) {
        this.session = session;
    }
}
