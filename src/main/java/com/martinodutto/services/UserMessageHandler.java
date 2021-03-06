package com.martinodutto.services;

import com.martinodutto.daos.TodoListDao;
import com.martinodutto.exceptions.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.sql.SQLException;

/**
 * Handles "normal" user inputs, i.e. inputs that are not commands.
 */
@Service
public class UserMessageHandler implements InputHandler {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final TodoListDao todoListDao;

    @Autowired
    public UserMessageHandler(TodoListDao todoListDao) {
        this.todoListDao = todoListDao;
    }

    @Override
    public String handleMessage(@NotNull Update update) {
        String message;

        try {
            Message msg = update.getMessage();
            todoListDao.addNote(msg.getText(), msg.getChatId(), msg.getMessageId().longValue());
            message = I18nSupport.i18nize("note.saved");
        } catch (@NotNull PersistenceException | SQLException e) {
            logger.error("An error occurred while saving the note to the database", e);
            message = I18nSupport.i18nize("internal.error");
        }

        return message;
    }

    @Override
    public String handleEditedMessage(@NotNull Update update) {
        String message;

        try {
            Message msg = update.getEditedMessage();
            todoListDao.editNote(msg.getText(), msg.getChatId(), msg.getMessageId().longValue());
            message = I18nSupport.i18nize("note.updated");
        } catch (@NotNull PersistenceException | SQLException e) {
            logger.error("An error occurred while updating the note to the database", e);
            message = I18nSupport.i18nize("internal.error");
        }

        return message;
    }
}
