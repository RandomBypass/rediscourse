package edu.udemy.redis.producer;

import edu.udemy.redis.config.RedisConfig;
import edu.udemy.redis.dto.OrderDto;
import edu.udemy.redis.util.Constants;
import edu.udemy.redis.util.UserClass;
import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.LongStream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderCollectorTest {

    private static final String REDIS_SERV_CONFIG = "C:/Users/joh/IdeaProjects/redis/priority-queue/src/test/resources/test-compose.yml";
    private static final long ELEMENTS_SIZE = 3L;

    @ClassRule
    public static DockerComposeContainer REDIS_SERVER = new DockerComposeContainer(new File(REDIS_SERV_CONFIG))
            .withExposedService("redis", 6379);

    protected final RedisConfig redisConfig = new RedisConfig();

    private OrderCollector orderCollector;
    private RedissonClient redissonClient;

    @BeforeAll
    public void setUp() {
        REDIS_SERVER.start();
        redissonClient = redisConfig.getRedissonClient();
        orderCollector = new OrderCollector(redissonClient);
    }

    @Test
    void ensureOrdering() throws InterruptedException {
        UserClass[] values = UserClass.values();
        for (int i = 0; i < values.length; i++) {
            UserClass userClass = values[i];
            int currentClassIndex = i;
            LongStream.rangeClosed(1, 3L)
                    .map(j -> j + currentClassIndex * ELEMENTS_SIZE + ELEMENTS_SIZE * values.length)
                    .mapToObj(String::valueOf)
                    .map(j -> new OrderDto(j, userClass))
                    .forEach(orderCollector::pushOrder);
        }
        Thread.sleep(20000);

        RScoredSortedSet<OrderDto> orders = redissonClient.getScoredSortedSet(Constants.ORDERS);
        Arrays.stream(values)
                .sorted(Comparator.comparing(UserClass::getPriority))
                .forEachOrdered(userClass -> {
                    for (int i = 0; i < ELEMENTS_SIZE; i++) {
                        Assertions.assertEquals(orders.pollFirst().getUserClass(), userClass);
                    }
                });
    }

    @AfterAll
    public void shutdown() {
        redissonClient.shutdown();
        REDIS_SERVER.stop();
    }
}