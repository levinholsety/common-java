package org.lds.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileInputStreamFactory extends InputStreamFactory {
    private File file;

    public FileInputStreamFactory(File file) {
        this.file = file;
    }

    public FileInputStreamFactory(String filename) {
        this.file = new File(filename);
    }

    @Override
    public InputStream newInputStream() throws IOException {
        return new FileInputStream(file);
    }
}
