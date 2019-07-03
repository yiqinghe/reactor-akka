package monoServer.actors;

import akka.actor.Props;
import akka.japi.Creator;
import annotation.AkkaMaper;
import monoServer.command.HttpCommand;
import monoServer.common.ActContext;

import java.lang.reflect.Field;

/**
 * 同步执行actor
 */
public abstract class AbstractBlockActor extends BaseActor{

}
