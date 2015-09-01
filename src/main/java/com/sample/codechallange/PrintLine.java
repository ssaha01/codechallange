package com.sample.codechallange;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;


/**
 * Created by Supriya Saha on 8/31/15.
 */
public class PrintLine {
    protected static final String hazelcastInstanceName = "test";

    protected static final String PRINT_COUNT = "print_count";

    private static final HazelcastInstance hazelcastInstance;

    private static final Map<String,Integer> defaultMap;

    private static final Lock lock;
    static {
        Config config = new Config();
        config.setInstanceName(hazelcastInstanceName);
        NetworkConfig network = config.getNetworkConfig();

        JoinConfig join = network.getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().addMember("localhost").setEnabled(true);
        network.getInterfaces().setEnabled(false);
        hazelcastInstance = Hazelcast.getOrCreateHazelcastInstance(config);
        defaultMap = hazelcastInstance.getMap("default");
        lock = hazelcastInstance.getLock("myLock");
    }

    /**
     * This function prints 'Hello World!!!!!' only once even if the function is invoked multiple times from same or multiple jvms.
     * Since Hazelcast is a in memory distributed cache, accessible from different jvms, once the text is printed once and the Hazelcast cache is updated, other invocations
     * of the function does not print again
     *
     */
    public void printLine() {
        try {
            if (lock.tryLock(10000, TimeUnit.MILLISECONDS)) {
                try {
                    int cnt = defaultMap.getOrDefault(PRINT_COUNT,0);
                    System.out.println("============================================");
                    System.out.println("Print Count: "+cnt);
                    if (cnt == 0) {
                        System.out.println("Hello World!!!!!");
                        defaultMap.put(PRINT_COUNT, ++cnt);
                    }
                    System.out.println("============================================");
                } finally {
                    lock.unlock();
                }
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
