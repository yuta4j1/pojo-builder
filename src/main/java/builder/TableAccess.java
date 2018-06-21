package builder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TableAccess {

    static final String JDBC_CLASS  = "pojobuilder.jdbc.driver";
    static final String JDBC_URL    = "pojobuilder.jdbc.url";
    static final String DB_USER     = "pojobuilder.db.user";
    static final String DB_PASS     = "pojobuilder.db.pass";

    private String driverClassName;
    private String jdbcUrl;
    private String user;
    private String password;

    public TableAccess(final Properties props) {
        this.driverClassName = props.getProperty(JDBC_CLASS);
        this.jdbcUrl = props.getProperty(JDBC_URL);
        this.user = props.getProperty(DB_USER);
        this.password = props.getProperty(DB_PASS);
    }

    public Connection getConnection() {

        Connection conn = null;

        try {
            Class.forName(driverClassName).newInstance();
            conn = DriverManager.getConnection(jdbcUrl, user, password);

            System.out.println("Connected...");

            return conn;

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TableMetadata> getTableColumnAndType(final String tableName) {
        Connection conn = getConnection();

        String sql = "SELECT * FROM " + tableName;
        List<TableMetadata> columnAndType = new ArrayList<>();
        try(PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();
                ) {
            ResultSetMetaData metaInfo = rs.getMetaData();
            int columnNum = metaInfo.getColumnCount();
            for(int i = 1; i <= columnNum; i++) {
                columnAndType.add(TableMetadata.readResultSet(metaInfo, i));
            }

        }catch(SQLException e) {
            throw new RuntimeException(e);
        }

        return columnAndType;
    }

}
