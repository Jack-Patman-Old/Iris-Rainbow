package com.iris.rainbow.properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Jack on 07/11/2014.
 */
public class Property
{

    private Logger logger = LogManager.getLogger(Property.class.getName());
    private Properties prop;


    public static void main(String[] args)
    {
        Property prop = new Property();
    }

    public Property()
    {
        prop = new Properties();

        logger.debug("Attempting to load properties file");
        try
        {
            prop.load(Property.class.getClassLoader().getResourceAsStream("config.properties"));
        }
        catch (Exception e)
        {
            logger.error("Fatal exception encountered attempting to load config.properties, exception was " + e);
        }

        logger.debug("Successfully located and loaded config.properties");
    }

    public String getDbHost()
    {
        return prop.getProperty("connectionString");
    }

    public String getDbUser()
    {
        return prop.getProperty("username");
    }

    public String getDbPassword()
    {
        return prop.getProperty("password");
    }
}
