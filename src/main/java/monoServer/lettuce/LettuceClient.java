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
                  RedisClient redisClient = RedisClient.create("redis://password@localhost:6379/0");
                  lettuceClient.statefulRedisConnection = redisClient.connect();

                  redisReactiveCommands = statefulRedisConnection.reactive();
              }
          }
      }
      return lettuceClient;
  }

  public void get(String key, Consumer<String> completeConsumer, Consumer errorConsumer){
      redisReactiveCommands.get(key).subscribe(
             // value->{xxxx.tell(value);}
              completeConsumer,
              errorConsumer,
              ()->System.out.println("complete get"));
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
              ()->System.out.println("complete mGet"));
  }

}