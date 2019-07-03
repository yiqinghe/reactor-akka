package monoServer.actors;

import akka.actor.Props;
import akka.japi.Creator;
import annotation.AkkaMaper;
import annotation.BlockActor;
import monoServer.command.HttpCommand;
import monoServer.common.ActContext;

import java.lang.reflect.Field;

/**
 * 同步执行actor
 */
@BlockActor
public abstract class AbstractBlockActor extends BaseActor{

}
