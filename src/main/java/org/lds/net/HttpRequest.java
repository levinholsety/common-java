package org.lds.net;

import org.lds.Encoding;
import org.lds.io.IOUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";

    static {
        CookieHandler.setDefault(new CookieManager());
    }

    private final String path;
    private final QueryString qs;
    private int timeout = 5000;
    private HttpResponse referer;

    public HttpRequest(String urlString) {
        int index = urlString.indexOf('?');
        if (index < 0) {
            path = urlString;
            qs = new QueryString();
        } else {
            path = urlString.substring(0, index);
            qs = new QueryString(urlString.substring(index + 1));
        }

    }

    public int getTimeout() {
        return timeout;
    }

    public HttpRequest setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public HttpResponse getReferer() {
        return referer;
    }

    public HttpRequest setReferer(HttpResponse referer) {
        this.referer = referer;
        return this;
    }

    public HttpRequest addParameter(String name, String value) {
        qs.addParameter(name, value);
        return this;
    }

    public HttpResponse doGet() throws IOException {
        return request(METHOD_GET, null, null);
    }

    public HttpResponse doPost() throws IOException {
        return request(METHOD_POST, null, null);
    }

    public HttpResponse doPost(QueryString qs) throws IOException {
        InputStream dataStream = qs == null ? null
                : new ByteArrayInputStream(Encoding.UTF_8.encode(qs.toString()));
        return request(METHOD_POST, dataStream, "application/x-www-form-urlencoded");
    }

    public HttpResponse doPost(InputStream dataStream) throws IOException {
        return request(METHOD_POST, dataStream, "application/octet-stream");
    }

    private HttpResponse request(String method, InputStream dataStream, String contentType) throws IOException {
        StringBuilder sb = new StringBuilder(path);
        if (!qs.isEmpty()) {
            sb.append('?').append(qs);
        }
        String address = sb.toString();
        URL url = new URL(address);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);
        conn.setRequestMethod(method);
        if (dataStream != null) {
            conn.setDoOutput(true);
            conn.addRequestProperty("Content-Type", contentType);
        }
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:72.0) Gecko/20100101 Firefox/72.0");
        if (referer != null) {
            conn.addRequestProperty("Referer", referer.getAddress());
        }
        try {
            conn.connect();
            if (dataStream != null) {
                IOUtil.transfer(dataStream, conn.getOutputStream());
            }
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return new HttpResponse(path, address, responseCode, null);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtil.transfer(conn.getInputStream(), out);
            return new HttpResponse(path, address, responseCode, out.toByteArray());
        } finally {
            conn.disconnect();
        }
    }
}
