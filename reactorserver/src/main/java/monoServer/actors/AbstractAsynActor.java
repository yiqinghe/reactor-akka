package monoServer.actors;

import annotation.AysncActor;
import monoServer.listener.AsynCommandListener;

/**
 * 异步执行actor
 */
@AysncActor
public abstract class AbstractAsynActor extends BaseActor implements AsynCommandListener {

}
