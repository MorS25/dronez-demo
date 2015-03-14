package org.ml4j.dronez;

import org.machinelearning4j.dronez.commands.CommandFactory;
import org.machinelearning4j.dronez.commands.LearnedContinuousInnerPolicyCommandFactoryImpl;
import org.machinelearning4j.dronez.domain.Drone;
import org.machinelearning4j.dronez.domain.ODCDrone;
import org.machinelearning4j.dronez.tracking.WebCamObserver;
import org.ml4j.dronez.policy.learning.PolicyLearner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("ardrone")
public class DronezConfig {

	@Value("${policy.recentActionCount}")
	private int policyRecentActionCount;
	
	/**
	 * Create a StateActionController<DroneState, DroneAction> for the Drone
	 * 
	 * This acts as both StateObserver<DroneState> and ActionTaker<DroneAction>
	 * 
	 * Delegates to droneObserver() for state observation
	 * 
	 * @return the Drone
	 */
	@Bean
	public Drone drone() {
		// Return wrapper around Open Drone Control library
		return new ODCDrone(droneObserver());

	}
	
	/**
	 * Observe the drone using a webcam
	 * 
	 * @return the drone observer
	 */
	@Bean 
	public WebCamObserver droneObserver() {
		return new WebCamObserver();
	}

	
	
	/**
	 * Generates the commands we use to control drone flight
	 * 
	 * @return a CommandFactory implementation
	 */
	@Bean
	public CommandFactory commandFactory()
	{
		return LearnedContinuousInnerPolicyCommandFactoryImpl.create(
				PolicyLearner.class.getClassLoader(), "org/ml4j/dronez/policies",policyRecentActionCount);
	}

	
	/**
	 * Uses the commandFactory() and drone() beans
	 * to execute a flight, and updates the droneObserver()
	 * with any target trajectories for display purposes
	 * 
	 * @return
	 */
	@Bean
	public DronezFlightExecutor flightExecutor()
	{
		return new DronezFlightExecutor();
	}
	

}
