package kr.co.iwaterski.camera.speedmanager;

import java.util.ArrayList;

import kr.co.iwaterski.camera.settings.Data;
import kr.co.iwaterski.camera.settings.Data.Item;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class Speed implements LocationListener
{
	Context mContext;
	long mStartTime = 0;
	long mEndTime = 0;
	ArrayList<SpeedListener> mSpeedListenerList = new ArrayList<SpeedListener>();
	LocationManager mLocationManager = null;
	String mProvider;

	String mLatitude;
	String mLongitude;
	int mBoundary;
	int mMinSpeed;
	int mMinStartTime;
	int mMinStopTime;

	SharedPreferences mSharedPreferences;

	public Speed(Context context)
	{
		mContext = context;

		mSharedPreferences = mContext.getSharedPreferences("kr.co.iwaterski.camera", Context.MODE_PRIVATE);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		criteria.setAltitudeRequired(true);
		criteria.setBearingRequired(true);
		criteria.setSpeedRequired(true);
		criteria.setCostAllowed(false);

		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		mProvider = mLocationManager.getBestProvider(criteria, true);
	}

	public void startChecking()
	{
		refreshData();
		mLocationManager.requestLocationUpdates(mProvider, 100, 0, this);
	}

	public void stopChecking()
	{
		mLocationManager.removeUpdates(this);
	}

	public void addEventListener(SpeedListener speedlistener)
	{
		if (mSpeedListenerList.contains(speedlistener) == false)
		{
			mSpeedListenerList.add(speedlistener);
		}
	}

	public void deleteEventListener(SpeedListener speedlistener)
	{
		if (mSpeedListenerList.contains(speedlistener))
		{
			mSpeedListenerList.remove(speedlistener);
		}
	}

	@Override
	public void onLocationChanged(Location location)
	{
		float speed = location.getSpeed() * 3600 / 1000;
		long time = location.getTime();

		if (mMinSpeed * 5 < speed)
		{
			mEndTime = 0;

			if (mStartTime == 0)
			{
				mStartTime = time;

				for (SpeedListener listener : mSpeedListenerList)
				{
					listener.onMoveChangeEvent(SpeedListener.Move.ACCELERATE, location);

				}
			}
			else
			{
				if ((time - mStartTime) / 1000 > mMinStartTime)
				{
					for (SpeedListener listener : mSpeedListenerList)
					{
						listener.onMoveChangeEvent(SpeedListener.Move.MOVE, location);
					}
				}
			}
		}
		else
		{
			mStartTime = 0;

			if (mEndTime == 0)
			{
				mEndTime = time;

				for (SpeedListener listener : mSpeedListenerList)
				{
					listener.onMoveChangeEvent(SpeedListener.Move.DEACCELERATE, location);
				}
			}
			else
			{
				if ((time - mEndTime) / 1000 > mMinStopTime)
				{
					for (SpeedListener listener : mSpeedListenerList)
					{
							listener.onMoveChangeEvent(SpeedListener.Move.STOP, location);
					}
				}
			}
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	}

	@Override
	public void onProviderEnabled(String provider)
	{
	}

	@Override
	public void onProviderDisabled(String provider)
	{
	}

	public void refreshData()
	{
		mLatitude = Data.getData(mContext, Item.GPS_LATITUDE);
		mLongitude = Data.getData(mContext, Item.GPS_LONGITUDE);
		mBoundary = Integer.valueOf(Data.getData(mContext, Item.GPS_BOUNDARY));
		mMinSpeed = Integer.valueOf(Data.getData(mContext, Item.GPS_MIN_SPEED));
		mMinStartTime = Integer.valueOf(Data.getData(mContext, Item.GPS_MIN_START_TIME));
		mMinStopTime = Integer.valueOf(Data.getData(mContext, Item.GPS_MIN_STOP_TIME));
	}
}
