package monoServer.actors;

import akka.actor.Props;
import akka.japi.Creator;
import annotation.AkkaMaper;
import com.netflix.ribbon.proxy.annotation.Http;
import dao.UserDao;
import monoServer.SpringContext;
import monoServer.actors.BaseActor;
import monoServer.command.HttpCommand;
import monoServer.common.ActContext;

import java.lang.reflect.Field;

public abstract class AbstractDBActor extends AbstractBlockActor{

    //#greeter-messages
    public static Props props(Class<? extends BaseActor> actorClass) throws IllegalAccessException, InstantiationException {
        return Props.create(actorClass, new Creator<AbstractDBActor>() {
            @Override
            public AbstractDBActor create() throws Exception {
                AbstractDBActor abstractDBActor = (AbstractDBActor) actorClass.newInstance();
                Field[] declaredFields = actorClass.getDeclaredFields();
                for (Field field:declaredFields) {
                    if(field.getAnnotation(AkkaMaper.class) != null){

                    }
                }
                return abstractDBActor;
            }
        });
    }


    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .match(ActContext.class, context -> {
                    Class<? extends BaseActor> actorRefClass = this.execute(context);
                    dispatch(actorRefClass,context);
                })
                .build();
    }

    public abstract Class<? extends BaseActor> execute(ActContext context);

}
