package org.lds;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Logger {

    private static class Log {
        private final Date time;
        private final int threadNumber;
        private final Object msg;

        private Log(Date time, int threadNumber, Object msg) {
            this.time = time;
            this.threadNumber = threadNumber;
            this.msg = msg;
        }
    }

    private static class LogRunnable implements Runnable {

        private final Logger logger;

        private LogRunnable(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void run() {
            Log log;
            while ((log = logQueue.poll()) != null) {
                StringWriter w = new StringWriter(4000);
                w.write(sdf.format(log.time));
                w.write('/');
                w.write(String.format("%08x", log.threadNumber));
                w.write(": ");
                if (log.msg instanceof Exception) {
                    Exception e = (Exception) log.msg;
                    e.printStackTrace(new PrintWriter(w));
                    logger.printError(w.toString());
                } else {
                    w.write(log.msg == null ? "<NULL>" : log.msg.toString());
                    logger.print(w.toString());
                }
            }
        }
    }

    private static class DefaultLogger {
        private static final Logger instance = newLogger();

        private static Logger newLogger() {
            String loggerClassName = System.getProperty(Logger.class.getName());
            if (Util.isNullOrWhiteSpace(loggerClassName)) {
                return new Logger() {
                    @Override
                    protected void print(String msg) {
                        System.out.println(msg);
                    }

                    @Override
                    protected void printError(String msg) {
                        System.err.println(msg);
                    }
                };
            }
            Class<?> cls = Must.forName(loggerClassName);
            return (Logger) Must.newInstance(cls);
        }
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final ConcurrentLinkedQueue<Log> logQueue = new ConcurrentLinkedQueue<Log>();
    private static Thread logThread;

    public static Logger getLogger() {
        return DefaultLogger.instance;
    }

    public void log(Object msg) {
        Log log = new Log(new Date(), Thread.currentThread().hashCode(), msg);
        logQueue.offer(log);
        if (logThread == null || logThread.getState() == Thread.State.TERMINATED) {
            synchronized (this) {
                if (logThread == null || logThread.getState() == Thread.State.TERMINATED) {
                    logThread = new Thread(new LogRunnable(this));
                    logThread.start();
                }
            }
        }
    }

    public void waitLog() throws InterruptedException {
        if (logThread != null) {
            logThread.join();
        }
    }

    protected abstract void print(String msg);

    protected abstract void printError(String msg);
}
