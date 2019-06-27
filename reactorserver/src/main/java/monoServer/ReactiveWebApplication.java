package monoServer;

import akka.actor.ActorSystem;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReactiveWebApplication {

	public static String[] argsMain;
	public final static ActorSystem system = ActorSystem.create("root");;
	public static void main(String[] args) {
		argsMain = args;
		SpringApplication.run(ReactiveWebApplication.class, args);
	}

}
