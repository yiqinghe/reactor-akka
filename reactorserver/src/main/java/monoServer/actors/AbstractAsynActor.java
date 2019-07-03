package monoServer.actors;

import annotation.AysncActor;
import monoServer.common.ActContext;
import monoServer.listener.AsynCommandListener;

/**
 * 异步执行actor
 */
@AysncActor
public abstract class AbstractAsynActor extends BaseActor implements AsynCommandListener {
    /**
     * 异步请求才有的error回调
     * 如果不需要执行，直接返回null，系统自动将context.exception返回。
     * 如果context.responseData为空,组装默认返回数据
     * @param context
     * @param exception
     * @return
     */
    public abstract Class<? extends BaseActor> errorAndNext(ActContext context,Throwable exception);

}
