package org.ml4j.dronez;

import java.util.logging.Logger;

import org.machinelearning4j.dronez.commands.CommandFactory;

public class DemoFlight implements DronezFlight {

	
	public static Logger logger = Logger.getLogger(DemoFlight.class.getName());
	
	public static DroneState HOVER_POSITION = new DroneState(new PositionVelocity(0, 0), new PositionVelocity(0.3, 0),
			new PositionVelocity(2.5, 0), new PositionVelocity(0, 0));
	

	@Override
	public void fly(DronezFlightExecutor flightExecutor,CommandFactory commandFactory) {
	
		logger.info("No-op for 25 iterations");
		flightExecutor.executeCommand(commandFactory.createNoOpCommand(25));
		
		
		for (int i = 0; i < 20; i ++)
		{
			logger.info("Hovering round hover position for 50 iterations");

			
			flightExecutor.executeTargetTrajectoryCommand(commandFactory.createHoverCommand(HOVER_POSITION, 50));
			
			// As policies are static, this only saves the state action sequence history
			commandFactory.updatePolicies();
		}
		
		logger.info("No-op for 25 iterations");
		
		flightExecutor.executeCommand(commandFactory.createNoOpCommand(25));
	}

}
