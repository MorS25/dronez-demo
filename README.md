# dronez-demo

Drone is both a [StateObserver<DroneState>](https://github.com/ml4j/ml4j-mdp/blob/master/src/main/java/org/ml4j/mdp/StateObserver.java) and an [ActionTaker<DroneAction>](https://github.com/ml4j/ml4j-mdp/blob/master/src/main/java/org/ml4j/mdp/ActionTaker.java), and so can obtain observed DroneState and can send DroneActions to the ARDrone

During a Flight, the [Drone](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/domain/Drone.java) is sent a series of high-level [PolicyCommand<DroneState,P,DroneAction>](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/commands/PolicyCommand.java)s such as [HoverCommand](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/commands/HoverCommand.java) or [TargetTrajectoryCommand](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/commands/TargetTrajectoryCommand.java) - these are created using an [AbstractCommandFactory](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/commands/AbstractCommandFactory.java) implementation.

Each [PolicyCommand<DroneState,P,DroneAction>](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/commands/PolicyCommand.java) specifies 

a PolicyStateMapper<DroneState,P> - a way to obtain a policy-specific state of type P from the latest observed DroneState, and 
a Policy<P,DroneAction> which should be executed for a number of iterations.

When the Drone receives a [PolicyCommand<DroneState,P,DroneAction>](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/commands/PolicyCommand.java), it uses a [PolicyExecutor<DroneState,P,DroneAction>](https://github.com/ml4j/ml4j-mdp/blob/master/src/main/java/org/ml4j/mdp/PolicyExecutor.java) along with the encapsulated [Policy<P,DroneAction>](https://github.com/ml4j/ml4j-mdp/blob/master/src/main/java/org/ml4j/mdp/Policy.java) and [PolicyStateMapper<DroneState,P>](https://github.com/ml4j/ml4j-mdp/blob/master/src/main/java/org/ml4j/mdp/PolicyStateMapper.java)  to make decisions about which [DroneAction](https://github.com/ml4j/dronez-core/blob/master/src/main/java/org/ml4j/dronez/DroneAction) to take for each iteration, using the latest observed [DroneState](https://github.com/ml4j/dronez-core/blob/master/src/main/java/org/ml4j/dronez/DroneState.java).

The selected [DroneAction](https://github.com/ml4j/dronez-core/blob/master/src/main/java/org/ml4j/dronez/DroneAction.java)s are sent to the Drone to be sent on to the ARDrone.
