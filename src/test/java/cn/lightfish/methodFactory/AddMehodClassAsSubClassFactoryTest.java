package cn.lightfish.methodFactory;

import cn.lightfish.$Context;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AddMehodClassAsSubClassFactoryTest {
    @Test
    public void test() throws NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException {
        AddMehodClassFactory factory = new AddMehodClassFactory("Name", $Context.class);
        Class o = factory.build(false);
        Object o1 = o.newInstance();
        Assert.assertNotNull(o1);
        Assert.assertEquals(o1.getClass().getName(), "Name");
    }

    @Test
    public void test1() throws NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException {
        AddMehodClassFactory factory = new AddMehodClassFactory("Name1", $Context.class);
        Class o = factory.build(true);
        Object o1 = o.newInstance();
        Assert.assertNotNull(o1);
    }

    @Test
    public void test2() throws NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        AddMehodClassFactory factory = new AddMehodClassFactory("Name2", $Context.class);
        factory.addMethod("public String name(){return \"hello\";}");
        Class o = factory.build(true);
        Object o1 = o.newInstance();
        Method name = o1.getClass().getDeclaredMethod("name");
        String value = (String) name.invoke(o1);
        Assert.assertEquals("hello", value);
    }
}