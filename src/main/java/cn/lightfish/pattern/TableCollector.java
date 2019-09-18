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
    private final int dotHash;
    private final LongObjectHashMap<TableInfo> map;
    private final TableInfo[] tables = new TableInfo[32];
    private final TableCollectorBuilder builder;
    private int tableIndex;
    private State state = State.EXCPECT_ID;
    private long currentSchemaLeftShift32;
    private int first;
    private int second;

    public TableCollector(TableCollectorBuilder builder) {
        this.builder = builder;
        this.dotHash = builder.dotHash;
        this.map = builder.map;
    }

    public void useSchema(String schema) {
        Integer intHash = builder.schemaHash.get(schema);
        if (intHash == null) throw new UnsupportedOperationException();
        long hash = intHash;
        hash = hash << 32;
        currentSchemaLeftShift32 = hash;
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
                collect(first, second);
                clearState();
                state = State.EXPECT_DOT;
                break;
            }
            case EXPECT_DOT: {
                if (hash == dotHash) {
                    state = State.EXPECT_TABLE;
                } else {
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
        for (int i = 0; i < this.tableIndex; i++) {
            TableInfo table = tables[i];
            map.computeIfAbsent(table.getSchemaName(), s -> new HashSet<>()).add(table.getTableName());
        }
        return map;
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