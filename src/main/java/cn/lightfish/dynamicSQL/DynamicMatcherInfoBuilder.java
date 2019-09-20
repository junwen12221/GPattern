package cn.lightfish.dynamicSQL;

import cn.lightfish.Item;
import cn.lightfish.SchemaItem;
import cn.lightfish.SchemaTable;
import cn.lightfish.TextItem;

import java.util.*;

public class DynamicMatcherInfoBuilder {

    final List<TextItem> petterns = new ArrayList<>();
    final List<SchemaItem> schemaPetterns = new ArrayList<>();
    final Map<String, Collection<String>> tableMap = new HashMap<>();

    public static void main(String[] args) {


        DynamicMatcherInfoBuilder builder = new DynamicMatcherInfoBuilder();
        builder.add("select * from travelrecord;", "$proxy($SQL,\"dataNode1\")");
        builder.add("select * from {db} travelrecord;", "$proxy($removeSchema($db,$SQL),\"dataNode1\")");

        builder.add("set XA = 1;", " setXA(true)");
        builder.add("set XA = 0;", "setXA(false)");

        builder.add("begin;", "!$XA?$proxyOnMaster(\"begin;\".\"dataNode1\"):$JDBC.begin()");
        builder.add("commit;", "!$XA?$proxyOnMaster(\"commit;\".\"dataNode1\"):$JDBC.commit()");

        builder.addSchema("TESTDB.travelrecord", "select {}", "return $proxyOnBalance($autoRemoveSchema($SQL),\"dataNode1\")");
        builder.addSchema("TESTDB.travelrecord", "{}", "return $proxyOnMaster($autoRemoveSchema($SQL),\"dataNode1\")");
        builder.addSchema("TESTDB.travelrecord,TESTDB.user", "select {}", "return $calcite($SQL)");

    }

    public void addTable(String schemaName, String tableName) {
        Collection<String> set = tableMap.computeIfAbsent(schemaName.toUpperCase(), (s) -> new HashSet<>());
        set.add(tableName.toUpperCase());
    }

    private void addSchema(String schema, String pattern, String code) {
        String[] split = schema.split(",");
        Set<SchemaTable> set = new HashSet<>();
        for (String s : split) {
            String[] split1 = s.split(".");
            String schemaName = split1[0].intern();
            String tableName = split1[1].intern();
            addTable(schemaName, tableName);
            SchemaTable schemaTable = new SchemaTable(schemaName, tableName);
            set.add(schemaTable);
        }
        schemaPetterns.add(new SchemaItem(set, pattern, code));
    }

    private void add(String pettern, String code) {
        petterns.add(new TextItem(pettern, code));
    }

    public HashMap<Integer, List<Item>> build(PatternComplier complier) {
        Map<Integer, TextItem> textItems = new HashMap<>();
        for (TextItem pettern : petterns) {
            int id = complier.complie(pettern.getPettern());
            if (textItems.put(id, pettern) != null) {
                throw new UnsupportedOperationException();
            }
        }

        Map<Integer, List<SchemaItem>> delayDecisionSet = new HashMap<>();
        for (SchemaItem schemaPettern : schemaPetterns) {
            if (schemaPettern.getPettern() != null) {
                int id = complier.complie(schemaPettern.getPettern());
                if (!textItems.containsKey(id)) {
                    List<SchemaItem> list = delayDecisionSet.computeIfAbsent(id, integer -> new ArrayList<>());
                    list.add(schemaPettern);
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        }
        HashSet<Set<SchemaTable>> set = new HashSet<>();
        for (Map.Entry<Integer, List<SchemaItem>> entry : delayDecisionSet.entrySet()) {
            for (SchemaItem schemaItem : entry.getValue()) {
                if (!set.add(schemaItem.getSchemas())) {
                    throw new UnsupportedOperationException();
                }
            }
        }

        HashMap<Integer, List<Item>> matcher = new HashMap<>();
        textItems.forEach((key, value) -> matcher.put(key, Collections.singletonList(value)));
        for (Map.Entry<Integer, List<SchemaItem>> integerListEntry : delayDecisionSet.entrySet()) {
            List<Item> items = matcher.computeIfAbsent(integerListEntry.getKey(), integer -> new ArrayList<>());
            items.addAll(integerListEntry.getValue());
        }
        return matcher;
    }

    public Map<String, Collection<String>> getTableMap() {
        return tableMap;
    }

    public interface PatternComplier {
        int complie(String pettern);
    }

}