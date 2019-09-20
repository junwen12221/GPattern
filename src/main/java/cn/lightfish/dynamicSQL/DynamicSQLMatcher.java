package cn.lightfish.dynamicSQL;

import cn.lightfish.$Context;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public interface DynamicSQLMatcher<T extends $Context> {
    default void match(String sql, T context) {
        match(StandardCharsets.UTF_8.encode(sql), context);
    }

    void match(ByteBuffer sql, T context);
}