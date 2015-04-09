package test.db;
import com.iris.rainbow.db.DAOManager;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class DAOManagerTest
{
    @Test
    public void OpenConnection_valid() throws SQLException
    {
        DAOManager db = new DAOManager();
        db.openConnection();

        assertFalse(db.getConnection().isClosed());
    }

    @Test
    public void CloseConnection_valid() throws SQLException
    {
        DAOManager db = new DAOManager();
        db.openConnection();
        db.closeConnection();

        assertTrue(db.getConnection().isClosed());
    }
}
