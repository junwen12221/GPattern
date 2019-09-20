package cn.lightfish.methodFactory;

import cn.lightfish.$Context;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ExpendClassFactoryTest {
    @Test
    public void test() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        ExpendClassFactory factory = new ExpendClassFactory($Context.class, TestExpenderInterface.class, Arrays.asList("cn.lightfish.methodFactory"));
        Class<?> expender = factory.getExpend();
        Object o = expender.newInstance();
        Method name = o.getClass().getMethod("name");
        String value = (String) name.invoke(o);
        Assert.assertEquals("name", value);
    }
}