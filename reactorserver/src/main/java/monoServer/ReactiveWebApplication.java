package monoServer;

import akka.actor.ActorSystem;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class ReactiveWebApplication {

	public static String[] argsMain;
	public final static ActorSystem system = ActorSystem.create("root");;
	public static void main(String[] args) {
		argsMain = args;
		SpringApplication.run(ReactiveWebApplication.class, args);
	}

}
