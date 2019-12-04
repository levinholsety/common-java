package org.lds.test;

import org.junit.Assert;
import org.junit.Test;
import org.lds.Util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class IdGeneratorTest {
    @Test
    public void testGenerateId() throws Exception {
        final int iterationCount = 100;
        int threadCount = 100;
        int expectedIdCount = iterationCount * threadCount;
        final Set<Long> idSet = Collections.synchronizedSet(new HashSet<Long>(expectedIdCount));
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < iterationCount; i++) {
                        idSet.add(Util.generateId());
                    }
                }
            });
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        Assert.assertEquals(expectedIdCount, idSet.size());
    }
}
