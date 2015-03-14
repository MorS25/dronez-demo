package org.ml4j.dronez;

import java.util.logging.Logger;

import org.machinelearning4j.dronez.commands.CommandFactory;
import org.machinelearning4j.dronez.commands.PolicyCommand;
import org.machinelearning4j.dronez.commands.TargetTrajectoryCommand;
import org.machinelearning4j.dronez.domain.Drone;
import org.machinelearning4j.dronez.tracking.AbstractWebCamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class DronezFlightExecutor {

	public static Logger logger = Logger.getLogger(DronezFlightExecutor.class.getName());

	
	@Autowired
	protected Drone drone;
	
	@Autowired
	protected CommandFactory commandFactory;
	
	@Autowired
	protected AbstractWebCamObserver webCamObserver;
	
	
	public void executeCommand(PolicyCommand<DroneState, ?, DroneAction> command)
	{
		drone.executeCommand(command);
	}

	public void executeTargetTrajectoryCommand(TargetTrajectoryCommand targetTrajectoryCommand)
	{
		webCamObserver.setTargetTrajectory(targetTrajectoryCommand.getTrajectory());
		drone.executeCommand(targetTrajectoryCommand);
		webCamObserver.setTargetTrajectory(null);
	}
	
	@Async
	public void fly(DronezFlight flight)
	{
		
		drone.executeLaunchSequence();
		
		DroneState droneState = webCamObserver.getCurrentState();

		while (droneState == null || droneState.getLeftRightPositionVelocity() == null) {
			try {
				Thread.sleep(1000);
				logger.info("Looking for drone");
			} catch (InterruptedException e) {
			}
			droneState = webCamObserver.getCurrentState();
		}
		logger.info("Drone aquired");

		
	
		logger.info("Executing Main Flight");
		
		drone.addStateActionSequenceListener(commandFactory);
		
		flight.fly(this,commandFactory);
		

		drone.executeLandingSequence();
	}
}
