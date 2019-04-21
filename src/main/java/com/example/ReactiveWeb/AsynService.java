package com.example.ReactiveWeb;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Service
public class AsynService {

    final long processGroupCount = 20;
    AtomicLong reqeustIndex = new AtomicLong();
    public static final ActorSystem  system = ActorSystem.create("root");

    public void  sayHello(DeferredResult<String> deferredResult){

        long processGroupId =  reqeustIndex.addAndGet(1) % processGroupCount;

        BusinessProcess businessProcessKey = new BusinessProcess("demo", processGroupId, null, null);
        BusinessProcess businessProcess = businessProcessMap.get(businessProcessKey);
        if (businessProcess == null) {
            // 倒序组装依赖，责任链模式
            final ActorRef secondStepActor =
                    system.actorOf(SecondStep.props(), "secondStepActor" + processGroupId);
            final ActorRef firstStepActor =
                    system.actorOf(FirstStep.props(secondStepActor), "firstStepActor" + processGroupId);

            businessProcess = new BusinessProcess("demo", processGroupId, secondStepActor, firstStepActor);
            businessProcessMap.put(businessProcessKey, businessProcess);
        }
        businessProcess.firstStepActor.tell(deferredResult, ActorRef.noSender());

    }

    /**
     * 定义一组业务处理执行单元
     */
    static class BusinessProcess {
        final String business;
        // 组id
        final long processGroupId;
        final ActorRef secondStepActor;
        final ActorRef firstStepActor;

        public BusinessProcess(String business, long processGroupId, ActorRef secondStepActor, ActorRef firstStepActor) {
            this.business = business;
            this.processGroupId = processGroupId;
            this.secondStepActor = secondStepActor;
            this.firstStepActor = firstStepActor;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BusinessProcess that = (BusinessProcess) o;
            return processGroupId == that.processGroupId &&
                    Objects.equals(business, that.business);
        }

        @Override
        public int hashCode() {
            // fixme
            return Objects.hash(business, processGroupId);
        }
    }

    static Map<BusinessProcess, BusinessProcess> businessProcessMap = new ConcurrentHashMap<>();

}
