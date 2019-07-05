package monoServer.actors;

import monoServer.common.ActContext;

/**
 * 通用执行单元，一般处理入口业务、或者整合逻辑，以及决定下一个执行器
 */
public abstract class AbstractCommonActor extends BaseActor {

    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .match(ActContext.class, context -> {
                    Object firstObject = this.buildExecuteData(context);
                    Class<? extends BaseActor> actorRefClass = this.executeNextOnSuccess(context,firstObject);
                    dispatch(actorRefClass,context);
                })
                .build();
    }

}
