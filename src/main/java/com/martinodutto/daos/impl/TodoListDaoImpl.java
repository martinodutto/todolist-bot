package com.martinodutto.daos.impl;

import com.martinodutto.daos.AbstractDao;
import com.martinodutto.daos.TodoListDao;
import com.martinodutto.dtos.Note;
import com.martinodutto.exceptions.PersistenceException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
                            "chatid," +
                            "noteid," +
                            "idea," +
                            "PRIMARY KEY (chatid, noteid))");
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

    @NotNull
    @Override
    public List<Note> readTodoList(Long chatId) throws PersistenceException, SQLException {
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement("SELECT chatid, noteid, idea FROM todolist_table WHERE chatid = ? ORDER BY noteid ASC")) {
            List<Note> notes = new ArrayList<>();
            Note note;

            statement.setLong(1, chatId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    note = new Note(rs.getLong("chatid"), rs.getLong("noteid"), rs.getString("idea"));
                    notes.add(note);
                }
            }

            return notes;
        }
    }

    @Override
    public void deleteTodoList(Long chatId) throws PersistenceException, SQLException {
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement("DELETE FROM todolist_table WHERE chatid = ?")) {
            statement.setLong(1, chatId);

            statement.execute();

            logger.debug("Deleted todo-list for chat id = {}", chatId);
        }
    }

    @Override
    public void editNote(String editedMessage, Long chatId, Long noteId) throws PersistenceException, SQLException {
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement("UPDATE todolist_table SET idea = ? WHERE chatid = ? AND noteid = ?")) {
            int idx = 1;
            statement.setString(idx++, editedMessage);
            statement.setLong(idx++, chatId);
            statement.setLong(idx++, noteId);

            statement.execute();

            logger.debug("Updated todo-list for chat id = {} and note id = {}", chatId, noteId);
        }
    }
}
