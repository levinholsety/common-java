package org.lds.net;

import org.lds.Encoding;

import java.net.HttpURLConnection;

public class HttpResponse {

    private final String path;
    private final String address;
    private final int code;
    private final byte[] data;

    HttpResponse(String path, String address, int code, byte[] data) {
        this.path = path;
        this.address = address;
        this.code = code;
        this.data = data;
    }

    public String getPath() {
        return path;
    }

    public String getAddress() {
        return address;
    }

    public int getCode() {
        return code;
    }

    public boolean isStatusOk() {
        return code == HttpURLConnection.HTTP_OK;
    }

    public byte[] getData() {
        return data;
    }

    public String getString() {
        if (data == null) {
            return null;
        }
        return Encoding.UTF_8.decode(data);
    }

}
