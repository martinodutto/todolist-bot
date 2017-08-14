package com.martinodutto;

import com.martinodutto.components.TodoListBot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.generics.BotSession;

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

    @Autowired
    public App(TodoListBot todoListBot) {
        this.todoListBot = todoListBot;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        BotSession session = telegramBotsApi.registerBot(todoListBot);
        if (!session.isRunning()) {
            session.start();
        }
        logger.info("Session started!");
    }
}
