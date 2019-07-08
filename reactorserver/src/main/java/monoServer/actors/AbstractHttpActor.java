package monoServer.actors;

import monoServer.common.ActContext;
import monoServer.http.Command;
import monoServer.http.HttpAsyncClient;
import monoServer.request.HttpRequest;

public abstract class AbstractHttpActor extends AbstractAsynActor {


    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .match(ActContext.class, context -> {
                    HttpRequest request = this.buildExecuteData(context);
                    //多次http调用 只会纪录上一次的信息
                    Command clientCommand = new Command(context
                            ,System.currentTimeMillis(),this,request);
                    HttpAsyncClient.getInstance().asynCall(clientCommand);
                })
                .build();
    }

    /**
     * 需要实现要发起请求的url，body信息等
     * @param context
     * @return
     */
    public abstract HttpRequest buildExecuteData(ActContext context);

    @Override
    public void onSuccess(ActContext context, Object httpresult) {
        Class<? extends BaseActor> actorRefClass = executeNextOnSuccess(context,httpresult);
        dispatch(actorRefClass,context);
    }

    @Override
    public void onFail(ActContext context, Throwable exception) {
        Class<? extends BaseActor> aClass = executeNextOnError(context, exception);
        dispatch(aClass,context);
    }
}
