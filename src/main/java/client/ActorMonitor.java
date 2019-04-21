package client;

import Mono.ReactiveWebApplication;
import akka.actor.ActorRef;

import java.util.Timer;
import java.util.TimerTask;

public class ActorMonitor {
    static Timer timer = new Timer();

    // 2核机器，如果xmx是gc时间要50ms左右以上了。。。

    public static MonitorEntityRecall monitor20ms(String taskName, String ext, final ActorRef actor) {
        MonitorEntityRecall monitorEntityRecall = new MonitorEntityRecall(taskName, ext, System.currentTimeMillis(), 20,actor);
        timer.schedule(monitorEntityRecall, 20);
        return monitorEntityRecall;
    }
    public static MonitorEntityRecall monitor50ms(String taskName, String ext, final ActorRef actor) {
        MonitorEntityRecall monitorEntityRecall = new MonitorEntityRecall(taskName, ext, System.currentTimeMillis(), 50,actor);
        timer.schedule(monitorEntityRecall, 50);
        return monitorEntityRecall;
    }

    public static class MonitorEntityRecall extends TimerTask {
        public final String taskName;
        public final String ext;
        public final long startTime;
        public final long delayTime;
        public long doneTime;
        public final ActorRef actor;


        public MonitorEntityRecall(String taskName, String ext, long startTime, long delayTime,final ActorRef actor) {
            this.ext = ext;
            this.taskName = taskName;
            this.startTime = startTime;
            this.delayTime = delayTime;
            this.actor = actor;
        }

        @Override
        public void run() {
            if (doneTime - startTime > delayTime) {
                System.out.println("task:" + taskName + " ext:"+ext+" block:" + delayTime + "ms");
                // todo 重启 actor
                if(actor!=null){
                   // ReactiveWebApplication.system.stop(actor);
                }
            }
        }
    }
}
