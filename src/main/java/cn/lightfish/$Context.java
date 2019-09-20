package cn.lightfish;

import java.util.HashMap;

public abstract class $Context {
    final HashMap<Byte, Object> context = new HashMap<>();

    public <T> T put(Byte key, T value) {
        return (T) context.put(key, value);
    }

    public <T> T get(Byte key) {
        return (T) context.get(key);
    }

    public void setErrorMessage(Exception e) {
    }

    public boolean hasResult() {
        return false;
    }

    public void handleError() {
    }
}