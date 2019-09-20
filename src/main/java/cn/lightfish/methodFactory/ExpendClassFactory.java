package cn.lightfish.methodFactory;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ExpendClassFactory {
    private final Class<?> expend;
    private final Class<?> orign;
    private final List<String> packageNameList;
    private Class<?> expenderCollection;

    public ExpendClassFactory(Class<?> orign, Class<?> expenderCollection, List<String> packageNameList) {
        this.orign = orign;
        this.expenderCollection = expenderCollection;
        this.packageNameList = packageNameList;
        DynamicType.Builder<?> contextBuilder = new ByteBuddy()
                .subclass(orign);
        for (String packageName : packageNameList) {
            Map<Class, List<Method>> methodMap = getExpendMethods(packageName);
            for (Map.Entry<Class, List<Method>> classListEntry : methodMap.entrySet()) {
                for (Method method : classListEntry.getValue()) {
                    contextBuilder = contextBuilder.defineMethod(method.getName(), method.getReturnType(), Modifier.PUBLIC)
                            .withParameters(method.getParameterTypes()).throwing(method.getExceptionTypes())
                            .intercept(MethodDelegation.to(classListEntry.getKey()));
                }
            }
        }
        expend = contextBuilder.make().load(ExpendClassFactory.class.getClassLoader()).getLoaded();
    }

    public Class<?> getExpend() {
        return expend;
    }

    private Map<Class, List<Method>> getExpendMethods(String name) {
        Map<Class, List<Method>> listMap = new HashMap<>();
        Reflections reflections = new Reflections(name);
        Set<Class<?>> impls = reflections.getSubTypesOf((Class<Object>) expenderCollection);
        for (Class<?> impl : impls) {
            List<Method> list;
            listMap.put(impl, list = new ArrayList<>());
            Method[] declaredMethods = impl.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                if (Modifier.isStatic(declaredMethod.getModifiers())) {
                    list.add(declaredMethod);
                }
            }
        }
        return listMap;
    }
}