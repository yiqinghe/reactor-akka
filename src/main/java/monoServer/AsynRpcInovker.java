package monoServer;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AsynRpcInovker {
    // 单例，模拟io事件监听 ，定时器到期事件属于硬件终端触发
    private volatile Map<Integer, Timer> timers = new HashMap<>();
    // 如果是多个定时器的话，模拟有多个远程调用io线程
    private static int timerSize = 1;
    private static volatile int timerIndex;

    public static class Command {
        final public Request request;
        final public long startTime;

        final public CommandDoneListener commandDoneListener;

        public Command(Request request, long startTime, CommandDoneListener commandDoneListener) {
            this.request = request;
            this.startTime = startTime;
            this.commandDoneListener = commandDoneListener;
        }

        public interface CommandDoneListener {
            void recall(Request request);
        }
    }

    private static AsynRpcInovker asynRpcInovker;

    // 由timer线程 负责回调
    class TimerTaskRecall extends TimerTask {
        final Command command;

        public TimerTaskRecall(Command command) {
            this.command = command;
        }

        @Override
        public void run() {
            // 发起回调
            command.commandDoneListener.recall(command.request);
        }
    }

    public AsynRpcInovker asynCall(Command command) {
        // 模拟异步远程调用，立马返回
        // System.out.println("asynCall start");
        // 模拟io事件监听
        Timer timer = timers.get(Integer.valueOf(timerIndex % timerSize));
        if (timer == null) {
            timer = new Timer();
            timers.put(Integer.valueOf(timerIndex), timer);
        }
        timerIndex++;
        long delay = 1+Double.valueOf(Math.random() * 50).longValue();
        timer.schedule(new TimerTaskRecall(command), delay);
        // System.out.println("asynCall done");


        return asynRpcInovker;
    }


    public static AsynRpcInovker getInstance() {
        if (asynRpcInovker == null) {
            synchronized (AsynRpcInovker.class) {
                if (asynRpcInovker == null) {
                    asynRpcInovker = new AsynRpcInovker();

                }
            }
        }
        return asynRpcInovker;
    }

}
