package com.martinodutto.components;

import com.martinodutto.daos.TodoListDao;
import com.martinodutto.exceptions.PersistenceException;
import com.martinodutto.services.CommandHandler;
import com.martinodutto.services.I18nSupport;
import com.martinodutto.services.PropertiesManager;
import com.martinodutto.services.UserMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.sql.SQLException;

@Component
public class TodoListBot extends TelegramLongPollingBot {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final PropertiesManager propertiesManager;

    private final TodoListDao todoListDao;

    private final CommandHandler commandHandler;

    private final UserMessageHandler userMessageHandler;

    @Autowired
    public TodoListBot(PropertiesManager propertiesManager, TodoListDao todoListDao, CommandHandler commandHandler, UserMessageHandler userMessageHandler) {
        this.propertiesManager = propertiesManager;
        this.todoListDao = todoListDao;
        this.commandHandler = commandHandler;
        this.userMessageHandler = userMessageHandler;
    }

    @PostConstruct
    public void init() throws SQLException, PersistenceException {
        try {
            if (!todoListDao.tablesAlreadyCreated()) {
                todoListDao.createTables();
                todoListDao.vacuum();
            }
            logger.debug("Tables correctly initialized");
        } catch (@NotNull SQLException | PersistenceException e) {
            logger.fatal("The bot cannot be initialized: database problem", e);
            throw e;
        }
    }

    @Override
    public void onUpdateReceived(@Nullable Update update) {
        String message = null;
        Long chatId = null;
        Message inputMsg;

        logger.debug("Update received: {}", update);

        if (update != null && (update.hasMessage() || update.hasEditedMessage())) {
            inputMsg = (update.hasMessage() ? update.getMessage() : update.getEditedMessage());
            chatId = inputMsg.getChatId();
            if (inputMsg.getText() != null) {
                if (inputMsg.isUserMessage()) {
                    if (inputMsg.isCommand()) {
                        message = (update.hasMessage() ? commandHandler.handleMessage(update) : commandHandler.handleEditedMessage(update));
                    } else {
                        message = (update.hasMessage() ? userMessageHandler.handleMessage(update) : userMessageHandler.handleEditedMessage(update));
                    }
                } else {
                    message = I18nSupport.i18nize("unrecognized.input");
                }
            } else {
                message = I18nSupport.i18nize("no.message.received");
            }
        }

        SendMessage reply = new SendMessage();
        reply.setChatId(chatId);
        reply.setText(message);
        try {
            sendMessage(reply);
        } catch (TelegramApiException e) {
            logger.error("An error occurred while send the reply message", e);
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
}
