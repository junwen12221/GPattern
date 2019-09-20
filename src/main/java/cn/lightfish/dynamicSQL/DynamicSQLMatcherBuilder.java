package cn.lightfish.dynamicSQL;

import cn.lightfish.Instruction;
import cn.lightfish.InstructionSet;
import cn.lightfish.Item;
import cn.lightfish.SchemaItem;
import cn.lightfish.methodFactory.AddMehodClassFactory;
import cn.lightfish.methodFactory.ExpendClassFactory;
import cn.lightfish.pattern.*;
import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicSQLMatcherBuilder {
    private final String dafaultSchema;
    private final DynamicMatcherInfoBuilder dynamicMatcherInfoBuilder = new DynamicMatcherInfoBuilder();
    private final GPatternBuilder patternBuilder = new GPatternBuilder(0);
    private final DynamicMatcherInfoBuilder.PatternComplier patternComplier = pettern -> patternBuilder.addRule(pettern);
    private int id = 0;
    private HashMap<Integer, List<Item>> runtimeMap;
    private TableCollectorBuilder tableCollctorbuilder;

    public DynamicSQLMatcherBuilder(String dafaultSchema) {
        this.dafaultSchema = dafaultSchema;
    }

    public void build(List<String> packageNameList, boolean debug) throws NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException {
        Class c = Instruction.class;
        ExpendClassFactory expendClassFactory = new ExpendClassFactory(c, InstructionSet.class, packageNameList);
        Class<?> expendClass = expendClassFactory.getExpend();
        this.runtimeMap = dynamicMatcherInfoBuilder.build(patternComplier);
        for (List<Item> value : runtimeMap.values()) {
            for (Item item : value) {
                String name = c.getSimpleName() + id++;
                String code = item.getCode();
                AddMehodClassFactory addMehodClassFactory = new AddMehodClassFactory(name, expendClass);
                addMehodClassFactory.implMethod("execute", code);
                Class build = addMehodClassFactory.build(debug);
                Instruction o = (Instruction) build.newInstance();
                item.setInstruction(o);
            }
        }
        this.tableCollctorbuilder = new TableCollectorBuilder(patternBuilder.geIdRecorder(), dynamicMatcherInfoBuilder.getTableMap());
    }

    public DynamicSQLMatcher create() {
        TableCollector tableCollector = tableCollctorbuilder.create();
        GPattern gPattern = patternBuilder.createGroupPattern(tableCollector);
        return (sql, context) -> {
            GPatternMatcher matcher = gPattern.matcher(sql);
            Instruction instruction = getInstruction(matcher, tableCollector);
            if (instruction != null) {
                instruction.execute(context);
                return;
            }
            throw new UnsupportedOperationException();
        }
                ;
    }

    private Instruction getInstruction(GPatternMatcher matcher, TableCollector tableCollector) {
        if (matcher.acceptAll()) {
            int id = matcher.id();
            List<Item> items = runtimeMap.get(id);
            if (items.size() == 1) {
                return items.get(0).getInstruction();
            } else {
                Map<String, Collection<String>> collectionMap = tableCollector.geTableMap();
                int hash = collectionMap.hashCode();
                for (Item item : items) {
                    SchemaItem schemaItem = (SchemaItem) item;
                    if (hash == schemaItem.getTableMapHash()) {
                        return schemaItem.getInstruction();
                    }
                }
            }
        }
        return null;
    }

}