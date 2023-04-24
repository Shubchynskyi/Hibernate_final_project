package redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import lombok.extern.slf4j.Slf4j;
import util.Constants;

import java.util.List;

@Slf4j
public class RedisService {
    public final RedisClient client;
    public final ObjectMapper mapper;

    public RedisService() {
        this.client = prepareRedisClient();
        this.mapper = new ObjectMapper();
    }

    private RedisClient prepareRedisClient() {
        RedisClient redisClient = RedisClient.create(RedisURI.create(Constants.REDIS_HOST, Constants.REDIS_PORT));
        redisClient.connect();
        log.info(Constants.START_REDIS_CLIENT);
        return redisClient;
    }

    public <T extends RedisEntity> void pushToRedis(List<T> data) {
        try (StatefulRedisConnection<String, String> connection = client.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (var var : data) {
                try {
                    sync.set(String.valueOf(var.getId()), mapper.writeValueAsString(var));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    log.error(Constants.ERROR_SENDING_TO_REDIS);
                }
            }
        }
        log.info(Constants.SENDING_TO_REDIS_WAS_SUCCESSFUL);
    }

    public void shutdown() {
        client.shutdown();
        log.info(Constants.REDIS_CLIENT_SHUTDOWN_WAS_SUCCESSFUL);
    }
}
