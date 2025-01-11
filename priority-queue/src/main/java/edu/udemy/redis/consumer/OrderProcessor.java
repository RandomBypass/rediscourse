package edu.udemy.redis.consumer;

import edu.udemy.redis.dto.OrderDto;
import edu.udemy.redis.util.Constants;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;

public class OrderProcessor implements MessageListener<String> {

    private final RedissonClient redissonClient;

    public OrderProcessor(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void onMessage(CharSequence channel, String msg) {

        System.out.println("Got notification " + msg + " from " + channel);

        RScoredSortedSet<OrderDto> scoredSortedSet = redissonClient.getScoredSortedSet(Constants.ORDERS);

        OrderDto currentElement = scoredSortedSet.pollFirst();
        while (currentElement != null) {
            System.out.println("Processing order " + currentElement);
            currentElement = scoredSortedSet.pollFirst();
        }
    }
}