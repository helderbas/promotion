package nl.topicuszorg.promotion;

import java.util.Date;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity
{
	@ViewById
	public TextView regionLog;

	private BroadcastReceiver beaconEventReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Region region = (Region) intent.getParcelableExtra("region");
			logRegionEvent(region, intent.getAction());
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs)
	{
		return super.onCreateView(name, context, attrs);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		SystemRequirementsChecker.checkWithDefaultDialogs(this);

		LocalBroadcastManager.getInstance(this).registerReceiver(beaconEventReceiver, new BeaconIntentFilterWrapper().getIntentFilter());
	}

	private void logRegionEvent(Region region, String regionEvent)
	{
		regionLog.append(String.format("%s: %s %s %s", new Date().toString(), regionEvent, region.getIdentifier(),
			System.getProperty("line.separator")));
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(beaconEventReceiver);
	}
}
