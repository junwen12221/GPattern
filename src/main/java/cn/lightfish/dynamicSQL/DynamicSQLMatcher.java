package cn.lightfish.dynamicSQL;

import cn.lightfish.Instruction;
import cn.lightfish.Item;
import cn.lightfish.SchemaItem;
import cn.lightfish.SchemaTable;
import cn.lightfish.pattern.GPattern;
import cn.lightfish.pattern.GPatternMatcher;
import cn.lightfish.pattern.GPatternUTF8Lexer;
import cn.lightfish.pattern.TableCollector;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public final class DynamicSQLMatcher {
    private final TableCollector tableCollector;
    private final GPattern gPattern;
    private final HashMap<Integer, List<Item>> rule_tables_map;
    private final Map<Integer, SchemaItem> table_map;

    public DynamicSQLMatcher(TableCollector tableCollector, GPattern gPattern, HashMap<Integer, List<Item>> runtimeMap, HashMap<Set<SchemaTable>, SchemaItem> table_map) {
        this.tableCollector = tableCollector;
        this.gPattern = gPattern;
        this.rule_tables_map = runtimeMap;
        this.table_map = table_map.values().stream().collect(Collectors.toMap(k -> k.getTableMapHash(), v -> v));
    }

    public Instruction match(String sql) {
        return match(StandardCharsets.UTF_8.encode(sql));
    }

    public Instruction match(ByteBuffer sql) {
        return getInstruction(gPattern.matcherAndCollect(sql), tableCollector);
    }

    private Instruction getInstruction(GPatternMatcher matcher, TableCollector tableCollector) {
        boolean match = tableCollector.isMatch();
        if (matcher.acceptAll()) {
            int id = matcher.id();
            List<Item> items = rule_tables_map.get(id);
            if (items.size() == 1) {
                return items.get(0).getInstruction();
            } else if (match) {
                Map<String, Collection<String>> collectionMap = tableCollector.geTableMap();
                int hash = collectionMap.hashCode();
                for (Item item : items) {
                    SchemaItem schemaItem = (SchemaItem) item;
                    if (hash == schemaItem.getTableMapHash()) {
                        return schemaItem.getInstruction();
                    }
                }
            } else {
                return null;
            }
        }

        if (match) {
            Map<String, Collection<String>> collectionMap = tableCollector.geTableMap();
            int hash = collectionMap.hashCode();
            SchemaItem schemaItem = table_map.get(hash);
            if (schemaItem == null) {
                return null;
            } else {
                return schemaItem.getInstruction();
            }
        }
        return null;
    }

    public TableCollector getTableCollector() {
        return tableCollector;
    }

    public GPattern getgPattern() {
        return gPattern;
    }

    public HashMap<Integer, List<Item>> getRule_tables_map() {
        return rule_tables_map;
    }

    public Map<Integer, SchemaItem> getTable_map() {
        return table_map;
    }

    public GPatternUTF8Lexer getLexer() {
        return gPattern.getUtf8Lexer();
    }

    public Map<String, Collection<String>> geTables() {
        return getTableCollector().geTableMap();
    }

    public GPatternMatcher getResult() {
        return getgPattern().getMatcher();
    }
}