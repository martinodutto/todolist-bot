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
                    response = "Hi! I'm a todo-list bot. You can send me new todo-entries by simply entering a message. To edit your to-do list, just use any of my supported commands (start typing '/' to see them, or use the command '/help')";
                    break;
                }
                case HELP: {
                    response = "Help section. Work in progress!"; // TODO
                    break;
                }
                case READ_LIST: {
                    try {
                        response = getNotesList(message.getChatId());
                    } catch (@org.jetbrains.annotations.NotNull SQLException | PersistenceException e) {
                        logger.error("Error while getting the todo list", e);
                        response = "Error while getting the list";
                    }
                    break;
                }
                case DELETE: {
                    try {
                        todoListDao.deleteTodoList(message.getChatId());
                        response = "Todo list successfully deleted. Feel free to start a new one!";
                    } catch (@org.jetbrains.annotations.NotNull PersistenceException | SQLException e) {
                        logger.error("Error while getting the notes list", e);
                        response = "Error while deleting the list";
                    }
                    break;
                }
                case EDIT: {
                    try {
                        if (c.validateParameters()) {
                            if (todoListDao.editNote(c.getParameters().get(1), message.getChatId(), todoListDao.getIdFromNumber(Long.parseLong(c.getParameters().get(0)), message.getChatId())) > 0) {
                                response = "Great! Your note has been updated";
                            } else {
                                response = "Nothing to update";
                            }
                        } else {
                            response = "Wrong number or type of the parameters: you must enter a command like /edit 2 \"My fancy new idea\"";
                        }
                    } catch (PersistenceException | SQLException e) {
                        logger.error("Error while updating the note", e);
                        response = "Error while updating the note";
                    }
                    break;
                }
                default: {
                    response = "This should never happen!";
                }
            }
        } catch (UnknownCommandException uce) {
            response = "I'm sorry, I don't recognize this instruction";
        }

        return response;
    }

    @org.jetbrains.annotations.NotNull
    @Override
    public String handleEditedMessage(Update update) {
        return "Sorry but I can't edit any previously issued command";
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
            sb.append("Todo list empty. Simply send me a message to add your first note");
        }

        logger.debug("Notes list for chat id {}: {}", chatId, sb.toString());

        return sb.toString();
    }
}
