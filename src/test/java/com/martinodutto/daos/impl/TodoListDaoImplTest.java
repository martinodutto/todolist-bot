package com.martinodutto.daos.impl;

import com.martinodutto.daos.TodoListDao;
import com.martinodutto.dtos.Note;
import com.martinodutto.exceptions.PersistenceException;
import com.martinodutto.services.DbManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TodoListDaoImplTest {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private DbManager dbManager;

    @Autowired
    private TodoListDao todoListDao;

    @Before
    public void setUp() throws Exception {
        dbManager.getConnection().setAutoCommit(false);
        if (!todoListDao.tablesAlreadyCreated()) {
            todoListDao.createTables();
        }
    }

    @After
    public void tearDown() throws Exception {
        dbManager.getConnection().rollback();
    }

    @Test
    public void addNoteTest() throws Exception {
        assertEquals("Add note test", 1, todoListDao.addNote("message", 1L, 65l));
    }

    @Test
    public void addNoteNullTest() throws Exception {
        assertEquals("Add note with invalid primary key test", 0, todoListDao.addNote("message", 1L, null));
    }

    @Test
    public void readTodoListTest() throws Exception {
        final Long chatId = 1001L;

        assertEquals("Read todo-list test", populateTestChat(chatId), todoListDao.readTodoList(chatId));
    }

    @Test
    public void deleteTodoListTest() throws Exception {
        final Long chatId = 102L;
        int records = populateTestChat(chatId).size();

        assertEquals("Delete todo-list test", records, todoListDao.deleteTodoList(chatId));
    }

    @Test
    public void editNoteTest() throws Exception {
        final Long chatId = 144L;
        final List<Note> notes = populateTestChat(chatId);

        final Note note = notes.get(1);

        final String newIdea = "My edited idea";
        note.setIdea(newIdea);
        todoListDao.editNote(note);

        final List<Note> postEditNotes = todoListDao.readTodoList(chatId);

        assertEquals("Edit note test", newIdea, postEditNotes.get(1).getIdea());
    }

    @Test
    public void editNoteNullTest() throws Exception {
        final Long chatId = 145L;
        final List<Note> notes = populateTestChat(chatId);

        final Note note = notes.get(1);

        final String newIdea = "My edited idea";
        note.setIdea(newIdea);
        note.setChatId(null);

        assertEquals("Test behaviour in case of invalid primary key", 0, todoListDao.editNote(note));
    }

    @Test
    public void getIdFromNumberTest() throws Exception {
        Long chatId = 101L;
        populateTestChat(chatId);

        assertEquals("Normal use case", 24l, (long) todoListDao.getIdFromNumber(2, chatId));
    }

    @Test
    public void getIdFromNumberNullTest() throws Exception {
        final Long chatId = 101L;
        int records = populateTestChat(chatId).size();

        assertEquals("Exceptional use case: index out of range", null, todoListDao.getIdFromNumber(records + 1, chatId));
    }

    @Test
    public void getIdFromNumberEmptyChatTest() throws Exception {
        final Long chatId = 101L;
        populateTestChat(chatId);

        assertEquals("Exceptional use case: empty chat", null, todoListDao.getIdFromNumber(2, 102L));
    }

    private List<Note> populateTestChat(Long chatId) throws SQLException, PersistenceException {
        final List<Note> notes = new ArrayList<>();

        notes.add(new Note(chatId, 23l, "firstMessage"));
        notes.add(new Note(chatId, 24l, "secondMessage"));

        int added = 0;
        for (Note note : notes) {
            added += todoListDao.addNote(note);
        }

        logger.info("Added {} notes for chat id {}", added, chatId);

        return notes;
    }
}