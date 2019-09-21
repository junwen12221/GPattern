package cn.lightfish;

import cn.lightfish.dynamicSQL.DynamicSQLMatcher;

public interface Instruction {
    <T> T execute(Object context, DynamicSQLMatcher matcher);
}