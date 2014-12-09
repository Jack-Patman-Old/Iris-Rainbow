package com.iris.rainbow.db;

import com.iris.rainbow.properties.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAOManager
{
    private Logger logger = LogManager.getLogger(DAOManager.class.getName());
    private Connection con = null;

    /**
     * Attempts to openConnection connection with postgreSQL database
     **/
    public void openConnection()
    {
        logger.debug("Attempting to openConnection connection with database");

        try
        {

            Property properties = new Property();
            con = DriverManager.getConnection(properties.getDbHost(), properties.getDbUser(), properties.getDbPassword());
        }
        catch (SQLException e)
        {
            logger.error("Fatal exception encountered attempting to create connection with database, exception was " + e);
            closeConnection();
        }

        logger.debug("Successfully opened connection with Database");
    }

    /**
     * Attempts to closeConnection connection with PostgreSQL database.
     **/
    public void closeConnection()
    {
        logger.debug("Attempting to closeConnection connection with database");

        try
        {
            con.close();
        }
        catch (SQLException e)
        {
            logger.error("Fatal exception encountered attempting to closeConnection connection with database, exception was " + e);
            e.printStackTrace();
        }

        logger.debug("Successfully closed connection with database");
    }

    /**
     * Returns an open connection, if connection is invalid, attempts to open connection.
     *
     * @return connection object to allow database access.
     **/
    public Connection getConnection()
    {
        try
        {
            if (con != null && !con.isClosed())
            {
                return con;
            }
            else
            {
                openConnection();
                return con;
            }
        }
        catch (SQLException e)
        {
            logger.error("Unexpected exception checking state of Database connection, exception was " + e);
        }

        return null;
    }
}
