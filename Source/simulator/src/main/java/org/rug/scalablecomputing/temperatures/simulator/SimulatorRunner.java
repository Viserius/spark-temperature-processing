package org.rug.scalablecomputing.temperatures.simulator;

import org.rug.scalablecomputing.temperatures.simulator.faker.FakerRunner;
import org.rug.scalablecomputing.temperatures.simulator.parser.ParserRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SimulatorRunner {

	@Value("${threads}")
	private int threads;

	@Value("${only_fake}")
	private boolean onlyFakeDatasets;

	@Autowired
	private ApplicationContext context;

	public void run() {
		// Parser for converting raw data -> structured data + sending them
		System.out.println("Only fake?:" + onlyFakeDatasets);
		System.out.println("Threads:" + threads);

		if(!onlyFakeDatasets) {
			new Thread(context.getBean(ParserRunner.class)).start();
			threads -= 1;
		}

		// Generate fake simulated data for stress testing
		for (int fakeRunnerI = 0; fakeRunnerI < threads; fakeRunnerI++) {
			new Thread(context.getBean(FakerRunner.class)).start();
		}

	}

}
