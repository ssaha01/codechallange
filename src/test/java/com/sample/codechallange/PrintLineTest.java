package com.sample.codechallange;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Created by Supriya Saha on 8/31/15.
 */
public class PrintLineTest {
    @Test
    public void testPrintLine() {
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            List<Future<Boolean>> futureList = new ArrayList<Future<Boolean>>(5);
            for (int i = 0; i < 5; i++) {
                futureList.add(executorService.submit(new PrintTask()));
            }
            executorService.shutdown();

            for (Future<Boolean> future: futureList) {
                try {
                    future.get();
                }
                catch (ExecutionException ex) {
                    ex.printStackTrace();
                }
            }

            HazelcastInstance hazelcastInstance = Hazelcast.getHazelcastInstanceByName(PrintLine.hazelcastInstanceName);
            assertNotNull("Hazelcast Instance is null!!!!!", hazelcastInstance);
            assertEquals(1, hazelcastInstance.getMap("default").get(PrintLine.PRINT_COUNT));
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        finally {
            Hazelcast.shutdownAll();
        }
    }

    private class PrintTask implements Callable<Boolean> {
        public Boolean call() {
            new PrintLine().printLine();
            return true;
        }
    }
}
