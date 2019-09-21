package cn.lightfish.methodFactory;

import javassist.*;
import javassist.bytecode.MethodInfo;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

public class AddMehodClassFactory {
    private final ClassPool pool = ClassPool.getDefault();
    private CtClass cc;

    public AddMehodClassFactory(String name, Class superClass) throws Exception {
        ClassClassPath classClassPath = new ClassClassPath(superClass);
        this.pool.appendClassPath(new LoaderClassPath(AddMehodClassFactory.class.getClassLoader()));
        this.pool.appendClassPath(classClassPath);
        if (name != null) {
            this.cc = this.pool.getOrNull(name);
            if (this.cc == null) {
                this.cc = this.pool.makeClass(name);
            }
        } else {
            this.cc = this.pool.makeClass(name);
        }

        CtClass ctClass = this.pool.get(superClass.getName());
        this.cc.defrost();
        if (ctClass.isInterface()) {
            CtClass[] interfaces = this.cc.getInterfaces();
            boolean found = false;
            for (CtClass anInterface : interfaces) {
                if (anInterface.getName().equals(ctClass.getName())) {
                    found = true;
                }
            }
            if (!found) {
                this.cc.addInterface(ctClass);
            }
        } else {
            this.cc.setSuperclass(ctClass);
        }
        this.cc.setName(name);
    }

    public void addMethod(String code) throws CannotCompileException {
        cc.addMethod(CtMethod.make(code, cc));
    }

    public void implMethod(String name, String init, String code) throws CannotCompileException, NotFoundException {
        CtMethod[] methods = cc.getMethods();
        for (CtMethod method : methods) {
            MethodInfo methodInfo = method.getMethodInfo2();
            if (name.equals(method.getName())) {
                CtMethod cm = new CtMethod(method.getReturnType(), name, method.getParameterTypes(), cc);
                if (init != null && !"".equals(init)) {
                    String s = "{" + init + ";" + code + "}";
                    cm.setBody(s);
                } else {
                    cm.setBody(code);
                }
                cc.addMethod(cm);
                break;
            }
        }
    }

    public void addExpender(List<String> packageNameList, Class expenderInterface) throws CannotCompileException, NotFoundException {
        for (String s : packageNameList) {
            addExpender(s, expenderInterface);
        }
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

    public Class build(boolean debug) throws Exception {
        if (debug) {
            cc.debugWriteFile();
        } else {
            cc.writeFile();
        }
        return cc.toClass();
    }
}