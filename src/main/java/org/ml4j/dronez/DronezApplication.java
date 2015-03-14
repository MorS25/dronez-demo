package org.ml4j.dronez;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.machinelearning4j.dronez.commands.CommandFactory;
import org.machinelearning4j.dronez.domain.Drone;
import org.machinelearning4j.dronez.tracking.AbstractWebCamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DronezApplication {

	
	@Autowired 
	private CommandFactory commandFactory;
	
	@Autowired
	private AbstractWebCamObserver droneObserver;
	
	@Autowired
	private Drone drone;
	
	@Autowired 
	private DronezFlightExecutor flightExecutor;

	public static void main(String[] args) {
		  SpringApplication app = new SpringApplication(DronezApplication.class); 
		  app.setShowBanner(false);
		  app.run(args);
	}

	@PostConstruct
	public void start() throws InterruptedException {
		// Load the command factory
		commandFactory.init();
		
		// Start observing the drone
		droneObserver.startObserving();
		
		// Execute demo flight
		flightExecutor.fly(new DemoFlight());
	
		
	}
	

	@PreDestroy
	public void end() {
		drone.executeLandingSequence();
	}
	

	

}
