package edu.udemy.redis.producer;

import edu.udemy.redis.dto.OrderDto;
import edu.udemy.redis.util.Constants;
import org.redisson.api.*;

import java.util.*;

public class OrderCollector {

    private static final int PUBLISH_PERIOD = 10000;

    private final OrderPublisher orderPublisher;

    public OrderCollector(RedissonClient redissonClient) {
        Timer timer = new Timer();
        orderPublisher = new OrderPublisher(redissonClient);
        timer.scheduleAtFixedRate(orderPublisher, 0, PUBLISH_PERIOD);
    }

    public void pushOrder(OrderDto orderDto) {
        orderPublisher.addOrder(orderDto);
    }

    private static class OrderPublisher extends TimerTask {

        private final RedissonClient redissonClient;

        private final Set<OrderDto> orders;

        private OrderPublisher(RedissonClient redissonClient) {
            this.redissonClient = redissonClient;
            orders = Collections.synchronizedSet(new HashSet<>());
        }

        @Override
        public void run() {
            if (orders.isEmpty()) {
                return;
            }
            HashSet<OrderDto> toPublish = new HashSet<>(orders);
            int batchSize = toPublish.size();
            System.out.println("Starting to publish " + batchSize + " orders");

            RBatch batch = redissonClient.createBatch();
            RScoredSortedSetAsync<OrderDto> scoredOrders = batch.getScoredSortedSet(Constants.ORDERS);
            toPublish.forEach(e -> scoredOrders.addAsync(e.getUserClass().getPriority(), e));

            BatchResult<?> result = batch.execute();
            if (Objects.equals(result.getResponses().size(), batchSize)) {
                System.out.println("Successfully pushed " + batchSize + " orders to Redis");
                orders.removeAll(toPublish);
                // Is it possible to publish sortedSet itself?
                RTopic topic = redissonClient.getTopic(Constants.TOPIC);
                topic.publish("New orders arrived");
            }
        }

        private void addOrder(OrderDto orderDto) {
            orders.add(orderDto);
        }
    }
}