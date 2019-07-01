package monoServer.common;


import akka.actor.ActorSystem;
import monoServer.actors.ActorTopoBuilder;
import monoServer.enums.ActorGroupIdEnum;

import java.util.concurrent.ConcurrentHashMap;

public class GlobalActorHolder {

    public static ConcurrentHashMap<ActorGroupIdEnum,ActorTopoBuilder.ActorTopo> holders = new ConcurrentHashMap<>();

    public static final ActorSystem system = ActorSystem.create("root");

}
