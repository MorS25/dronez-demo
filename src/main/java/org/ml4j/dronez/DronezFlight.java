package org.ml4j.dronez;

import org.machinelearning4j.dronez.commands.CommandFactory;

public interface DronezFlight {
	
	public void fly(DronezFlightExecutor flightExecutor,CommandFactory commandFactory);
}
