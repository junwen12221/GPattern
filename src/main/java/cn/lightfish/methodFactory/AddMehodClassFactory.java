package cn.lightfish.methodFactory;

import javassist.*;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.Set;

public class AddMehodClassFactory {
    private final ClassPool pool = ClassPool.getDefault();
    private final CtClass cc;

    public AddMehodClassFactory(String name, Class superClass) throws Exception {
        ClassClassPath classClassPath = new ClassClassPath(superClass);
        this.pool.appendClassPath(classClassPath);
        this.cc = this.pool.makeClass(name);
        this.cc.setSuperclass(this.pool.get(superClass.getName()));
        this.cc.defrost();
        this.cc.setName(name);
    }

    public void addMethod(String code) throws CannotCompileException {
        cc.addMethod(CtMethod.make(code, cc));
    }

    public void addExpender(String packageName, Class expenderInterface) throws CannotCompileException, NotFoundException {
        Reflections reflections = new Reflections(packageName);
        Set<Class> set = reflections.getSubTypesOf(expenderInterface);
        for (Class aClass : set) {
            addExpender(aClass);
        }
    }

    public void addExpender(Class collections) throws CannotCompileException, NotFoundException {
        ClassClassPath classClassPath = new ClassClassPath(collections);
        this.pool.appendClassPath(classClassPath);
        CtClass ctClass = this.pool.get(collections.getName());
        CtMethod[] methods = ctClass.getMethods();
        for (CtMethod method : methods) {
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                cc.addMethod(CtNewMethod.copy(method, cc, null));
            }
        }
    }

    public Class build(boolean debug) throws CannotCompileException {
        if (debug) {
            cc.debugWriteFile();
        }
        return cc.toClass();
    }

    public void renameClassName(String name) {
        cc.replaceClassName(cc.getName(), name);
    }
}