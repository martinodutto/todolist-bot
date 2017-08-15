package com.martinodutto.daos.impl;

import com.martinodutto.daos.AbstractDao;
import com.martinodutto.daos.TodoListDao;
import com.martinodutto.dtos.Note;
import com.martinodutto.exceptions.PersistenceException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    public int addNote(String message, @Nullable Long chatId, @Nullable Long noteId) throws PersistenceException, SQLException {
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement("INSERT INTO todolist_table (noteid, chatid, idea) VALUES (?, ?, ?)")) {
            int res;
            if (chatId != null && noteId != null) {
                int idx = 0;
                statement.setLong(++idx, noteId);
                statement.setLong(++idx, chatId);
                statement.setString(++idx, message);

                res = statement.executeUpdate();

                logger.debug("Persisted new note with id = {} for chat id = {}", noteId, chatId);
            } else {
                res = 0;
                logger.warn("No new note to add with id = {} for chat id = {}", noteId, chatId);
            }

            return res;
        }
    }

    @Override
    public int addNote(@NotNull Note note) throws PersistenceException, SQLException {
        return addNote(note.getIdea(), note.getChatId(), note.getNoteId());
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
    public int deleteTodoList(Long chatId) throws PersistenceException, SQLException {
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement("DELETE FROM todolist_table WHERE chatid = ?")) {
            statement.setLong(1, chatId);

            logger.debug("Deleting todo-list for chat id = {}", chatId);

            return statement.executeUpdate();
        }
    }

    @Override
    public int editNote(String editedMessage, @Nullable Long chatId, @Nullable Long noteId) throws PersistenceException, SQLException {
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement("UPDATE todolist_table SET idea = ? WHERE chatid = ? AND noteid = ?")) {
            int res;
            if (chatId != null && noteId != null) {
                int idx = 0;
                statement.setString(++idx, editedMessage);
                statement.setLong(++idx, chatId);
                statement.setLong(++idx, noteId);

                res = statement.executeUpdate();

                logger.debug("Updated todo-list for chat id = {} and note id = {}", chatId, noteId);
            } else {
                res = 0;
                logger.debug("Nothing to update for chat id = {} and note id = {}", chatId, noteId);
            }
            return res;
        }
    }

    @Override
    public int editNote(@NotNull Note note) throws PersistenceException, SQLException {
        return editNote(note.getIdea(), note.getChatId(), note.getNoteId());
    }

    @Override
    public int deleteNote(@Nullable Long chatId, @Nullable Long noteId) throws PersistenceException, SQLException {
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement("DELETE FROM todolist_table WHERE chatid = ? AND noteid = ?")) {
            int res;
            if (chatId != null && noteId != null) {
                int idx = 0;
                statement.setLong(++idx, chatId);
                statement.setLong(++idx, noteId);

                res = statement.executeUpdate();

                logger.debug("Deleted note for chat id = {} and note id = {}", chatId, noteId);
            } else {
                res = 0;
                logger.debug("Nothing to delete for chat id = {} and note id = {}", chatId, noteId);
            }
            return res;
        }
    }

    @Override
    public int deleteNote(@NotNull Note note) throws PersistenceException, SQLException {
        return deleteNote(note.getChatId(), note.getNoteId());
    }

    @Override
    public @Nullable Long getIdFromNumber(long noteNumber, Long chatId) throws PersistenceException, SQLException {
        Long noteId = null;
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement("SELECT noteid FROM (SELECT n.noteid, (SELECT SUM(1) FROM todolist_table WHERE noteid <= n.noteid) as ROWNUM FROM todolist_table n WHERE n.chatid = ? ORDER BY n.noteid ASC) WHERE ROWNUM = ?")) {
            statement.setLong(1, chatId);
            statement.setLong(2, noteNumber);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    noteId = rs.getLong(1);
                }
            }

            return noteId;
        }
    }
}
