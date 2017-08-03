package com.martinodutto.components;

import com.martinodutto.daos.TodoListDao;
import com.martinodutto.exceptions.PersistenceException;
import com.martinodutto.services.PropertiesManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.BotSession;

import javax.annotation.PostConstruct;
import java.sql.SQLException;

@Component
public class TodoListBot extends TelegramLongPollingBot {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private BotSession session;

    private PropertiesManager propertiesManager;

    private TodoListDao todoListDao;

    @Autowired
    public TodoListBot(PropertiesManager propertiesManager, TodoListDao todoListDao) {
        this.propertiesManager = propertiesManager;
        this.todoListDao = todoListDao;
    }

    @PostConstruct
    public void init() throws SQLException, PersistenceException {
        try {
            if (!todoListDao.tablesAlreadyCreated()) {
                todoListDao.createTables();
                todoListDao.vacuum();
            }
            logger.debug("Tables correctly initialized");
        } catch (SQLException | PersistenceException e) {
            logger.fatal("The bot cannot be initialized: database problem", e);
            throw e;
        }

//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            logger.debug("Shutting down bot...");
//            session.stop();
//        }));
    }

    @Override
    public void onUpdateReceived(Update update) {
        String message = null;
        Long chatId = null;

        logger.debug("Update received: {}", update);

        if (update != null && update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            if (update.getMessage().getText() != null) {
                try {
                    todoListDao.addNote(update.getMessage().getText(), chatId, todoListDao.getNextNoteId(chatId));
                    message = "Great! Your note has been saved to my infallible memory ;)";
                } catch (PersistenceException | SQLException e) {
                    logger.error("An error occurred while saving the note to the database", e);
                    message = "Sorry, it seems there has been an internal error :(";
                }
            } else {
                message = "Sorry, but you didn't send me any note!";
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
        return propertiesManager.getProperty("bot.username");
    }

    @Override
    public String getBotToken() {
        return propertiesManager.getProperty("bot.token");
    }

    public void setSession(BotSession session) {
        this.session = session;
    }
}
