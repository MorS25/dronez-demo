package org.ml4j.dronez;

import java.util.Date;

import org.machinelearning4j.dronez.commands.AbstractIndependentDimensionsCommandFactory;
import org.ml4j.mdp.ActionValueFunctionDelayedModelAdapter;
import org.ml4j.mdp.ActionValueFunctionModelAdapter;
import org.ml4j.mdp.ContinuousStateActionValueFunctionGreedyPolicy;
import org.ml4j.mdp.Policy;
import org.ml4j.util.SerializationHelper;

public class DronezIndependentDimensionsLearnedContinuousStateActionValueFunctionPolicyCommandFactory extends
		AbstractIndependentDimensionsCommandFactory {

	private SerializationHelper serializationHelper;
	private String forwardBackActionValueFunctionName;
	private String leftRightActionValueFunctionName;
	private String upDownActionValueFunctionName;
	private String historySerializationDir;
	private Date historySerializationDate;
	private int delayInIterations;
	private int numberOfSamples= 1;
	
	public DronezIndependentDimensionsLearnedContinuousStateActionValueFunctionPolicyCommandFactory(
			int recentActionCount,SerializationHelper serializationHelper,String leftRightActionValueFunctionName,String upDownActionValueFunctionName,String forwardBackActionValueFunctionName,String historySerializationDir,int delayInIterations) {
		super(recentActionCount);
		this.serializationHelper = serializationHelper;
		this.leftRightActionValueFunctionName = leftRightActionValueFunctionName;
		this.upDownActionValueFunctionName = upDownActionValueFunctionName;
		this.forwardBackActionValueFunctionName = forwardBackActionValueFunctionName;
		this.historySerializationDir = historySerializationDir;
		this.delayInIterations = delayInIterations;
	}
	
	

	@Override
	public void updatePolicies() {
		// Policies are static - no-op
		// Persist history here
		if (historySerializationDate == null)
		{
			historySerializationDate = new Date();
		}
		
		SerializationHelper helper = new SerializationHelper(historySerializationDir);
		helper.serialize(history, "flight_" + historySerializationDate.getTime());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Policy<TargetRelativePositionWithVelocityAndRecentActions<ForwardBackAction>, ForwardBackAction> createForwardBackDistanceToTargetPolicy() {
		
		if (delayInIterations >  0)
		{
			ActionValueFunctionModelAdapter<TargetRelativePositionWithVelocityAndRecentActions<ForwardBackAction>, ForwardBackAction> actionValueFunction =  serializationHelper.deserialize(ActionValueFunctionDelayedModelAdapter.class,
					forwardBackActionValueFunctionName);
			return new ContinuousStateActionValueFunctionGreedyPolicy<TargetRelativePositionWithVelocityAndRecentActions<ForwardBackAction>, ForwardBackAction>(ForwardBackAction.ALL_ACTIONS,actionValueFunction,numberOfSamples);
		}
		else
		{
			ActionValueFunctionModelAdapter<TargetRelativePositionWithVelocityAndRecentActions<ForwardBackAction>, ForwardBackAction> actionValueFunction =  serializationHelper.deserialize(ActionValueFunctionModelAdapter.class,
					forwardBackActionValueFunctionName);
			return new ContinuousStateActionValueFunctionGreedyPolicy<TargetRelativePositionWithVelocityAndRecentActions<ForwardBackAction>, ForwardBackAction>(ForwardBackAction.ALL_ACTIONS,actionValueFunction,numberOfSamples);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Policy<TargetRelativePositionWithVelocityAndRecentActions<LeftRightAction>, LeftRightAction> createLeftRightDistanceToTargetPolicy() {
		if (delayInIterations >  0)
		{
			ActionValueFunctionModelAdapter<TargetRelativePositionWithVelocityAndRecentActions<LeftRightAction>, LeftRightAction> actionValueFunction =  serializationHelper.deserialize(ActionValueFunctionDelayedModelAdapter.class,
					leftRightActionValueFunctionName);
			return new ContinuousStateActionValueFunctionGreedyPolicy<TargetRelativePositionWithVelocityAndRecentActions<LeftRightAction>, LeftRightAction>(LeftRightAction.ALL_ACTIONS,actionValueFunction,numberOfSamples);
		}
		else
		{
			ActionValueFunctionModelAdapter<TargetRelativePositionWithVelocityAndRecentActions<LeftRightAction>, LeftRightAction> actionValueFunction =  serializationHelper.deserialize(ActionValueFunctionModelAdapter.class,
					leftRightActionValueFunctionName);
			return new ContinuousStateActionValueFunctionGreedyPolicy<TargetRelativePositionWithVelocityAndRecentActions<LeftRightAction>, LeftRightAction>(LeftRightAction.ALL_ACTIONS,actionValueFunction,numberOfSamples);
		}
		
		}

	@SuppressWarnings("unchecked")
	@Override
	protected Policy<TargetRelativePositionWithVelocityAndRecentActions<UpDownAction>, UpDownAction> createUpDownDistanceToTargetPolicy() {
		if (delayInIterations >  0)
		{
			ActionValueFunctionModelAdapter<TargetRelativePositionWithVelocityAndRecentActions<UpDownAction>, UpDownAction> actionValueFunction =  serializationHelper.deserialize(ActionValueFunctionDelayedModelAdapter.class,
					upDownActionValueFunctionName);
			return new ContinuousStateActionValueFunctionGreedyPolicy<TargetRelativePositionWithVelocityAndRecentActions<UpDownAction>, UpDownAction>(UpDownAction.ALL_ACTIONS,actionValueFunction,numberOfSamples);
		}
		else
		{
			ActionValueFunctionModelAdapter<TargetRelativePositionWithVelocityAndRecentActions<UpDownAction>, UpDownAction> actionValueFunction =  serializationHelper.deserialize(ActionValueFunctionModelAdapter.class,
					upDownActionValueFunctionName);
			return new ContinuousStateActionValueFunctionGreedyPolicy<TargetRelativePositionWithVelocityAndRecentActions<UpDownAction>, UpDownAction>(UpDownAction.ALL_ACTIONS,actionValueFunction,numberOfSamples);
		}
		
	}

}
