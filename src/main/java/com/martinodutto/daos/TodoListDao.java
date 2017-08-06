package com.martinodutto.daos;

import com.martinodutto.dtos.Note;
import com.martinodutto.exceptions.PersistenceException;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public interface TodoListDao {

    boolean tablesAlreadyCreated() throws PersistenceException, SQLException;

    void createTables() throws SQLException, PersistenceException;

    /**
     * @throws SQLException         SQL error occurred while vacuuming the database.
     * @throws PersistenceException Persistence exception while getting the connection.
     * @see @link{https://www.sqlite.org/lang_vacuum.html}
     */
    void vacuum() throws SQLException, PersistenceException;

    void addNote(String message, Long chatId, Long noteId) throws PersistenceException, SQLException;

    @NotNull List<Note> readTodoList(Long chatId) throws PersistenceException, SQLException;

    void deleteTodoList(Long chatId) throws PersistenceException, SQLException;

    void editNote(String editedMessage, Long chatId, Long noteId) throws PersistenceException, SQLException;
}
