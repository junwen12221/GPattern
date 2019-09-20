package cn.lightfish.pattern;

import cn.lightfish.GPatternException;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

import java.util.*;

public class TableCollectorBuilder {
    final Map<String, Integer> schemaHash = new HashMap<>();
    final IntObjectHashMap<TableCollector.TableInfo> map = new IntObjectHashMap<>();
    final int dotHash;
    private final GPatternIdRecorder recorder;
    private final Map<String, Map<String, TableCollector.TableInfo>> schemaInfos = new HashMap<>();

    public TableCollectorBuilder(GPatternIdRecorder recorder, Map<String, Collection<String>> schemaInfos) {
        this.recorder = recorder;
        this.dotHash = recorder.createConstToken(".").hashCode();
        for (Map.Entry<String, Collection<String>> stringSetEntry : schemaInfos.entrySet()) {
            String schemaName = stringSetEntry.getKey();
            Set<Integer> schemaHashList = record(recorder, schemaName);
            Map<String, TableCollector.TableInfo> tableInfoMap = this.schemaInfos.computeIfAbsent(schemaName, (s) -> new HashMap<>());
            Collection<String> tableNames = stringSetEntry.getValue();
            for (String tableName : tableNames) {
                Set<Integer> tableNameHashList = record(recorder, tableName);
                for (Integer schemaNameHash : schemaHashList) {
                    schemaHash.computeIfAbsent(schemaName, s -> schemaNameHash);
                    for (Integer tableNameHash : tableNameHashList) {
                        int hash = schemaNameHash;
                        hash = hash ^ tableNameHash;
                        TableCollector.TableInfo tableInfo = new TableCollector.TableInfo(schemaName, tableName, schemaNameHash, tableNameHash, hash);
                        tableInfoMap.put(tableName, tableInfo);
                        if (!map.containsKey(hash)) {
                            map.put(hash, tableInfo);
                        } else {
                            TableCollector.TableInfo info = map.get(hash);
                            if (info.getSchemaName().equals(schemaName)) {
                                if (info.getTableName().equals(tableName)) {
                                    continue;
                                }
                            }
                            throw new GPatternException.ConstTokenHashConflictException("Hash conflict between {0}.{1} and {2}.{3}",
                                    info.getSchemaName(), info.getTableName(),
                                    schemaName, tableName);
                        }
                    }
                }
            }
        }
    }

    private Set<Integer> record(GPatternIdRecorder recorder, String text) {
        String lowerCase = text.toLowerCase();
        String upperCase = text.toUpperCase();

        Set<Integer> list = new HashSet<>();
        list.add(recorder.createConstToken(lowerCase).hashCode());
        list.add(recorder.createConstToken(upperCase).hashCode());
        list.add(recorder.createConstToken("`" + lowerCase + "`").hashCode());
        list.add(recorder.createConstToken("`" + upperCase + "`").hashCode());
        return list;
    }

    public TableCollector create() {
        return new TableCollector(this);
    }

}