package org.lds.net;

import org.lds.Encoding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QueryString {
    private final List<Parameter> parameters = new ArrayList<Parameter>();

    public QueryString() {
    }

    public QueryString(String queryString) {
        String[] paramStringArray = queryString.split("&");
        for (String paramString : paramStringArray) {
            int index = paramString.indexOf('=');
            if (index < 0) {
                String name = Encoding.UTF_8.decodeURIComponent(paramString);
                parameters.add(new Parameter(name, null));
            } else {
                String name = Encoding.UTF_8.decodeURIComponent(paramString.substring(0, index));
                String value = Encoding.UTF_8.decodeURIComponent(paramString.substring(index + 1));
                parameters.add(new Parameter(name, value));
            }
        }
    }

    public void addParameter(String name, String value) {
        if (name == null) {
            return;
        }
        name = Encoding.UTF_8.encodeURIComponent(name);
        if (value != null) {
            value = Encoding.UTF_8.encodeURIComponent(value);
        }
        parameters.add(new Parameter(name, value));
        return;
    }

    public void removeParameters(String name) {
        Iterator<Parameter> it = parameters.iterator();
        while (it.hasNext()) {
            Parameter param = it.next();
            if (param.getName().equals(name)) {
                it.remove();
            }
        }
    }

    public void clearParameters() {
        parameters.clear();
    }

    public boolean isEmpty() {
        return parameters.size() == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (parameters.size() > 0) {
            sb.append(parameters.get(0));
            for (int i = 1; i < parameters.size(); i++) {
                sb.append('&');
                sb.append(parameters.get(i));
            }
        }
        return sb.toString();
    }

}
