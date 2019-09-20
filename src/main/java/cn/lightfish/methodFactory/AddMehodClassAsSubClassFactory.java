package cn.lightfish.methodFactory;

import javassist.*;

public class AddMehodClassAsSubClassFactory {
    private final ClassPool pool = ClassPool.getDefault();
    private final CtClass cc;

    public AddMehodClassAsSubClassFactory(String name, Class superClass) throws NotFoundException {
        this.pool.appendClassPath(new ClassClassPath(superClass));
        this.cc = this.pool.makeClass(name);
        this.cc.subclassOf(pool.get(superClass.getName()));
    }

    public void addMethod(String code) throws CannotCompileException {
        cc.addMethod(CtMethod.make(code, cc));
    }

    public void implMethod(String name, String code) throws CannotCompileException, NotFoundException {
        cc.getDeclaredMethod(name).setBody(code);
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