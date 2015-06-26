package org.ml4j.dronez;

import org.ml4j.algorithms.FeaturesMapper;

public class ActionValueFunctionFeaturesMapper implements FeaturesMapper<TargetRelativePositionWithVelocityAndRecentActions<LeftRightAction>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int getFeatureCount() {
		return 2 + PositionVelocityWithRecentActions.DEFAULT_RECENT_ACTION_COUNT;
	}

	@Override
	public double[] toFeaturesVector(
			TargetRelativePositionWithVelocityAndRecentActions<LeftRightAction> arg0) {
		
			double[] features = new double[getFeatureCount()];
			features[0] = arg0.getPosition();
			features[1] = arg0.getVelocity();
			int ind = 2;
			for (LeftRightAction action : arg0.getRecentActions())
			{
				features[ind++] = action.getValue();
			}
			return features;
		}

}
