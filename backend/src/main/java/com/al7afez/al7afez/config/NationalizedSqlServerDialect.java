package com.al7afez.al7afez.config;

import java.sql.SQLType;
import java.sql.Types;

import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.type.SqlTypes;

/**
 * Custom dialect that maps Java String to NVARCHAR so Arabic text stores correctly
 * without needing @Nationalized on every field.
 */
public class NationalizedSqlServerDialect extends SQLServerDialect {
    @Override
    protected String columnType(int sqlTypeCode) {
        if (SqlTypes.VARCHAR == sqlTypeCode)
            return "nvarchar($l)";
        if (SqlTypes.LONGVARCHAR == sqlTypeCode)
            return "nvarchar(max)";
        if (SqlTypes.CLOB == sqlTypeCode)
            return "nvarchar(max)";
        return super.columnType(sqlTypeCode);
    }
}
