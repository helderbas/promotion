package nl.topicuszorg.promotion;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Bas on 18/03/2017.
 */

public class PromotionApplication extends Application
{
	public static final String REGION_ENTERED_EVENT = "region.entered";

	public static final String REGION_LEFT_EVENT = "region.left";

	private BeaconManager beaconManager;

	private BroadcastReceiver beaconEventReceiver;

	@Override
	public void onCreate()
	{
		super.onCreate();

		beaconManager = new BeaconManager(getApplicationContext());

		beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener()
		{
			@Override
			public void onEnteredRegion(Region region, List<Beacon> list)
			{
				Intent intent = new Intent(REGION_ENTERED_EVENT);
				intent.putExtra("region", region);

				LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
			}

			@Override
			public void onExitedRegion(Region region)
			{
				Intent intent = new Intent(REGION_LEFT_EVENT);
				intent.putExtra("region", region);

				LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
			}
		});

		beaconManager.connect(new BeaconManager.ServiceReadyCallback()
		{
			@Override
			public void onServiceReady()
			{
				beaconManager.startMonitoring(
					new Region(
						"K43 eerste verdieping",
						UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
						22504, 48827));
			}
		});

		beaconEventReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				Region region = intent.getParcelableExtra("region");

				if (intent.getAction().equals(REGION_ENTERED_EVENT))
				{
					showNotification("Region Entered", String.format("Date: %s, region: %s", new Date().toString(), region.getIdentifier()));
				}
				else
				{
					showNotification("Left Region", String.format("Date: %s, region: %s", new Date().toString(), region.getIdentifier()));
				}
			}
		};

		LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(beaconEventReceiver,
			new BeaconIntentFilterWrapper().getIntentFilter());
	}

	@Override
	public void onTerminate()
	{
		LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(beaconEventReceiver);
		super.onTerminate();
	}

	public void showNotification(String title, String message)
	{
		Intent notifyIntent = new Intent(this, MainActivity.class);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
			new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification notification = new Notification.Builder(this)
			.setSmallIcon(android.R.drawable.ic_dialog_info)
			.setContentTitle(title)
			.setContentText(message)
			.setAutoCancel(true)
			.setContentIntent(pendingIntent)
			.build();
		notification.defaults |= Notification.DEFAULT_SOUND;
		NotificationManager notificationManager =
			(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, notification);
	}
}
