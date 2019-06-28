package monoServer.service;

import akka.actor.ActorRef;
import com.sun.jndi.toolkit.url.Uri;
import monoServer.Request;
import monoServer.actors.AbstractAsyncHttpActor;
import monoServer.actors.BaseActor;
import monoServer.common.ActContext;
import monoServer.http.HttpAsynInovker;
import monoServer.request.HttpRequest;

import java.net.URI;

public class AsyncHttpActor extends AbstractAsyncHttpActor {




    @Override
    public HttpRequest execute(Object request) {

        return null;

    }

    @Override
    public Class<ActorRef> onCommandDone(ActContext request) {
        //异步http执行完以后的操作

        return null;
    }


}
