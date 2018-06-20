package builder;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * TableMetadata
 */
public class TableMetadata {

    public static TableMetadata readResultSet(
        final ResultSetMetaData metadata, final int index) throws SQLException {
            return new TableMetadata(
                metadata.getColumnName(index), 
                metadata.getColumnClassName(index)
            );
    }

    private final String fieldName;
    private final String className;

    public TableMetadata(final String fieldName, final String className) {
        this.fieldName = fieldName;
        this.className = className;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getClassName() {
        return className;
    }
}