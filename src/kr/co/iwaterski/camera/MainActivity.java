package kr.co.iwaterski.camera;

import java.io.IOException;
import java.util.Calendar;

import kr.co.iwaterski.camera.menu.MenuActivity;
import kr.co.iwaterski.camera.speedmanager.Speed;
import kr.co.iwaterski.camera.speedmanager.SpeedListener;
import kr.co.iwaterski.camera.video.RecordManager;
import kr.co.iwaterski.camera.video.RecordManagerListener;
import kr.co.iwaterski.camera.wifimanager.Wifi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, SurfaceHolder.Callback, RecordManagerListener, SpeedListener
{
	SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera = null;

	TextView mTextViewRec;
	TextView mTextViewTime;

	TextView mTextViewLog;

	Button mButtonRecord;
	Button mButtonStop;
	Button mButtonAuto;

	RecordManager mRecordManager;

	boolean mAutoState = false;
	
	Speed mSpeed;
	RecordManagerListener.VALUE mValue = RecordManagerListener.VALUE.RESULT_SUCCESS_STOP;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Remove Titlebar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Remove Statusbar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		setContentView(R.layout.activity_main);

		mTextViewRec = (TextView) findViewById(R.id.rec_state_text);
		mTextViewTime = (TextView) findViewById(R.id.rec_time_text);

		mTextViewLog = (TextView) findViewById(R.id.debug_log_text);

		mButtonRecord = (Button) findViewById(R.id.btn_rec);
		mButtonStop = (Button) findViewById(R.id.btn_stop);
		mButtonAuto = (Button) findViewById(R.id.btn_auto);

		mButtonAuto.setBackgroundResource(R.drawable.btn_auto);

		mButtonRecord.setOnClickListener(this);
		mButtonStop.setOnClickListener(this);
		mButtonAuto.setOnClickListener(this);

		Wifi.getInstance();

		// Create an instance of Camera

		// Create our Preview view and set it as the content of our activity.
		FrameLayout preview = (FrameLayout) findViewById(R.id.preview);

		mSurfaceView = new SurfaceView(this);
		preview.addView(mSurfaceView);

		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		
		

		// Test();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		startActivity(new Intent(this, MenuActivity.class));
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void surfaceCreated(SurfaceHolder holder)
	{
		try
		{
			mCamera = Camera.open();
			Camera.Parameters parameter = mCamera.getParameters();
			parameter.setFocusMode(Parameters.FOCUS_MODE_AUTO);
			mCamera.setParameters(parameter);

			mRecordManager = new RecordManager(this, mCamera, mSurfaceHolder, this);
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
			
			mSpeed = new Speed(this);
			mSpeed.addEventListener(this);
			mSpeed.startChecking();
		}
		catch (IOException e)
		{
			Toast.makeText(this, "Error setting camera preview: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder)
	{
		try
		{
			mCamera.stopPreview();
			mCamera.release();
			
			if (mSpeed != null)
			{
				mSpeed.deleteEventListener(this);
				mSpeed.stopChecking();
			}
		}
		catch (Exception e)
		{
			Toast.makeText(this, "Error setting camera preview: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
	{
		if (mSurfaceHolder.getSurface() == null)
		{
			return;
		}

		try
		{
			mCamera.stopPreview();
		}
		catch (Exception e)
		{
			Toast.makeText(this, "Error setting camera preview: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		try
		{
			mCamera.setPreviewDisplay(mSurfaceHolder);
			mCamera.startPreview();
		}
		catch (Exception e)
		{
			Toast.makeText(this, "Error setting camera preview: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_rec:
			{

				mButtonRecord.setClickable(false);
				mButtonAuto.setClickable(false);
				mButtonRecord.setBackgroundResource(R.drawable.btn_rec_pressed);

				mRecordManager.start();

				break;
			}
			case R.id.btn_stop:
			{
				mButtonStop.setClickable(false);
				mButtonStop.setBackgroundResource(R.drawable.btn_stop_pressed);

				mRecordManager.stop();

				break;
			}
			case R.id.btn_auto:
			{
				mAutoState = !mAutoState;

				if (mAutoState == true)
				{
					mButtonRecord.setClickable(false);
					mButtonAuto.setClickable(false);
					mButtonAuto.setBackgroundResource(R.drawable.btn_auto_pressed);
				}
				else
				{
					mButtonAuto.setClickable(false);
					mButtonAuto.setBackgroundResource(R.drawable.btn_auto);
				}
				
				mRecordManager.changeAutoMode(mAutoState);
				break;
			}
		}
	}

	public void handler(VALUE value)
	{
		mValue = value;
		
		switch (value)
		{
			case RESULT_SUCCESS_START:
			{
				mTextViewRec.setVisibility(View.VISIBLE);
				mTextViewTime.setVisibility(View.VISIBLE);

				mButtonRecord.setBackgroundResource(R.drawable.btn_rec);
				mButtonRecord.setVisibility(View.GONE);
				mButtonStop.setVisibility(View.VISIBLE);
				mButtonAuto.setVisibility(View.GONE);

				mButtonRecord.setClickable(true);
				mButtonAuto.setClickable(true);
				break;
			}
			case RESULT_FAIL_START:
			{
				mButtonRecord.setClickable(true);
				mButtonAuto.setClickable(true);
				mButtonRecord.setBackgroundResource(R.drawable.btn_rec);
				break;
			}
			case RESULT_SUCCESS_STOP:
			{
				mTextViewRec.setVisibility(View.INVISIBLE);
				mTextViewTime.setVisibility(View.INVISIBLE);
				mButtonStop.setBackgroundResource(R.drawable.btn_stop);

				mButtonRecord.setVisibility(View.VISIBLE);
				mButtonStop.setVisibility(View.GONE);
				mButtonAuto.setVisibility(View.VISIBLE);

				mButtonStop.setClickable(true);
				break;
			}
			case RESULT_FAIL_STOP:
			{
				mButtonStop.setClickable(true);
				mButtonStop.setBackgroundResource(R.drawable.btn_stop);
				break;
			}
			case RESULT_SUCCESS_AUTO_ENABLE:
			{
				mButtonRecord.setVisibility(View.GONE);
				mButtonStop.setVisibility(View.GONE);

				mButtonRecord.setClickable(true);
				mButtonAuto.setClickable(true);
				break;
			}
			case RESULT_SUCCESS_AUTO_DISABLE:
			{
				mButtonRecord.setVisibility(View.VISIBLE);
				mButtonStop.setVisibility(View.GONE);
				
				mButtonAuto.setClickable(true);
				break;
			}
			case STATE_AUTO_START:
			{
				mTextViewRec.setVisibility(View.VISIBLE);
				mTextViewTime.setVisibility(View.VISIBLE);
				break;
			}
			case STATE_AUTO_STOP:
			{
				mTextViewRec.setVisibility(View.GONE);
				mTextViewTime.setVisibility(View.GONE);
				break;
			}
			default:
			{
				break;
			}
		}
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

		String message = "\n\n\n\n\n\n\nLatitude:" + latitude + "\nLongitude:" + longitude + "\nAltitude:" + altitude + "\nAccuracy:" + accuracy + "\nBearing:" + bearing + "\nSpeed(m/s):" + Float.toString(speedms) + "\nSpeed(Km/H):" + Float.toString(speedkmh)
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
		
		message = message + "\n" + mValue.toString();

		mTextViewLog.setText(message);		
	}
}
