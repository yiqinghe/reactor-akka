package monoServer.common;


import akka.actor.ActorSystem;
import monoServer.actors.topo.ActorTopo;
import monoServer.enums.ActorGroupIdEnum;

import java.util.concurrent.ConcurrentHashMap;

public class GlobalActorHolder {

    public static volatile ConcurrentHashMap<ActorGroupIdEnum,ActorTopo> holders = new ConcurrentHashMap<>();

    public static final ActorSystem system = ActorSystem.create("root");

}
