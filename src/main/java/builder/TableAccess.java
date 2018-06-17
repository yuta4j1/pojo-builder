package builder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Pair;

public class TableAccess {

    private static String databaseName;

    private static String user;

    private static String password;

    private static String tableName;

    private static Connection conn;

    public TableAccess() {
        if(conn == null) {
            conn = getConnection();
        }
    }

    public static Connection getConnection() {
        databaseName = "kinokoyama";
        user = "root";
        password = "root";
        tableName = "USER";

        String url = "jdbc:mysql://localhost/" + databaseName;

        Connection conn = null;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, user, password);

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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <L, R> List<Pair<L, R>> getTableColumnAndType() {
        if(conn == null) getConnection();

        String sql = "SELECT * FROM " + tableName;
        List<Pair<L, R>> columnAndType = new ArrayList<>();
        try(PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();) {
            ResultSetMetaData metaInfo = rs.getMetaData();
            int columnNum = metaInfo.getColumnCount();
            for(int i = 1; i <= columnNum; i++) {
                columnAndType.add(new Pair(metaInfo.getColumnName(i), metaInfo.getColumnClassName(i)));
            }

        }catch(SQLException e) {
            throw new RuntimeException(e);
        }

        return columnAndType;
    }

}
