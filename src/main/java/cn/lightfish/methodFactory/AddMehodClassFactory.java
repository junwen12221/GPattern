package cn.lightfish.methodFactory;

import javassist.*;

public class AddMehodClassFactory {
    private final ClassPool pool = ClassPool.getDefault();
    private final CtClass cc;

    public AddMehodClassFactory(String name, Class superClass) throws NotFoundException, CannotCompileException {
        this.pool.appendClassPath(new ClassClassPath(superClass));
        this.cc = this.pool.makeClass(name);
        this.cc.setSuperclass(this.pool.get(superClass.getName()));
        this.cc.defrost();
        this.cc.setName(name);
    }

    public void addMethod(String code) throws CannotCompileException {
        cc.addMethod(CtMethod.make(code, cc));
    }

    public void implMethod(String name, String code) throws CannotCompileException {
        CtMethod[] declaredMethods = cc.getDeclaredMethods();
        for (CtMethod method : declaredMethods) {
            if (name.equals(method.getName())) {
                method.setBody(code);
                return;
            }
        }
        throw new UnsupportedOperationException();
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