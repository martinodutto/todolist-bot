package com.martinodutto.services;

import com.martinodutto.exceptions.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
@Singleton
public class DbManager {

    private final String dbName;

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    public DbManager(@Value("${db.name}") String dbName) {
        this.dbName = dbName;
        logger.debug("Using database: {}", dbName);
    }

    /**
     * The unique connection to be used throughout all the application.
     */
    private static Connection connection;

    @PostConstruct
    public void init() throws SQLException {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
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

    /**
     * Safely terminates the database manager, allowing the persistence layer to be shutdown properly.
     *
     * @throws SQLException Error while terminating the database manager.
     */
    public void terminate() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
