# dronez-demo

[Drone](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/domain/Drone.java) is both a [``StateObserver<DroneState>``](https://github.com/ml4j/ml4j-mdp/blob/master/src/main/java/org/ml4j/mdp/StateObserver.java) and an [``ActionTaker<DroneAction>``](https://github.com/ml4j/ml4j-mdp/blob/master/src/main/java/org/ml4j/mdp/ActionTaker.java).

During a Flight, the [Drone](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/domain/Drone.java) is sent a series of high-level [``PolicyCommand<DroneState,P,DroneAction>``](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/commands/PolicyCommand.java)s such as [HoverCommand](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/commands/HoverCommand.java) or [TargetTrajectoryCommand](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/commands/TargetTrajectoryCommand.java) - these are created using an [AbstractCommandFactory](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/commands/AbstractCommandFactory.java) implementation.

Each [``PolicyCommand<DroneState,P,DroneAction>``](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/commands/PolicyCommand.java) specifies 

1. a [``PolicyStateMapper<DroneState,P>``](https://github.com/ml4j/ml4j-mdp/blob/master/src/main/java/org/ml4j/mdp/PolicyStateMapper.java) - a way to obtain a policy-specific state of type P from the latest observed DroneState, and 
2. a [``Policy<P,DroneAction>``](https://github.com/ml4j/ml4j-mdp/blob/master/src/main/java/org/ml4j/mdp/Policy.java) which should be executed
3. a number of iterations

When the Drone receives a [``PolicyCommand<DroneState,P,DroneAction>``](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/commands/PolicyCommand.java), it uses a [``PolicyExecutor<DroneState,P,DroneAction>``](https://github.com/ml4j/ml4j-mdp/blob/master/src/main/java/org/ml4j/mdp/PolicyExecutor.java) along with the encapsulated Policy and PolicyStateMapper to make decisions about which [DroneAction](https://github.com/ml4j/dronez-core/blob/master/src/main/java/org/ml4j/dronez/DroneAction) to take for each iteration, using the latest observed [DroneState](https://github.com/ml4j/dronez-core/blob/master/src/main/java/org/ml4j/dronez/DroneState.java).

The selected [DroneAction](https://github.com/ml4j/dronez-core/blob/master/src/main/java/org/ml4j/dronez/DroneAction.java)s are sent to the Drone to be sent on to the ARDrone.

The aim of this project is to implement an AbstractCommandFactory which creates commands that minimise distance to a specified target position using a learned Policy<TargetRelativeDroneStateWithRecentActions, DroneAction> 

This project is currently configured with an implementation of AbstractCommandFactory   ( [DronezIndependentDimensionsLearnedContinuousStatePolicyCommandFactory] (https://github.com/ml4j/dronez-demo/blob/master/src/main/java/org/ml4j/dronez/DronezIndependentDimensionsLearnedContinuousStatePolicyCommandFactory.java) ) which:

1. Uses an independent-dimensions strategy to construct a policy from 3 learned policies for the 3 dimensions:   

*  ``Policy<TargetRelativePositionWithVelocityAndRecentActions<LeftRightAction>``
*  ``Policy<TargetRelativePositionWithVelocityAndRecentActions<UpDown>``
*  ``Policy<TargetRelativePositionWithVelocityAndRecentActions<ForwardBackAction>``

2. Assumes that policies are generated from a continuous-state markov decision process, and are [ContinuousStateValueFunctionGreedyPolicy<?> instances]  (https://github.com/ml4j/ml4j-mdp/blob/master/src/main/java/org/ml4j/mdp/ContinuousStateValueFunctionGreedyPolicy.java)s

3. Loads the 3 policies from serialized files, by name, from the [dronez-policies](https://github.com/ml4j/dronez-policies) project.

With this configuration, then, the revised aim of this project is to demonstrate test flights which minimise distance to target ( as specified by [``Trajectory<DroneState>``](https://github.com/ml4j/ml4j-mdp/blob/master/src/main/java/org/ml4j/mdp/Trajectory.java) instances), using 3 learned policies as detailed in 1. above , loaded by name from the [dronez-policies](https://github.com/ml4j/dronez-policies) project.


To fly real ARDrone using webcam position estimation:

mvn spring-boot:run -Dspring.profiles.active=ardrone -Djava.awt.headless=false

To fly [MockDrone](https://github.com/michaellavelle/dronez/blob/master/src/main/java/org/machinelearning4j/dronez/mock/MockDrone.java) with simulated position estimation generated from a learned model of the Drone ( from the [dronez-models](https://github.com/ml4j/dronez-models) project)

mvn spring-boot:run -Dspring.profiles.active=test -Djava.awt.headless=false
