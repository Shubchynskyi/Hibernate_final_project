package redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;

import java.util.List;

public class RedisService {

    public final RedisClient client;
    public final ObjectMapper mapper;

    public RedisService() {
        this.client = prepareRedisClient();
        this.mapper = new ObjectMapper();
    }

    private RedisClient prepareRedisClient() {
        RedisClient redisClient = RedisClient.create(RedisURI.create("localhost", 6379));
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            System.out.println("\nConnected to Redis\n");
        }
        return redisClient;
    }

    // TODO generic
    public <T extends RedisEntity> void pushToRedis(List<T> data) {
        try (StatefulRedisConnection<String, String> connection = client.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (var var : data) {
                try {
                    sync.set(String.valueOf(var.getId()), mapper.writeValueAsString(var));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
