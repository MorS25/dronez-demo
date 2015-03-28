package org.ml4j.dronez;

import org.machinelearning4j.dronez.commands.CommandFactory;
import org.machinelearning4j.dronez.domain.Drone;
import org.machinelearning4j.dronez.mock.MockDrone;
import org.machinelearning4j.dronez.mock.MockDroneDimensionModel;
import org.machinelearning4j.dronez.mock.MockDroneModel;
import org.machinelearning4j.dronez.mock.MockWebCamObserver;
import org.ml4j.dronez.models.DroneModel;
import org.ml4j.dronez.models.StatefulDroneStateWithoutActionsModelAdapter;
import org.ml4j.dronez.models.factories.ModelFactory;
import org.ml4j.dronez.models.factories.SerializedModelFactory;
import org.ml4j.dronez.models.learning.DroneModelLearner;
import org.ml4j.dronez.policy.learning.PolicyLearner;
import org.ml4j.mdp.Model;
import org.ml4j.util.SerializationHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class DronezMockConfig {

	@Value("${policy.recentActionCount}")
	private int policyRecentActionCount;


	public static DroneState HOVER_POSITION = new DroneState(new PositionVelocity(0, 0), new PositionVelocity(0.3, 0),
			new PositionVelocity(2.5, 0), new PositionVelocity(0, 0));
	
	
	@Value("${model.delay}")
	private int modelDelayInIterations;

	
	
	@Value("${leftRightContinuousStatePolicy.name}")
	private String leftRightPolicyName;
	
	@Value("${upDownContinuousStatePolicy.name}")
	private String upDownPolicyName;
	
	@Value("${forwardBackContinuousStatePolicy.name}")
	private String forwardBackPolicyName;
	
	@Value("${historySerializationDir}")
	private String historySerializationDir;
	
	/**
	 * Create a StateActionController<DroneState, DroneAction> for the Drone
	 * 
	 * This acts as both StateObserver<DroneState> and ActionTaker<DroneAction>
	 * 
	 * This mock updates the mock droneObserver() with actions taken
	 * and delegates to the mock droneObserver() for state observation
	 * 
	 * @return the Drone
	 */
	@Bean
	public Drone drone() {
		return new MockDrone(droneObserver(), mockDroneModel(),
				0, 160);
	}
	
	/**
	 * Observe the drone using a mock web cam observer
	 * 
	 * This mock uses a Model of the drone to output
	 * simulated positions given actions taken
	 * 
	 * @return the drone observer
	 */
	@Bean
	public MockWebCamObserver droneObserver() {
		PositionVelocity initialLeftRightState = new PositionVelocity(-0.1, 0.1);
		PositionVelocity initialUpDownState = new PositionVelocity(-0.1, 0.1);
		PositionVelocity initialForwardBackState = new PositionVelocity(-0.1, 0);
		PositionVelocity initialSpinState = new PositionVelocity(0, 0);

		DroneState initialState = new DroneState(initialLeftRightState,
				initialUpDownState, initialForwardBackState, initialSpinState);

		MockWebCamObserver mockWebCamObserver = new MockWebCamObserver(
				initialState);

		return mockWebCamObserver;
	}

	
	/**
	 * Generates the commands we use to control drone flight
	 * 
	 * @return a CommandFactory implementation
	 */
	@Bean
	public CommandFactory commandFactory()
	{
		boolean learnInRealtime = true;
		if (learnInRealtime)
		{
			return new DronezIndependentDimensionsLearningContinuousStatePolicyCommandFactory(policyRecentActionCount,modelDelayInIterations,historySerializationDir);			
		}
		else
		{
			SerializationHelper serializationHelper = new SerializationHelper(PolicyLearner.class.getClassLoader(), "org/ml4j/dronez/policies");
			boolean delayedPolicy = true;

			return new DronezIndependentDimensionsLearnedContinuousStatePolicyCommandFactory(policyRecentActionCount,serializationHelper, leftRightPolicyName, upDownPolicyName, forwardBackPolicyName,historySerializationDir,delayedPolicy);

		}
		
		
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
	
	/**
	 * Create a Model of the drone we can use for our mock web cam observer
	 * 
	 * @return
	 */
	@Bean
	public Model<DroneState, DroneState, DroneAction> mockDroneModel() {
		boolean useActualFlightModel = true;

		if (useActualFlightModel) {
			int modelRecentActionCount = 10;
			return new StatefulDroneStateWithoutActionsModelAdapter(
					 droneModelFactory().createModel(
							"droneModel_27032015_1"), modelRecentActionCount);
		
		} else {

			Model<PositionVelocity, PositionVelocity, LeftRightAction> mockLeftRightModel = new MockDroneDimensionModel<LeftRightAction>(
					-2.5, 2.5, false);

			Model<PositionVelocity, PositionVelocity, UpDownAction> mockUpDownModel = new MockDroneDimensionModel<UpDownAction>(
					-2.5, 2.5, false);

			Model<PositionVelocity, PositionVelocity, ForwardBackAction> mockForwardBackModel = new MockDroneDimensionModel<ForwardBackAction>(
					0, 4, false);

			return new MockDroneModel(mockLeftRightModel, mockUpDownModel,
					mockForwardBackModel);
		}
	}

	/**
	 * Enables retrieval of existing learned Models
	 * 
	 * @return
	 */
	@Bean
	public ModelFactory<DroneStateWithRecentActions, DroneStateWithRecentActions, DroneAction> droneModelFactory() {
		// Create a serialized model factory
		SerializedModelFactory<DroneStateWithRecentActions, DroneStateWithRecentActions, DroneAction> modelFactory = new SerializedModelFactory<DroneStateWithRecentActions, DroneStateWithRecentActions, DroneAction>();
		// Register a serialized model with the model factory
		modelFactory.registerModel(DroneModelLearner.MODEL_CLASS, "droneModel");
		modelFactory.registerModel(DroneModelLearner.MODEL_CLASS,
				"droneModelExtendedRun");
		modelFactory.registerModel(DroneModelLearner.MODEL_CLASS,
				"droneModel_12032015_3");
		modelFactory.registerModel(DroneModelLearner.MODEL_CLASS,
				"droneModel_12032015_4");
		modelFactory.registerModel(DroneModelLearner.MODEL_CLASS,
				"droneModel_20032015_1");
		modelFactory.registerModel(DroneModelLearner.MODEL_CLASS,
				"droneModel_27032015_1");
		return modelFactory;
	}
	
	
}
