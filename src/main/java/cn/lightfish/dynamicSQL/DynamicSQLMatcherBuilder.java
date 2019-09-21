package cn.lightfish.dynamicSQL;

import cn.lightfish.*;
import cn.lightfish.methodFactory.AddMehodClassFactory;
import cn.lightfish.pattern.GPattern;
import cn.lightfish.pattern.GPatternBuilder;
import cn.lightfish.pattern.TableCollector;
import cn.lightfish.pattern.TableCollectorBuilder;

import java.util.*;

public class DynamicSQLMatcherBuilder {
    private static long id = 0;
    private final String dafaultSchema;
    private final DynamicMatcherInfoBuilder dynamicMatcherInfoBuilder = new DynamicMatcherInfoBuilder();
    private final GPatternBuilder patternBuilder = new GPatternBuilder(0);
    private final DynamicMatcherInfoBuilder.PatternComplier patternComplier = pettern -> patternBuilder.addRule(pettern);
    private HashMap<Integer, List<Item>> runtimeMap;
    private HashMap<Set<SchemaTable>, SchemaItem> runtimeMap2;
    private TableCollectorBuilder tableCollctorbuilder;

    public DynamicSQLMatcherBuilder(String dafaultSchema) {
        if (dafaultSchema != null) {
            this.dafaultSchema = dafaultSchema.toUpperCase();
        } else {
            this.dafaultSchema = null;
        }
    }

    public void addSchema(String schema, String pattern, String code) {
        dynamicMatcherInfoBuilder.addSchema(schema, pattern, code);
    }

    public void add(String pettern, String code) {
        dynamicMatcherInfoBuilder.add(pettern, code);
    }

    public void build(String packageName, boolean debug) throws Exception {
        build(Collections.singletonList(packageName), debug);
    }

    public void build(List<String> packageNameList, boolean debug) throws Exception {
        dynamicMatcherInfoBuilder.build(patternComplier);
        this.runtimeMap = dynamicMatcherInfoBuilder.ruleInstructionMap;
        this.runtimeMap2 = dynamicMatcherInfoBuilder.tableInstructionMap;
        ArrayList<List<Item>> list = new ArrayList<>(runtimeMap.values());
        list.add(new ArrayList<>(this.runtimeMap2.values()));
        for (List<Item> value : list) {
            for (Item item : value) {
                String name = Instruction.class.getSimpleName() + id++;
                String code = item.getCode();
                AddMehodClassFactory addMehodClassFactory = new AddMehodClassFactory(name, Instruction.class);
                addMehodClassFactory.addExpender(packageNameList, InstructionSet.class);
                addMehodClassFactory.implMethod("execute", "java.util.Map ctx = (java.util.Map)$1;" +
                        "cn.lightfish.dynamicSQL.DynamicSQLMatcher matcher = (cn.lightfish.dynamicSQL.DynamicSQLMatcher)$2;", code);
                Class build = addMehodClassFactory.build(debug);
                Instruction o = (Instruction) build.newInstance();
                item.setInstruction(o);
            }
        }
        this.tableCollctorbuilder = new TableCollectorBuilder(patternBuilder.geIdRecorder(), dynamicMatcherInfoBuilder.getTableMap());
    }

    public DynamicSQLMatcher createMatcher() {
        TableCollector tableCollector = tableCollctorbuilder.create();
        if (dafaultSchema != null) {
            tableCollector.useSchema(dafaultSchema);
        }
        GPattern gPattern = patternBuilder.createGroupPattern(tableCollector);
        return new DynamicSQLMatcher(tableCollector, gPattern, runtimeMap, runtimeMap2);
    }


}