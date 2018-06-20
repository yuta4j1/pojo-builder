package builder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.h2.Driver;
import org.junit.Test;

public class PojoBuilderTest {

    @Test public void renderTest() throws ClassNotFoundException, IOException, SQLException {

        final String jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

        // setup
        Driver.load();
        Connection initConn = DriverManager.getConnection(jdbcUrl);
        Statement create = initConn.createStatement();
        create.execute("create table USER (id int primary key, name varchar)");
        create.close();
        initConn.close();

        Properties testConfig = new Properties();
        testConfig.setProperty(TableAccess.JDBC_CLASS, Driver.class.getName());
        testConfig.setProperty(TableAccess.JDBC_URL, jdbcUrl);
        testConfig.setProperty(TableAccess.DB_USER, "");
        testConfig.setProperty(TableAccess.DB_PASS, "");

        TableAccess access = new TableAccess(testConfig);
        PojoBuilder pojoBuilder = new PojoBuilder(access, "USER");

        pojoBuilder.writeTo(System.out);
    }
}