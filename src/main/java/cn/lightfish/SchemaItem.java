package cn.lightfish;

import java.util.*;

public class SchemaItem extends Item {
    final Set<SchemaTable> schemas;
    final HashMap<String, Collection<String>> tableMap = new HashMap<>();
    final int tableMapHash;

    public SchemaItem(Set<SchemaTable> schemas, String pettern, String code) {
        super(pettern, code);
        this.schemas = schemas;

        for (SchemaTable schema : schemas) {
            String schemaName = schema.schemaName.toUpperCase();
            String tableName = schema.tableName.toUpperCase();
            Collection<String> strings = tableMap.computeIfAbsent(schemaName, s -> new HashSet<>());
            strings.add(tableName);
        }
        this.tableMapHash = tableMap.hashCode();
    }

    public Set<SchemaTable> getSchemas() {
        return schemas;
    }

    public Map<String, Collection<String>> getTableMap() {
        return tableMap;
    }

    public int getTableMapHash() {
        return tableMapHash;
    }
}