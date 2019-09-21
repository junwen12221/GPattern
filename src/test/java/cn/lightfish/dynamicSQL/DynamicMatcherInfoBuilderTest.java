package cn.lightfish.dynamicSQL;

import cn.lightfish.GPatternException;
import cn.lightfish.Item;
import cn.lightfish.pattern.GPatternBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicMatcherInfoBuilderTest {
    @Test
    public void test() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        DynamicMatcherInfoBuilder builder = new DynamicMatcherInfoBuilder();
        builder.add("{any};{any1};", "code");
        builder.addSchema("DB1.TABLE1,DB2.TABLE2", "select {any2}", "code1");
        builder.build(pettern -> patternBuilder.addRule(pettern));
        HashMap<Integer, List<Item>> map = builder.ruleInstructionMap;
        Map<String, Collection<String>> tableMap = builder.getTableMap();
        Assert.assertTrue(tableMap.get("DB1").contains("TABLE1"));
        Assert.assertTrue(tableMap.get("DB2").contains("TABLE2"));

        Assert.assertEquals(2, map.size());
    }

    @Test(expected = GPatternException.PatternConflictException.class)
    public void test2() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        DynamicMatcherInfoBuilder builder = new DynamicMatcherInfoBuilder();
        builder.add("{any};{any1};", "code");
        builder.addSchema("DB1.TABLE1,DB2.TABLE2", "{any};{any1};", "code1");
        builder.build(pettern -> patternBuilder.addRule(pettern));
        HashMap<Integer, List<Item>> map = builder.ruleInstructionMap;
    }

    @Test(expected = GPatternException.PatternConflictException.class)
    public void test3() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        DynamicMatcherInfoBuilder builder = new DynamicMatcherInfoBuilder();
        builder.add("{any};{any1};", "code");
        builder.add("{any};{any1};", "code");
        builder.build(pettern -> patternBuilder.addRule(pettern));
        HashMap<Integer, List<Item>> map = builder.ruleInstructionMap;
    }

    @Test(expected = GPatternException.PatternConflictException.class)
    public void test4() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        DynamicMatcherInfoBuilder builder = new DynamicMatcherInfoBuilder();
        builder.addSchema("DB1.TABLE1,DB2.TABLE2", "{any};{any1};", "code1");
        builder.addSchema("DB1.TABLE1,DB2.TABLE2", "{any};{any1};", "code1");
        builder.build(pettern -> patternBuilder.addRule(pettern));
        HashMap<Integer, List<Item>> map = builder.ruleInstructionMap;
    }

    @Test(expected = GPatternException.PatternConflictException.class)
    public void test5() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        DynamicMatcherInfoBuilder builder = new DynamicMatcherInfoBuilder();
        builder.addSchema("DB1.TABLE1,DB2.TABLE2", null, "code1");
        builder.addSchema("DB1.TABLE1,DB2.TABLE2", null, "code1");
        builder.build(pettern -> patternBuilder.addRule(pettern));
        HashMap<Integer, List<Item>> map = builder.ruleInstructionMap;
    }
}