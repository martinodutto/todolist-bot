package com.martinodutto.services;

import com.martinodutto.daos.TodoListDao;
import com.martinodutto.dtos.Note;
import com.martinodutto.exceptions.PersistenceException;
import com.martinodutto.exceptions.UnknownCommandException;
import com.martinodutto.types.Command;
import com.martinodutto.utils.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.sql.SQLException;

/**
 * Handles user command inputs.
 */
@Service
public class CommandHandler implements InputHandler {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final TodoListDao todoListDao;

    public CommandHandler(TodoListDao todoListDao) {
        this.todoListDao = todoListDao;
    }

    @Nullable
    @Override
    public String handleMessage(@org.jetbrains.annotations.NotNull Update update) {
        String response;
        Message message = update.getMessage();

        Command c;
        try {
            c = getCommand(message);
            switch (c.getKindOf()) {
                case START_ME_UP: {
                    response = I18nSupport.i18nize("start.me.up");
                    break;
                }
                case HELP: {
                    response = I18nSupport.i18nize("help.section"); // TODO
                    break;
                }
                case READ_LIST: {
                    try {
                        response = getNotesList(message.getChatId());
                    } catch (@org.jetbrains.annotations.NotNull SQLException | PersistenceException e) {
                        logger.error("Error while getting the todo list", e);
                        response = I18nSupport.i18nize("error.reading.list");
                    }
                    break;
                }
                case DELETE: {
                    try {
                        todoListDao.deleteTodoList(message.getChatId());
                        response = I18nSupport.i18nize("todo.list.successfully.deleted");
                    } catch (@org.jetbrains.annotations.NotNull PersistenceException | SQLException e) {
                        logger.error("Error while getting the notes list", e);
                        response = I18nSupport.i18nize("error.deleting.list");
                    }
                    break;
                }
                case EDIT: {
                    try {
                        if (c.validateParameters()) {
                            if (todoListDao.editNote(c.getParameters().get(1), message.getChatId(), todoListDao.getIdFromNumber(Long.parseLong(c.getParameters().get(0)), message.getChatId())) > 0) {
                                response = I18nSupport.i18nize("note.updated");
                            } else {
                                response = I18nSupport.i18nize("nothing.to.update");
                            }
                        } else {
                            response = I18nSupport.i18nize("wrong.parameters");
                        }
                    } catch (PersistenceException | SQLException e) {
                        logger.error("Error while updating the note", e);
                        response = I18nSupport.i18nize("error.updating.note");
                    }
                    break;
                }
                default: {
                    response = I18nSupport.i18nize("this.should.never.happen");
                }
            }
        } catch (UnknownCommandException uce) {
            response = I18nSupport.i18nize("unrecognized.instruction");
        }

        return response;
    }

    @org.jetbrains.annotations.NotNull
    @Override
    public String handleEditedMessage(Update update) {
        return I18nSupport.i18nize("cannot.edit.command");
    }

    private Command getCommand(@org.jetbrains.annotations.NotNull Message message) throws UnknownCommandException {
        Command command = null;

        if (message.getText().startsWith("/") && message.getText().length() > 1) {
            final String[] strings = StringTokenizer.translateCommandline(message.getText().substring(1));
            if (strings.length > 0) {
                command = new Command(strings[0]);
                for (int j = 1; j < strings.length; j++) {
                    command.addParameter(strings[j]);
                }
            }
        } else {
            throw new UnknownCommandException();
        }

        return command;
    }

    private String getNotesList(Long chatId) throws SQLException, PersistenceException {
        StringBuilder sb = new StringBuilder();
        int cnt = 0;
        for (Note note : todoListDao.readTodoList(chatId)) {
            sb.append(++cnt).append(") ").append(note.getIdea()).append("\n");
        }

        if (sb.length() == 0) {
            sb.append(I18nSupport.i18nize("todo.list.empty"));
        }

        logger.debug("Notes list for chat id {}: {}", chatId, sb.toString());

        return sb.toString();
    }
}
