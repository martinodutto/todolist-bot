package com.martinodutto.daos;

import com.martinodutto.exceptions.PersistenceException;

import java.sql.SQLException;

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

    Long getNextNoteId(Long chatId) throws PersistenceException, SQLException;
}
