package Mono;

import akka.actor.ActorSystem;
import client.Main;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReactiveWebApplication {

	public static String[] argsMain;
	public final static ActorSystem system = ActorSystem.create("root");;
	public static void main(String[] args) {
		argsMain = args;
		// 初始化实例，连接池，避免使用的时候才创建导致的创建超时？
		client.AsynRpcInovker.getInstance();
		SpringApplication.run(ReactiveWebApplication.class, args);
	}

}
