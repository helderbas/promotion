package nl.topicuszorg.promotion;

import android.content.IntentFilter;

/**
 * Created by Bas on 18/03/2017.
 */

public class BeaconIntentFilterWrapper
{
	private IntentFilter intentFilter;

	public BeaconIntentFilterWrapper()
	{
		intentFilter = new IntentFilter();
		intentFilter.addAction(PromotionApplication.REGION_ENTERED_EVENT);
		intentFilter.addAction(PromotionApplication.REGION_LEFT_EVENT);
	}

	public IntentFilter getIntentFilter()
	{
		return intentFilter;
	}
}
