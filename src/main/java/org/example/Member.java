package org.example;

import com.hazelcast.collection.IQueue;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
public class Member {
    public static void main(String[] args) throws InterruptedException {
        final int LOOP_COUNT = 100000;
        final int MAX_ADD_COUNT = 1000;

        HazelcastInstance hz = Hazelcast.newHazelcastInstance();
        Random random = new Random();

        for (int i = 0; i < LOOP_COUNT; i ++) {
            int addCount = random.nextInt(MAX_ADD_COUNT);
            List<String> objects = hz.getDistributedObjects().stream().filter(x -> x instanceof IQueue)
                    .map(DistributedObject::getName).collect(Collectors.toList());
            if (objects.size() > 0 && Integer.parseInt(objects.get(0)) >= i) {
                addCount = 0;
            }

            String queueName = String.valueOf(i);

            IQueue<Integer> q = hz.getQueue(queueName);
            log.info("{} is Created.", queueName);

            log.info("-- Adding " + addCount + " items.");
            for (int j = 0; j < addCount; j ++) {
                q.add(j);
                Thread.sleep(10);
            }

            q.clear();
            log.info("{} is cleared.", queueName);

            q.destroy();
            log.info("{} is destroyed.", queueName);

            log.info("Distributed objects: {}", hz.getDistributedObjects().toString());
        }
    }
}
