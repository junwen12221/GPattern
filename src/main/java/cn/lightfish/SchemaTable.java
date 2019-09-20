package cn.lightfish;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class SchemaTable {
    String schemaName;
    String tableName;
}

