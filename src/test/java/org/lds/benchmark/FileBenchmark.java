package org.lds.benchmark;

import org.lds.ByteArrayUtil;
import org.lds.Util;
import org.lds.io.IOUtil;
import org.openjdk.jmh.annotations.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Fork(1)
public class FileBenchmark {

    private static final int fileSize = 1024;
    private static final File file = getTestDataFile(fileSize);
    private static final byte[] data = ByteArrayUtil.random(fileSize);

    public static void main(String[] args) throws Exception {
        //new Runner(new OptionsBuilder().include(FileBenchmark.class.getSimpleName()).build()).run();
        new FileBenchmark().writeWithMappedByteBuffer();
        System.out.println(Arrays.toString(new FileBenchmark().readWithMappedByteBuffer()));
    }

    private static File getTestDataFile(int size) {
        final File file = new File(String.format("D:\\Temp\\test\\%s_%d.dat", FileBenchmark.class.getSimpleName(), Util.generateId()));
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if (!file.delete()) {
                    Util.log(String.format("cannot delete file: %s", file.getAbsolutePath()));
                }
            }
        }));
        if (size > 0) {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                out.write(ByteArrayUtil.random(size));
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                IOUtil.close(out);
            }
        }
        return file;
    }

    @Benchmark
    public byte[] readWithStream() throws Exception {
        FileInputStream in = null;
        //BufferedInputStream buf = null;
        try {
            in = new FileInputStream(file);
            //buf = new BufferedInputStream(in);
            byte[] bytes = new byte[in.available()];
            if (in.read(bytes) == bytes.length) {
                return bytes;
            }
            throw new IOException("not complete");
        } finally {
            IOUtil.close(in);
        }
    }

    @Benchmark
    public byte[] readWithChannel() throws Exception {
        FileInputStream in = null;
        FileChannel ch = null;
        try {
            in = new FileInputStream(file);
            ch = in.getChannel();
            ByteBuffer bb = ByteBuffer.allocate((int) ch.size());
            if (ch.read(bb) == bb.capacity()) {
                return bb.array();
            }
            throw new IOException("not complete");
        } finally {
            IOUtil.close(ch, in);
        }
    }

    @Benchmark
    public byte[] readWithMappedByteBuffer() throws Exception {
        FileInputStream in = null;
        FileChannel ch = null;
        try {
            in = new FileInputStream(file);
            ch = in.getChannel();
            MappedByteBuffer bb = ch.map(FileChannel.MapMode.READ_ONLY, 0, ch.size());
            byte[] byteArray = new byte[(int) ch.size()];
            bb.get(byteArray);
            return byteArray;
        } finally {
            IOUtil.close(ch, in);
        }
    }

    @Benchmark
    public void writeWithStream() throws Exception {
        FileOutputStream out = null;
        //BufferedOutputStream buf = null;
        try {
            out = new FileOutputStream(getTestDataFile(0));
            //buf = new BufferedOutputStream(out);
            out.write(data);
            //buf.flush();
        } finally {
            IOUtil.close(out);
        }
    }

    @Benchmark
    public void writeWithChannel() throws Exception {
        FileOutputStream out = null;
        FileChannel ch = null;
        try {
            out = new FileOutputStream(getTestDataFile(0));
            ch = out.getChannel();
            ByteBuffer bb = ByteBuffer.wrap(data);
            while (bb.hasRemaining()) {
                ch.write(bb);
            }
            //ch.force(false);
        } finally {
            IOUtil.close(ch, out);
        }
    }

    @Benchmark
    public void writeWithMappedByteBuffer() throws Exception {
        RandomAccessFile out = null;
        FileChannel ch = null;
        try {
            out = new RandomAccessFile(getTestDataFile(0), "rw");
            ch = out.getChannel();
            MappedByteBuffer mbb = ch.map(FileChannel.MapMode.READ_WRITE, 0, data.length);
            mbb.put(data);
        } finally {
            IOUtil.close(ch, out);
        }
    }

}
