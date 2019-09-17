package cn.lightfish.pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.eclipse.collections.impl.map.mutable.primitive.LongObjectHashMap;

import java.util.*;


/**
 * db1.table1.column1
 * db1.table1
 * table1.column1
 * column1
 * table1
 * <p>
 * dafault
 * <p>
 * column1 ->dafault.column1
 * table1->dafault.table1
 * <p>
 * <p>
 * db1.table1->
 * table1.column1
 */
public class TableCollector implements GPatternTokenCollector {
    private final TableInfo[] tables = new TableInfo[32];
    private final GPatternIdRecorder recorder;
    private final Map<String, Map<String, TableInfo>> schemaInfos = new HashMap<>();
    private final LongObjectHashMap<TableInfo> map = new LongObjectHashMap<>();
    State state = State.EXCPECT_ID;
    long currentSchemaLeftShift32;
    int first;
    int second;
    private int dotHash;
    private int tableIndex;


    public TableCollector(GPatternIdRecorder recorder, Map<String, Collection<String>> schemaInfos) {
        this.recorder = recorder;
        this.dotHash = recorder.createConstToken(".").hashCode();
        for (Map.Entry<String, Collection<String>> stringSetEntry : schemaInfos.entrySet()) {
            String schemaName = stringSetEntry.getKey();
            List<Integer> schemaHashList = record(recorder, schemaName);
            Map<String, TableInfo> tableInfoMap = this.schemaInfos.computeIfAbsent(schemaName, (s) -> new HashMap<>());
            Collection<String> tableNames = stringSetEntry.getValue();
            for (String tableName : tableNames) {
                List<Integer> tableNameHashList = record(recorder, tableName);
                for (Integer schemaNameHash : schemaHashList) {
                    for (Integer tableNameHash : tableNameHashList) {
                        long hash = schemaNameHash;
                        hash = hash << 32;
                        hash = hash | tableNameHash;
                        TableInfo tableInfo = new TableInfo(schemaName, tableName, schemaNameHash, tableNameHash, hash);
                        tableInfoMap.put(tableName, tableInfo);
                        map.put(hash, tableInfo);
                    }
                }
            }
        }
    }

    public void useSchema(String schema) {
        Map<String, TableInfo> stringTableInfoMap = schemaInfos.get(schema);
        if (stringTableInfoMap == null) throw new UnsupportedOperationException();
        long hash = stringTableInfoMap.values().iterator().next().getSchema();
        hash = hash << 32;
        currentSchemaLeftShift32 = hash;
    }

    private List<Integer> record(GPatternIdRecorder recorder, String text) {
        String lowerCase = text.toLowerCase();
        String upperCase = text.toUpperCase();

        ArrayList<Integer> list = new ArrayList<>();
        list.add(recorder.createConstToken(lowerCase).hashCode());
        list.add(recorder.createConstToken(upperCase).hashCode());
        list.add(recorder.createConstToken("`" + lowerCase + "`").hashCode());
        list.add(recorder.createConstToken("`" + upperCase + "`").hashCode());
        return list;
    }


    @Override
    public void onCollectStart() {
        this.tableIndex = 0;
    }

    @Override
    public void collect(GPatternSeq token) {
        int hash = token.hashCode();
        switch (state) {
            case EXCPECT_ID: {
                if (hash == dotHash) {
                    state = State.EXPECT_ATTRIBUTE;
                } else {
                    first = hash;
                    collect(first);
                }
                break;
            }
            case EXPECT_ATTRIBUTE: {
                second = hash;
                state = State.EXPECT_DOT;
                break;
            }
            case EXPECT_DOT: {
                if (hash == dotHash) {
                    state = State.EXPECT_TABLE;
                } else {
                    collect(first, second);
                    clearState();
                    state = State.EXCPECT_ID;
                }
                break;
            }
            case EXPECT_TABLE: {
                collect(first, second);
                clearState();
                state = State.EXCPECT_ID;
                break;
            }
        }
    }

    @Override
    public void onCollectEnd() {
    }

    private void collect(long first) {
        long hash = currentSchemaLeftShift32 | first;
        add(hash);
    }

    private void add(long hash) {
        TableInfo o = map.get(hash);
        if (o != null) {
            tables[tableIndex++] = o;
        }
    }

    private void collect(int first, int second) {
        long hash = first;
        hash = hash << 32 | second;
        add(hash);
    }

    private void clearState() {
        this.first = 0;
        this.second = 0;
    }

    public TableInfo[] getTableArray() {
        return Arrays.copyOf(tables, tableIndex);
    }

    public Map<String, Collection<String>> geTableMap(Map<String, Collection<String>> map) {
        Map<String, Collection<String>> collectionMap = new HashMap<>();
        for (int i = 0; i < this.tableIndex; i++) {
            TableInfo table = tables[i];
            collectionMap.computeIfAbsent(table.getSchemaName(), s -> new HashSet<>()).add(table.getTableName());
        }
        return collectionMap;
    }

    public Map<String, Collection<String>> geTableMap() {
        return geTableMap(new HashMap<>());
    }


    enum State {
        EXCPECT_ID,
        EXPECT_ATTRIBUTE,
        EXPECT_DOT,
        EXPECT_TABLE
    }


    @AllArgsConstructor
    @Getter
    static class TableInfo {
        final String schemaName;
        final String tableName;
        final int schema;
        final int table;
        final long hash;
    }


}