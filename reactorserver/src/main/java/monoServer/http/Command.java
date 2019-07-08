package monoServer.http;

import monoServer.common.ActContext;
import monoServer.listener.AsynCommandListener;
import org.springframework.cloud.client.ServiceInstance;

public class Command {

    public ActContext context;
    final public long startTime;
    public ServiceInstance serviceInstance;
    public final monoServer.request.HttpRequest httpRequest;



    final public AsynCommandListener asynCommandListener;

    public Command(ActContext context, long startTime, AsynCommandListener asynCommandListener
            ,monoServer.request.HttpRequest httpRequest) {
        this.context = context;
        this.startTime = startTime;
        this.asynCommandListener = asynCommandListener;
        this.httpRequest = httpRequest;
    }



}
