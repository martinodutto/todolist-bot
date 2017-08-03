package com.martinodutto;

import com.martinodutto.components.TodoListBot;
import com.martinodutto.services.DbManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.generics.BotSession;

import java.sql.SQLException;

/**
 * Main class.
 */
@SpringBootApplication
public class App implements CommandLineRunner {

    static {
        ApiContextInitializer.init();
    }

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final TodoListBot todoListBot;

    private DbManager dbManager;

    @Autowired
    public App(TodoListBot todoListBot, DbManager dbManager) {
        this.todoListBot = todoListBot;
        this.dbManager = dbManager;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        configureDatabase();

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        BotSession session = telegramBotsApi.registerBot(todoListBot);
        todoListBot.setSession(session);
        if (!session.isRunning()) {
            session.start();
        }
        logger.debug("Session started!");
    }

    public void configureDatabase() throws SQLException {
        try {
            dbManager.init();
        } catch (SQLException se) {
            logger.fatal("An error occurred while initializing the database manager", se);
            throw se;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.debug("Shutting down main application...");
            try {
                dbManager.terminate();
            } catch (SQLException se) {
                logger.error("An error occurred while shutting down the database manager", se);
            }
        }));
    }
}
