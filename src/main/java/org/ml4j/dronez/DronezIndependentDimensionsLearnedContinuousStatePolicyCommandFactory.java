package org.ml4j.dronez;

import org.machinelearning4j.dronez.commands.AbstractIndependentDimensionsCommandFactory;
import org.ml4j.mdp.ContinuousStateValueFunctionGreedyPolicy;
import org.ml4j.mdp.Policy;
import org.ml4j.util.SerializationHelper;

public class DronezIndependentDimensionsLearnedContinuousStatePolicyCommandFactory extends
		AbstractIndependentDimensionsCommandFactory {

	private SerializationHelper serializationHelper;
	private String forwardBackPolicyName;
	private String leftRightPolicyName;
	private String upDownPolicyName;
	
	public DronezIndependentDimensionsLearnedContinuousStatePolicyCommandFactory(
			int recentActionCount,SerializationHelper serializationHelper,String leftRightPolicyName,String upDownPolicyName,String forwardBackPolicyName) {
		super(recentActionCount);
		this.serializationHelper = serializationHelper;
		this.leftRightPolicyName = leftRightPolicyName;
		this.upDownPolicyName = upDownPolicyName;
		this.forwardBackPolicyName = forwardBackPolicyName;

	}

	@Override
	public void updatePolicies() {
		// Policies are static - no-op
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Policy<TargetRelativePositionWithVelocityAndRecentActions<ForwardBackAction>, ForwardBackAction> createForwardBackDistanceToTargetPolicy() {
		return serializationHelper.deserialize(ContinuousStateValueFunctionGreedyPolicy.class,
				forwardBackPolicyName);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Policy<TargetRelativePositionWithVelocityAndRecentActions<LeftRightAction>, LeftRightAction> createLeftRightDistanceToTargetPolicy() {
		return serializationHelper.deserialize(ContinuousStateValueFunctionGreedyPolicy.class,
				leftRightPolicyName);	}

	@SuppressWarnings("unchecked")
	@Override
	protected Policy<TargetRelativePositionWithVelocityAndRecentActions<UpDownAction>, UpDownAction> createUpDownDistanceToTargetPolicy() {
		return serializationHelper.deserialize(ContinuousStateValueFunctionGreedyPolicy.class,
				upDownPolicyName);
	}

}
