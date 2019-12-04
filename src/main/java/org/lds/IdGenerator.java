package org.lds;

public class IdGenerator {
    private static final int nodeIdBitLen = 10;
    private static final int sequenceIdBitLen = 12;
    private static final long epoch = 1477958400000L;

    private final long nodeId;
    private long timestamp;
    private long sequenceId;

    public IdGenerator(long nodeId) {
        this.nodeId = nodeId & ((1 << nodeIdBitLen) - 1);
    }

    public long generateId() {
        long timestamp, sequenceId;
        synchronized (this) {
            timestamp = System.currentTimeMillis();
            if (timestamp != this.timestamp) {
                sequenceId = 0;
            } else {
                sequenceId = this.sequenceId + 1;
                if (sequenceId >> sequenceIdBitLen > 0) {
                    Must.sleep(1);
                    timestamp++;
                    sequenceId = 0;
                }
            }
            this.timestamp = timestamp;
            this.sequenceId = sequenceId;
        }
        long id = timestamp - epoch;
        id <<= nodeIdBitLen;
        id |= nodeId;
        id <<= sequenceIdBitLen;
        id |= sequenceId;
        return id;
    }

}
