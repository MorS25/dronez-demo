package org.ml4j.dronez;

import java.util.Date;

import org.machinelearning4j.dronez.commands.AbstractIndependentDimensionsCommandFactory;
import org.ml4j.mdp.ContinuousStateDelayedMdpValueFunctionGreedyPolicy;
import org.ml4j.mdp.ContinuousStateValueFunctionGreedyPolicy;
import org.ml4j.mdp.Policy;
import org.ml4j.util.SerializationHelper;

public class DronezIndependentDimensionsLearnedContinuousStatePolicyCommandFactory extends
		AbstractIndependentDimensionsCommandFactory {

	private SerializationHelper serializationHelper;
	private String forwardBackPolicyName;
	private String leftRightPolicyName;
	private String upDownPolicyName;
	private String historySerializationDir;
	private Date historySerializationDate;
	private boolean delayed;
	
	public DronezIndependentDimensionsLearnedContinuousStatePolicyCommandFactory(
			int recentActionCount,SerializationHelper serializationHelper,String leftRightPolicyName,String upDownPolicyName,String forwardBackPolicyName,String historySerializationDir,boolean delayed) {
		super(recentActionCount);
		this.serializationHelper = serializationHelper;
		this.leftRightPolicyName = leftRightPolicyName;
		this.upDownPolicyName = upDownPolicyName;
		this.forwardBackPolicyName = forwardBackPolicyName;
		this.historySerializationDir = historySerializationDir;
		this.delayed = delayed;
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
		if (delayed)
		{
			return serializationHelper.deserialize(ContinuousStateDelayedMdpValueFunctionGreedyPolicy.class,
				forwardBackPolicyName);
		}
		else
		{
			return serializationHelper.deserialize(ContinuousStateValueFunctionGreedyPolicy.class,
					forwardBackPolicyName);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Policy<TargetRelativePositionWithVelocityAndRecentActions<LeftRightAction>, LeftRightAction> createLeftRightDistanceToTargetPolicy() {
		if (delayed)
		{
			return serializationHelper.deserialize(ContinuousStateDelayedMdpValueFunctionGreedyPolicy.class,
					leftRightPolicyName);	
		}
		else
		{
			return serializationHelper.deserialize(ContinuousStateValueFunctionGreedyPolicy.class,
					leftRightPolicyName);	
		}
		
		
		}

	@SuppressWarnings("unchecked")
	@Override
	protected Policy<TargetRelativePositionWithVelocityAndRecentActions<UpDownAction>, UpDownAction> createUpDownDistanceToTargetPolicy() {
		if (delayed)
		{
			return serializationHelper.deserialize(ContinuousStateDelayedMdpValueFunctionGreedyPolicy.class,
				upDownPolicyName);
		}
		else
		{
			return serializationHelper.deserialize(ContinuousStateValueFunctionGreedyPolicy.class,
					upDownPolicyName);
		}
	}

}
