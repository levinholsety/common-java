package org.lds.net;

import org.lds.Encoding;

public class Parameter {
    private final String name;
    private String value;

    public Parameter(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
    }

    public Parameter(String name, String value) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        String str = Encoding.UTF_8.encodeURIComponent(name) + "=";
        if (value == null) {
            return str;
        }
        return str + Encoding.UTF_8.encodeURIComponent(value);
    }
}
