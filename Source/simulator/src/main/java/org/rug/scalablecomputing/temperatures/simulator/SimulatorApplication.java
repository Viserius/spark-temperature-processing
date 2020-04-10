package org.rug.scalablecomputing.temperatures.simulator;

import org.rug.scalablecomputing.temperatures.simulator.faker.FakerRunner;
import org.rug.scalablecomputing.temperatures.simulator.parser.FileHandler;
import org.rug.scalablecomputing.temperatures.simulator.parser.ParserRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class SimulatorApplication {

	public static void main(String[] args) {
		// Service container bootstrapping
		ApplicationContext context =
				SpringApplication.run(SimulatorApplication.class, args);

		context.getBean(SimulatorRunner.class).run();
	}

}
