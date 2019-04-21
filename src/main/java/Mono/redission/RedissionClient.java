package Mono.redission;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedissionClient {
    private static RedissonClient redissonClient;

    public static RedissonClient getInstance(){
        if(redissonClient == null){
            synchronized (RedissionClient.class){
                if(redissonClient == null){
                    Config config = new Config();
                    config.useClusterServers()
                            .setScanInterval(2000) // cluster state scan interval in milliseconds
                            // use "rediss://" for SSL connection
                            .addNodeAddress("redis://127.0.0.1:7000", "redis://127.0.0.1:7001")
                            .addNodeAddress("redis://127.0.0.1:7002")
                            .setPassword("ijklk~df.oie,80@2");


                    redissonClient = Redisson.create(config);
                }
            }
        }
        return redissonClient;
    }
}
