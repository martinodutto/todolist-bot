package com.martinodutto.daos.impl;

import com.martinodutto.daos.AbstractDao;
import com.martinodutto.daos.TodoListDao;
import com.martinodutto.exceptions.PersistenceException;
import org.springframework.stereotype.Service;

import javax.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Service("TodoListDao")
@Singleton
public class TodoListDaoImpl extends AbstractDao implements TodoListDao {

    @Override
    public boolean tablesAlreadyCreated() throws PersistenceException, SQLException {
        try (Statement statement = dbManager.getConnection().createStatement();
             ResultSet rs = statement.executeQuery(
                     "SELECT COUNT(*) FROM (SELECT name FROM sqlite_master " +
                             "WHERE type IN ('table','view') AND name = 'todolist_table')")
        ) {
            return rs.next() && rs.getInt(1) == 1;
        }
    }

    @Override
    public void createTables() throws SQLException, PersistenceException {
        try (Statement statement = dbManager.getConnection().createStatement()) {
            statement.execute(
                    "CREATE TABLE todolist_table (" +
                            "noteid PRIMARY KEY," +
                            "chatid," +
                            "idea)");
        }
    }

    @Override
    public void vacuum() throws SQLException, PersistenceException {
        try (Statement statement = dbManager.getConnection().createStatement()) {
            statement.execute("VACUUM");
        }
    }

    @Override
    public void addNote(String message, Long chatId, Long noteId) throws PersistenceException, SQLException {
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement("INSERT INTO todolist_table (noteid, chatid, idea) VALUES (?, ?, ?)")) {
            int idx = 1;
            statement.setLong(idx++, noteId);
            statement.setLong(idx++, chatId);
            statement.setString(idx++, message);

            statement.execute();

            logger.debug("Persisted new note with id = {} for chat id = {}", noteId, chatId);
        }
    }

    @Override
    public Long getNextNoteId(Long chatId) throws PersistenceException, SQLException {
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement("SELECT MAX(noteid) FROM todolist_table WHERE chatid = ?")) {
            statement.setLong(1, chatId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1) + 1;
                } else {
                    return 1L;
                }
            }
        }
    }
}
