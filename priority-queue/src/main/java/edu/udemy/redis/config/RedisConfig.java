package edu.udemy.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

public class RedisConfig {

    private static class RedissonClientHolder {

        private static final RedissonClient REDISSON_CLIENT = createClient();

        private static RedissonClient createClient() {

            Config config = new Config();
            config.useSingleServer()
                    .setAddress("redis://127.0.0.1:6379");
            ObjectMapper objectMapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule());
            config.setCodec(new JsonJacksonCodec(objectMapper));

            return Redisson.create(config);
        }
    }

    // Should client be singleton or is it OK to create separate for each class?
    public RedissonClient getRedissonClient() {
        return RedissonClientHolder.REDISSON_CLIENT;
    }
}