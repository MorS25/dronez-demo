package org.ml4j.dronez;

import java.util.Arrays;
import java.util.Date;

import org.machinelearning4j.dronez.commands.AbstractIndependentDimensionsCommandFactory;
import org.ml4j.dronez.models.MockDimModelWithoutDelay;
import org.ml4j.dronez.models.SingleDimensionDroneDistanceToTargetPositionModel;
import org.ml4j.dronez.models.learning.DroneModelLearner;
import org.ml4j.dronez.models.learning.ModelLearner;
import org.ml4j.dronez.policy.learning.ContinuousStateMarkovDecisionProcessDronePolicyLearner;
import org.ml4j.dronez.policy.learning.PolicyLearner;
import org.ml4j.dronez.util.StateActionSequenceHistoryConvertingLoader;
import org.ml4j.mdp.AbstractValueFunctionGreedyPolicy;
import org.ml4j.mdp.ActionValueFunctionDelayedModelAdapter;
import org.ml4j.mdp.ActionValueFunctionModelAdapter;
import org.ml4j.mdp.Model;
import org.ml4j.mdp.Policy;
import org.ml4j.mdp.RandomPolicy;
import org.ml4j.mdp.StateActionSequenceHistory;
import org.ml4j.util.SerializationHelper;

public class DronezIndependentDimensionsLearningContinuousStatePolicyCommandFactory extends
		AbstractIndependentDimensionsCommandFactory {

	
	private Date historySerializationDate;
	
	private SerializationHelper serializationHelper;
	
	private int modelDelayInIterations;
	
	private Policy<TargetRelativePositionWithVelocityAndRecentActions<ForwardBackAction>, ForwardBackAction> forwardBackPolicy;
	private Policy<TargetRelativePositionWithVelocityAndRecentActions<LeftRightAction>, LeftRightAction> leftRightPolicy;
	private Policy<TargetRelativePositionWithVelocityAndRecentActions<UpDownAction>, UpDownAction> upDownPolicy;

	private Model<TargetRelativePositionWithVelocityAndRecentActions<LeftRightAction>, TargetRelativePositionWithVelocityAndRecentActions<LeftRightAction>, LeftRightAction> leftRightModel = null;;
	private Model<TargetRelativePositionWithVelocityAndRecentActions<UpDownAction>, TargetRelativePositionWithVelocityAndRecentActions<UpDownAction>, UpDownAction> upDownModel = null;;
	private Model<TargetRelativePositionWithVelocityAndRecentActions<ForwardBackAction>, TargetRelativePositionWithVelocityAndRecentActions<ForwardBackAction>, ForwardBackAction> forwardBackModel = null;;

	
	
	public DronezIndependentDimensionsLearningContinuousStatePolicyCommandFactory(
			int recentActionCount,int modelDelayInIterations,String historySerializationDir) {
		super(recentActionCount);
		
		serializationHelper = new SerializationHelper(historySerializationDir);

		SerializationHelper policySerializationHelper = new SerializationHelper(PolicyLearner.class.getClassLoader(), "org/ml4j/dronez/policies");

		
		//this.leftRightPolicy = policySerializationHelper.deserialize(ContinuousStateDelayedMdpValueFunctionGreedyPolicy.class, "policy_lr_1427557210474");
		//this.upDownPolicy = policySerializationHelper.deserialize(ContinuousStateDelayedMdpValueFunctionGreedyPolicy.class, "policy_ud_1427557210474");
		//this.forwardBackPolicy = policySerializationHelper.deserialize(ContinuousStateDelayedMdpValueFunctionGreedyPolicy.class, "policy_fb_1427557210474");

		//this.leftRightPolicy = new SimpleLeftRightPolicy();
		this.leftRightPolicy = new RandomPolicy<TargetRelativePositionWithVelocityAndRecentActions<LeftRightAction>,LeftRightAction>(LeftRightAction.ALL_ACTIONS);
		//this.upDownPolicy = new SimpleUpDownPolicy();
		this.upDownPolicy = new RandomPolicy<TargetRelativePositionWithVelocityAndRecentActions<UpDownAction>,UpDownAction>(UpDownAction.ALL_ACTIONS);
		//this.forwardBackPolicy = new SimpleForwardBackPolicy();
		this.forwardBackPolicy = new RandomPolicy<TargetRelativePositionWithVelocityAndRecentActions<ForwardBackAction>,ForwardBackAction>(ForwardBackAction.ALL_ACTIONS);
		this.modelDelayInIterations = modelDelayInIterations;
	}
	
	protected void learnPolicies()
	{
		
		
		ContinuousStateMarkovDecisionProcessDronePolicyLearner<LeftRightAction> 
		
		leftRightLearner 
		 = modelDelayInIterations > 0 ? new ContinuousStateMarkovDecisionProcessDronePolicyLearner<LeftRightAction>
			(leftRightModel, LeftRightAction.ALL_ACTIONS,modelDelayInIterations) : new ContinuousStateMarkovDecisionProcessDronePolicyLearner<LeftRightAction>
			(leftRightModel, LeftRightAction.ALL_ACTIONS);
		
		ContinuousStateMarkovDecisionProcessDronePolicyLearner<UpDownAction> 
		
		upDownLearner 
		 = modelDelayInIterations > 0 ? new ContinuousStateMarkovDecisionProcessDronePolicyLearner<UpDownAction>
			(upDownModel, UpDownAction.ALL_ACTIONS,modelDelayInIterations) : new ContinuousStateMarkovDecisionProcessDronePolicyLearner<UpDownAction>
			(upDownModel, UpDownAction.ALL_ACTIONS);
		
		
	ContinuousStateMarkovDecisionProcessDronePolicyLearner<ForwardBackAction> 
		
		forwardBackLearner 
		 = modelDelayInIterations > 0 ? new 	ContinuousStateMarkovDecisionProcessDronePolicyLearner<ForwardBackAction>
			(forwardBackModel, ForwardBackAction.ALL_ACTIONS,modelDelayInIterations) : new 	ContinuousStateMarkovDecisionProcessDronePolicyLearner<ForwardBackAction>
			(forwardBackModel, ForwardBackAction.ALL_ACTIONS);
		
		
		leftRightPolicy = leftRightLearner.learnPolicy();
		upDownPolicy = upDownLearner.learnPolicy();
		forwardBackPolicy = forwardBackLearner.learnPolicy();

		
	}

	@Override
	public void updatePolicies() {

		
		if (historySerializationDate == null)
		{
			historySerializationDate = new Date();
		}
		
		learnModels();
		
		learnPolicies();
	
		
		
		serializationHelper.serialize(history, "flight_" + historySerializationDate.getTime());
		serializationHelper.serialize(leftRightPolicy, "policy_lr_" + historySerializationDate.getTime());
		serializationHelper.serialize(upDownPolicy, "policy_ud_" + historySerializationDate.getTime());
		serializationHelper.serialize(forwardBackPolicy, "policy_fb_" + historySerializationDate.getTime());
		
		serializeActionValueFunction(serializationHelper,leftRightPolicy,leftRightModel,LeftRightAction.ALL_ACTIONS,"lr");
		serializeActionValueFunction(serializationHelper,upDownPolicy,upDownModel,UpDownAction.ALL_ACTIONS,"ud");
		serializeActionValueFunction(serializationHelper,forwardBackPolicy,forwardBackModel,ForwardBackAction.ALL_ACTIONS,"fb");

		
		init();
	}
	
	
	private <A extends NumericAction> void serializeActionValueFunction(SerializationHelper helper,Policy<TargetRelativePositionWithVelocityAndRecentActions<A>,A> policy,Model<TargetRelativePositionWithVelocityAndRecentActions<A>,TargetRelativePositionWithVelocityAndRecentActions<A>,A> model,A[] actions,String suffix)
	{
		if (policy instanceof AbstractValueFunctionGreedyPolicy)
		{
		
			AbstractValueFunctionGreedyPolicy<TargetRelativePositionWithVelocityAndRecentActions<A>,A>
			 valueFunctionPolicy = (AbstractValueFunctionGreedyPolicy<TargetRelativePositionWithVelocityAndRecentActions<A>,A>)policy;
			
			
			if (modelDelayInIterations > 0)
			{
				ActionValueFunctionModelAdapter<TargetRelativePositionWithVelocityAndRecentActions<A>,A> actionValueFunction
				= new ActionValueFunctionDelayedModelAdapter<TargetRelativePositionWithVelocityAndRecentActions<A>,A>(((AbstractValueFunctionGreedyPolicy<TargetRelativePositionWithVelocityAndRecentActions<A>,A>) valueFunctionPolicy).getValueFunction(),model,Arrays.asList(actions),modelDelayInIterations);
			
				serializationHelper.serialize(actionValueFunction, "actionValueFunction_" + suffix + "_" +  historySerializationDate.getTime());

			}
			else 
			{
			
			ActionValueFunctionModelAdapter<TargetRelativePositionWithVelocityAndRecentActions<A>,A> actionValueFunction
			= new ActionValueFunctionModelAdapter<TargetRelativePositionWithVelocityAndRecentActions<A>,A>(((AbstractValueFunctionGreedyPolicy<TargetRelativePositionWithVelocityAndRecentActions<A>,A>) valueFunctionPolicy).getValueFunction(),model,Arrays.asList(actions));
		
			serializationHelper.serialize(actionValueFunction, "actionValueFunction_" + suffix + "_" +  historySerializationDate.getTime());

			}
		}
		
	}
	

	@SuppressWarnings("unchecked")
	private void learnModels() {
		
	
				// Consider only the first of the six recent actions, and ignore latest action
			//	boolean[] recentActionsAndLatestActionMask 
				// = new boolean[]{true,true,true,true,false,false,false ,false,false,false,false};
				
				boolean[] recentActionsAndLatestActionMask 
				 = new boolean[]{true,true,true,false,false,false,false,false,false,false,false };
				
			
				// Create our model learner.
				ModelLearner<DroneStateWithRecentActions,DroneStateWithRecentActions,DroneAction> modelLearner = new DroneModelLearner(getRecentActionCount(),recentActionsAndLatestActionMask,serializationHelper);
				
				StateActionSequenceHistory<DroneStateWithRecentActions, DroneStateWithRecentActions, DroneAction> history1 = StateActionSequenceHistoryConvertingLoader.convert(history, getRecentActionCount());
				
				Model<DroneStateWithRecentActions, DroneStateWithRecentActions, DroneAction> model = modelLearner.learnModel(history1);

				serializationHelper.serialize(model, "model_" + historySerializationDate.getTime());

				
				
				LinearApproximationDeltaPositionWithVelocityModel<LeftRightAction> leftRightDeltaModel = serializationHelper.deserialize(LinearApproximationDeltaPositionWithVelocityModel.class, "droneDeltaPositionLeftRightActionModel");
				LinearApproximationDeltaPositionWithVelocityModel<UpDownAction> upDownDeltaModel = serializationHelper.deserialize(LinearApproximationDeltaPositionWithVelocityModel.class, "droneDeltaPositionUpDownActionModel");
				LinearApproximationDeltaPositionWithVelocityModel<ForwardBackAction> forwardBackDeltaModel = serializationHelper.deserialize(LinearApproximationDeltaPositionWithVelocityModel.class, "droneDeltaPositionForwardBackActionModel");
				
				SingleDimensionDroneDistanceToTargetPositionModel<LeftRightAction> leftRightModel1 = new SingleDimensionDroneDistanceToTargetPositionModel<LeftRightAction>(leftRightDeltaModel,-2.5,2.5,-0.5,0.5,Arrays.asList(LeftRightAction.ALL_ACTIONS),getRecentActionCount());
				SingleDimensionDroneDistanceToTargetPositionModel<UpDownAction> upDownModel1 = new SingleDimensionDroneDistanceToTargetPositionModel<UpDownAction>(upDownDeltaModel,-2.5,2.5,-0.5,0.5,Arrays.asList(UpDownAction.ALL_ACTIONS),getRecentActionCount());
				SingleDimensionDroneDistanceToTargetPositionModel<ForwardBackAction> forwardBackModel1 = new SingleDimensionDroneDistanceToTargetPositionModel<ForwardBackAction>(forwardBackDeltaModel,-2.5,2.5,-0.5,0.5,Arrays.asList(ForwardBackAction.ALL_ACTIONS),getRecentActionCount());

				if (modelDelayInIterations > 0)
				{
	
					leftRightModel = new MockDimModelWithoutDelay<LeftRightAction>(leftRightModel1,LeftRightAction.NO_OP,getRecentActionCount(),modelDelayInIterations);
					upDownModel = new MockDimModelWithoutDelay<UpDownAction>(upDownModel1,UpDownAction.NO_OP,getRecentActionCount(),modelDelayInIterations);
					forwardBackModel = new MockDimModelWithoutDelay<ForwardBackAction>(forwardBackModel1,ForwardBackAction.NO_OP,getRecentActionCount(),modelDelayInIterations);
				}
				else
				{
					leftRightModel = leftRightModel1;
					upDownModel = upDownModel1;
					forwardBackModel = forwardBackModel1;
				}
				
	}



	@Override
	protected Policy<TargetRelativePositionWithVelocityAndRecentActions<ForwardBackAction>, ForwardBackAction> createForwardBackDistanceToTargetPolicy() {
		return forwardBackPolicy;
	}

	@Override
	protected Policy<TargetRelativePositionWithVelocityAndRecentActions<LeftRightAction>, LeftRightAction> createLeftRightDistanceToTargetPolicy() {
		return leftRightPolicy;
	}

	@Override
	protected Policy<TargetRelativePositionWithVelocityAndRecentActions<UpDownAction>, UpDownAction> createUpDownDistanceToTargetPolicy() {
		return upDownPolicy;
	}
}