package monoServer.lettuce;

import com.alibaba.fastjson.JSON;
import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.netty.util.concurrent.Promise;
import monoServer.actors.AbstractLettcueRedisActor;
import monoServer.actors.BaseActor;
import monoServer.command.RedisCommand;
import monoServer.common.ActContext;
import monoServer.enums.RedisCommandEnum;
import reactor.core.publisher.Flux;
import scala.collection.immutable.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class LettuceClient {
    private static LettuceClient lettuceClient;
    private static StatefulRedisConnection<String, String> statefulRedisConnection;
    private static RedisReactiveCommands<String,String> redisReactiveCommands;

    public static LettuceClient getInstance(){
        Promise TaskPromise
      if (lettuceClient==null){
          synchronized (LettuceClient.class){
              if (lettuceClient==null){
                  lettuceClient = new LettuceClient();
                  RedisClient redisClient = RedisClient.create("redis://@localhost:6379");
                  lettuceClient.statefulRedisConnection = redisClient.connect();

                  redisReactiveCommands = statefulRedisConnection.reactive();
              }
          }
      }
      return lettuceClient;
  }

    public void excuteComand(RedisCommand redisCommand, ActContext context, AbstractLettcueRedisActor actor){
        switch (redisCommand.getCommandType()){
            case GET:
                LettuceClient.getInstance().get(redisCommand.getKey(),list ->{
                    actor.onSuccess(context,list.size() ==0 ? null:list.get(0));
                },error ->{
                    actor.onFail(context,new Throwable(JSON.toJSONString(error)));
                });
                break;
            case SET:
                LettuceClient.getInstance().set(redisCommand.getKey(),redisCommand.getValue(),list ->{
                    actor.onSuccess(context,list.size() ==0 ? null:list.get(0));
                },error ->{
                    actor.onFail(context,new Throwable(JSON.toJSONString(error)));
                });
                break;
            case HGET:
                LettuceClient.getInstance().hget(redisCommand.getKey(),redisCommand.getField(),list ->{
                    actor.onSuccess(context,list.size() ==0 ? null:list.get(0));
                },error ->{
                    actor.onFail(context,new Throwable(JSON.toJSONString(error)));
                });
                break;
            case HGETALL:
                LettuceClient.getInstance().hgetAll(redisCommand.getKey(),list ->{
                    actor.onSuccess(context,list.size() ==0 ? null:list);
                },error ->{
                    actor.onFail(context,new Throwable(JSON.toJSONString(error)));
                });
                break;
            case HSET:
                LettuceClient.getInstance().hset(redisCommand.getKey(),redisCommand.getField(),redisCommand.getValue(),list ->{
                    actor.onSuccess(context,list.size() ==0 ? null:list.get(0));
                },error ->{
                    actor.onFail(context,new Throwable(JSON.toJSONString(error)));
                });
                break;
            case MGET:
                LettuceClient.getInstance().mget(list ->{
                    actor.onSuccess(context,list.size() ==0 ? null:list);
                },error ->{
                    actor.onFail(context,new Throwable(JSON.toJSONString(error)));
                },redisCommand.getKeys());
                break;
            case MSET:
                break;
            case GETBIT:
                LettuceClient.getInstance().getBit(redisCommand.getKey(),redisCommand.getOffset(),list ->{
                    actor.onSuccess(context,list.size() ==0 ? null:list.get(0));
                },error ->{
                    actor.onFail(context,new Throwable(JSON.toJSONString(error)));
                });
                break;
            case SETBIT:
                LettuceClient.getInstance().setBit(redisCommand.getKey(),redisCommand.getOffset(),redisCommand.isFalg(),list ->{
                    actor.onSuccess(context,list.size() ==0 ? null:list.get(0));
                },error ->{
                    actor.onFail(context,new Throwable(JSON.toJSONString(error)));
                });
                break;
        }
    }




  public void get(String key, Consumer<List<String>> completeConsumer, Consumer errorConsumer){
      Flux.just(key).flatMap(tempKey -> redisReactiveCommands.get(tempKey)).collectList().subscribe(
              completeConsumer,
              errorConsumer,
              ()->System.out.println("redis complete get"));
  }

    public void set(String key,String value,Consumer<List<String>> completeConsumer, Consumer errorConsumer){
        Flux.just(key).flatMap(tempkey -> redisReactiveCommands.set(tempkey,value)).collectList().subscribe(
                completeConsumer,
                errorConsumer,
                ()->System.out.println("redis complete mGet"));
    }

    /**
     * 批量获取key
     * @param completeConsumer
     * @param errorConsumer
     * @param keys
     */
  public void mget(Consumer<List<KeyValue<String,String>>> completeConsumer, Consumer errorConsumer, String... keys){
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


    public void hget(String key,String field, Consumer<List<String>> completeConsumer, Consumer errorConsumer){
        Flux.just(key).flatMap(tempKey -> redisReactiveCommands.hget(tempKey,field)).collectList().subscribe(
                completeConsumer,
                errorConsumer,
                ()->System.out.println("redis complete hget"));
    }

    public void hgetAll(String key, Consumer<List<Map<String,String>>> completeConsumer, Consumer errorConsumer){
        Flux.just(key).flatMap(tempKey -> redisReactiveCommands.hgetall(tempKey)).collectList().subscribe(
                completeConsumer,
                errorConsumer,
                ()->System.out.println("redis complete hgetall"));
    }

    public void hset(String key,String filed,String value, Consumer<List<Boolean>> completeConsumer, Consumer errorConsumer){
        Flux.just(key).flatMap(tempKey -> redisReactiveCommands.hset(tempKey,filed,value)).collectList().subscribe(
                completeConsumer,
                errorConsumer,
                ()->System.out.println("redis complete hset"));
    }

    public void getBit(String key,long offset,Consumer<List<Long>> completeConsumer, Consumer errorConsumer){
        Flux.just(key).flatMap(tempKey -> redisReactiveCommands.getbit(tempKey,offset)).collectList().subscribe(
                completeConsumer,
                errorConsumer,
                ()->System.out.println("redis complete hset"));
    }

    public void setBit(String key,long offset,boolean flag, Consumer<List<Long>> completeConsumer, Consumer errorConsumer){
        Flux.just(key).flatMap(tempKey ->{
                    int value = 0;
                    if(flag){
                        value = 1;
                    }
                  return   redisReactiveCommands.setbit(tempKey,offset,value);
        }).collectList().subscribe(
                completeConsumer,
                errorConsumer,
                ()->System.out.println("redis complete hset"));
    }

}
