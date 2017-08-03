package com.martinodutto.services;

import com.martinodutto.exceptions.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
@Singleton
public class DbManager {

    private static final String DB_NAME = "todo-list-bot.db";

    private final Logger logger = LogManager.getLogger(this.getClass());

    /**
     * The unique connection to be used throughout all the application.
     */
    private static Connection connection;

    @PostConstruct
    public void init() throws SQLException {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
        } catch (SQLException se) {
            logger.fatal("An error occurred while initializing the database manager", se);
            throw se;
        }
    }

    public Connection getConnection() throws PersistenceException {
        if (connection == null) {
            throw new PersistenceException("Database manager never initialized");
        }
        return connection;
    }

    public void terminate() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
