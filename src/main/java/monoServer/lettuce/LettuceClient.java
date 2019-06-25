package monoServer.lettuce;

import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Consumer;

public class LettuceClient {
    private static LettuceClient lettuceClient;
    private static StatefulRedisConnection<String, String> statefulRedisConnection;
    private static RedisReactiveCommands<String,String> redisReactiveCommands;

    public static LettuceClient getInstance(){
      if (lettuceClient==null){
          synchronized (LettuceClient.class){
              if (lettuceClient==null){
                  lettuceClient = new LettuceClient();
                  RedisClient redisClient = RedisClient.create("redis://localhost:6379");
                  lettuceClient.statefulRedisConnection = redisClient.connect();

                  redisReactiveCommands = statefulRedisConnection.reactive();
              }
          }
      }
      return lettuceClient;
  }

  public void get(String key, Consumer<List<String>> completeConsumer, Consumer errorConsumer){
      Flux.just(key).flatMap(tempKey -> redisReactiveCommands.get(tempKey)).collectList().subscribe(
              completeConsumer,
              errorConsumer,
              ()->System.out.println("redis complete get"));
  }

    /**
     * 批量获取key
     * @param completeConsumer
     * @param errorConsumer
     * @param keys
     */
  public void mGet(Consumer<List<KeyValue<String,String>>> completeConsumer, Consumer errorConsumer, String... keys){
      Flux.just(keys).flatMap(key -> redisReactiveCommands.mget(key)).collectList().subscribe(
        //              list->{
        //          for(KeyValue<String,String> keyValue:list){
        //              System.out.println("keyValue:"+keyValue);
        //          }
        //      }
              completeConsumer,
              errorConsumer,
              ()->System.out.println("redis complete mGet"));
  }

}
