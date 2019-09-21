package cn.lightfish;

import cn.lightfish.dynamicSQL.DynamicSQLMatcher;

import java.util.Map;
import java.util.Objects;

public class InstructionSetImpl implements InstructionSet {
    public static String toUpperCase(Map map, Object key) {
        return Objects.toString(map.get(key)).toUpperCase();
    }

    public static Byte one() {
        return Byte.valueOf((byte) 1);
    }

    public static String getNameAsString(DynamicSQLMatcher matcher, String key) {
        return matcher.getResult().getName(key);
    }
}