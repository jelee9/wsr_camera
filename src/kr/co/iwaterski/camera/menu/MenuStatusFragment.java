package kr.co.iwaterski.camera.menu;

import java.util.Calendar;

import kr.co.iwaterski.camera.R;
import kr.co.iwaterski.camera.speedmanager.Speed;
import kr.co.iwaterski.camera.speedmanager.SpeedListener;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

public class MenuStatusFragment extends Fragment implements SpeedListener, OnItemSelectedListener, TextWatcher
{
	Context mContext;
	Speed mSpeed = null;

	TextView mGpsStatusText;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.menu_status, container, false);

		mContext = getActivity();

		mGpsStatusText = (TextView) view.findViewById(R.id.gps_item_category);

		return view;
	}

	@Override
	public void onPause()
	{
		if (mSpeed != null)
		{
			mSpeed.deleteEventListener(this);
			mSpeed.stopChecking();
		}

		super.onPause();
	}

	@Override
	public void onStart()
	{
		mSpeed = new Speed(mContext);
		mSpeed.addEventListener(this);
		mSpeed.startChecking();

		super.onStart();
	}


	@Override
	public void onMoveChangeEvent(Move move, Location location)
	{
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		Double altitude = location.getAltitude();
		float accuracy = location.getAccuracy();
		float bearing = location.getBearing();
		float speedms = location.getSpeed();
		float speedkmh = speedms * 3600 / 1000;
		long time = location.getTime();

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		String year_string = Integer.toString(calendar.get(Calendar.YEAR));
		String month_string = (calendar.get(Calendar.MONTH) + 1 >= 10) ? Integer.toString(calendar.get(Calendar.MONTH) + 1) : "0" + Integer.toString(calendar.get(Calendar.MONTH) + 1);
		String date_string = (calendar.get(Calendar.DATE) >= 10) ? Integer.toString(calendar.get(Calendar.DATE)) : "0" + Integer.toString(calendar.get(Calendar.DATE));
		String hour_string = ((calendar.get(Calendar.HOUR_OF_DAY) == 0) ? "00" : ((calendar.get(Calendar.HOUR_OF_DAY) < 10) ? "0" + Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)) : Integer.toString(calendar.get(Calendar.HOUR_OF_DAY))));
		String minute_string = ((calendar.get(Calendar.MINUTE) == 0) ? "00" : ((calendar.get(Calendar.MINUTE) < 10) ? "0" + Integer.toString(calendar.get(Calendar.MINUTE)) : Integer.toString(calendar.get(Calendar.MINUTE))));
		String second_string = ((calendar.get(Calendar.SECOND) == 0) ? "00" : ((calendar.get(Calendar.SECOND) < 10) ? "0" + Integer.toString(calendar.get(Calendar.SECOND)) : Integer.toString(calendar.get(Calendar.SECOND))));

		String string_date = year_string + "/" + month_string + "/" + date_string + " " + hour_string + ":" + minute_string + ":" + second_string;

		String message = "Latitude:" + latitude + "\nLongitude:" + longitude + "\nAltitude:" + altitude + "\nAccuracy:" + accuracy + "\nBearing:" + bearing + "\nSpeed(m/s):" + Float.toString(speedms) + "\nSpeed(Km/H):" + Float.toString(speedkmh)
				+ "\nTime:" + string_date;

		switch (move)
		{
			case STOP:
			{
				message = message + "\nMove Status:STOP";
				break;
			}
			case DEACCELERATE:
			{
				message = message + "\nMove Status:DEACCELERATE";
				break;
			}
			case ACCELERATE:
			{
				message = message + "\nMove Status:ACCELERATE";
				break;
			}
			case MOVE:
			{
				message = message + "\nMove Status:MOVE";
				break;
			}
		}

		mGpsStatusText.setText(message);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		mSpeed.refreshData();
	}

	@Override
	public void afterTextChanged(Editable s)
	{
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		mSpeed.refreshData();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
	}
}
