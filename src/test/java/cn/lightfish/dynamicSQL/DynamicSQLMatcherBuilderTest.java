package cn.lightfish.dynamicSQL;

import cn.lightfish.$Context;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.junit.Test;

import java.io.IOException;

public class DynamicSQLMatcherBuilderTest {
//
//    @Test
//    public void test() throws Exception {
//        DynamicSQLMatcherBuilder builder = new DynamicSQLMatcherBuilder("db1");
//        builder.add("select 1;","System.out.prinln(1);");
//        builder.addSchema("DB1.TABLE,DB2.TABLE2","select * from {tables}","System.out.prinln(1);");
//        builder.build("cn.lightfish.methodFactory",false);
//        DynamicSQLMatcher dynamicSQLMatcher = builder.create();
//        $Context context = new $Context();
//        dynamicSQLMatcher.match("select 1;",context);
//    }
}