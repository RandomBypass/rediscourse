package edu.udemy.redis;

import edu.udemy.redis.config.RedisConfig;
import edu.udemy.redis.consumer.OrderProcessor;
import edu.udemy.redis.dto.OrderDto;
import edu.udemy.redis.producer.OrderCollector;
import edu.udemy.redis.util.UserClass;
import org.redisson.api.RedissonClient;

import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

import static edu.udemy.redis.util.Constants.TOPIC;

public class Application {

    private static final long DEFAULT_ELEMENTS = 3L;
    private static final int LOOPS = 4;

    public static void main(String[] args) throws InterruptedException {

        RedisConfig redisConfig = new RedisConfig();
        RedissonClient redissonClient = redisConfig.getRedissonClient();
        OrderCollector orderCollector = new OrderCollector(redissonClient);

        redissonClient.getTopic(TOPIC)
                .addListener(String.class, new OrderProcessor(redissonClient));

        AtomicLong loopCounter = new AtomicLong();

        while (loopCounter.get() < LOOPS) {
            long loopNumber = loopCounter.getAndIncrement();
            UserClass[] values = UserClass.values();
            for (int i = 0; i < values.length; i++) {
                UserClass userClass = values[i];
                int currentClassIndex = i;
                LongStream.rangeClosed(1, DEFAULT_ELEMENTS)
                        .map(j -> j + currentClassIndex * DEFAULT_ELEMENTS + loopNumber * DEFAULT_ELEMENTS * values.length)
                        .mapToObj(String::valueOf)
                        .map(j -> new OrderDto(j, userClass))
                        .forEach(orderCollector::pushOrder);
            }
            Thread.sleep(20000);
        }
    }
}