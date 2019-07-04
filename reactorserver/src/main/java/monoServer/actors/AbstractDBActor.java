package monoServer.actors;

import akka.actor.Props;
import akka.japi.Creator;
import monoServer.annotation.AkkaMaper;
import monoServer.annotation.DBActor;
import monoServer.SpringContext;
import monoServer.common.ActContext;

import java.lang.reflect.Field;

@DBActor
public abstract class AbstractDBActor extends AbstractBlockActor{

    public AbstractDBActor(){

    }

    public static Props props(Class<? extends BaseActor> actorClass)  {
        return Props.create(actorClass, new Creator() {
            @Override
            public Object create() throws Exception {
                AbstractDBActor abstractDBActor = (AbstractDBActor) actorClass.newInstance();
                Field[] declaredFields = actorClass.getDeclaredFields();
                for (Field field:declaredFields) {
                    if(field.getAnnotation(AkkaMaper.class) != null){
                        field.setAccessible(true);
                        field.set(abstractDBActor,SpringContext.getBean(field.getType()
                        ));
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
                    Object dbObject = this.buildExecuteData(context);
                    Class<? extends BaseActor> actorRefClass = this.executeAndNext(context,dbObject);
                    dispatch(actorRefClass,context);
                })
                .build();
    }

}
