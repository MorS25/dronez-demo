# dronez-demo

[Drone](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/domain/Drone.java) is both a [``StateObserver<DroneState>``](https://github.com/ml4j/ml4j-mdp/blob/master/src/main/java/org/ml4j/mdp/StateObserver.java) and an [``ActionTaker<DroneAction>``](https://github.com/ml4j/ml4j-mdp/blob/master/src/main/java/org/ml4j/mdp/ActionTaker.java).

During a Flight, the [Drone](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/domain/Drone.java) is sent a series of high-level [``PolicyCommand<DroneState,P,DroneAction>``](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/commands/PolicyCommand.java)s such as [HoverCommand](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/commands/HoverCommand.java) or [TargetTrajectoryCommand](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/commands/TargetTrajectoryCommand.java) - these are created using an [AbstractCommandFactory](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/commands/AbstractCommandFactory.java) implementation.

Each [``PolicyCommand<DroneState,P,DroneAction>``](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/commands/PolicyCommand.java) specifies 

1. a [``PolicyStateMapper<DroneState,P>``](https://github.com/ml4j/ml4j-mdp/blob/master/src/main/java/org/ml4j/mdp/PolicyStateMapper.java) - a way to obtain a policy-specific state of type P from the latest observed DroneState, and 
2. a [``Policy<P,DroneAction>``](https://github.com/ml4j/ml4j-mdp/blob/master/src/main/java/org/ml4j/mdp/Policy.java) which should be executed
3. a number of iterations

When the Drone receives a [``PolicyCommand<DroneState,P,DroneAction>``](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/commands/PolicyCommand.java), it uses a [``PolicyExecutor<DroneState,P,DroneAction>``](https://github.com/ml4j/ml4j-mdp/blob/master/src/main/java/org/ml4j/mdp/PolicyExecutor.java) along with the encapsulated Policy and PolicyStateMapper to make decisions about which [DroneAction](https://github.com/ml4j/dronez-core/blob/master/src/main/java/org/ml4j/dronez/DroneAction) to take for each iteration, using the latest observed [DroneState](https://github.com/ml4j/dronez-core/blob/master/src/main/java/org/ml4j/dronez/DroneState.java).

The selected [DroneAction](https://github.com/ml4j/dronez-core/blob/master/src/main/java/org/ml4j/dronez/DroneAction.java)s are sent to the Drone to be sent on to the ARDrone.

The goals of this project are to:

1.  Learn reward-maximising ``Policy<TargetRelativeDroneStateWithRecentActions, DroneAction>`` implementations for the aim of minimising distance-to-target.
2.  Implement AbstractCommandFactory implementation to load these policies into appropriate commands.
3.  Demonstrate test Flights
