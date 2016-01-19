package kr.co.iwaterski.camera.video;

import java.io.IOException;

import kr.co.iwaterski.camera.R;
import kr.co.iwaterski.camera.speedmanager.Speed;
import kr.co.iwaterski.camera.speedmanager.SpeedListener;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Messenger;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

public class Preview extends SurfaceView implements SurfaceHolder.Callback, SpeedListener
{
	private Context mContext;
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera = null;
	boolean mRecordingStatus = false;
	private Recorder mRecorder;
	private Speed mSpeed;
	private RecordingTimer mRecordingTimer;
	int mRecordingCount = 0;
	Messenger mClient;

	private enum RecordingState
	{
		STOP,
		PAUSE,
		RECORDING,
	};

	RecordingState mRecordingState = RecordingState.STOP;

	public Preview(Context context)
	{
		super(context);
		mContext = context;

		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
	}

	private void getCamera()
	{
		try
		{
			mCamera = Camera.open();
			Camera.Parameters parameter = mCamera.getParameters();
			parameter.setFocusMode(Parameters.FOCUS_MODE_AUTO);
			mCamera.setParameters(parameter);
		}
		catch (Exception e)
		{
			Toast.makeText(mContext, "Error setting camera preview: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void surfaceCreated(SurfaceHolder holder)
	{
		try
		{
			getCamera();
			mRecorder = new Recorder(mContext, mCamera, mSurfaceHolder);
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		}
		catch (IOException e)
		{
			Toast.makeText(mContext, "Error setting camera preview: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
				mSpeed.stopChecking();
				mSpeed.deleteEventListener(this);
			}
		}
		catch (Exception e)
		{
			Toast.makeText(mContext, "Error setting camera preview: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
			Toast.makeText(mContext, "Error setting camera preview: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		try
		{
			mCamera.setPreviewDisplay(mSurfaceHolder);
			mCamera.startPreview();
		}
		catch (Exception e)
		{
			Toast.makeText(mContext, "Error setting camera preview: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void setAutoMode(boolean is_auto)
	{
		if (is_auto == true)
		{
			mSpeed = new Speed(mContext);
			mSpeed.addEventListener(this);
			mSpeed.startChecking();
		}
		else
		{
			if (mSpeed != null)
			{
				mSpeed.stopChecking();
				mSpeed.deleteEventListener(this);
			}
		}
	}

	public void start()
	{
		if (mRecordingState == RecordingState.STOP)
		{
			mRecordingCount = 0;
			mRecordingState = RecordingState.RECORDING;
			//mRecorder.start();
			mRecordingTimer = new RecordingTimer();
			mRecordingTimer.execute(((TextView) ((Activity) mContext).findViewById(R.id.rec_time_text)));
		}
		else if (mRecordingState == RecordingState.PAUSE)
		{
			mRecordingState = RecordingState.RECORDING;
			//mRecorder.start();
			mRecordingTimer = new RecordingTimer();
			mRecordingTimer.execute(((TextView) ((Activity) mContext).findViewById(R.id.rec_time_text)));
		}
	}

	public void stop()
	{
		if (mRecordingState != RecordingState.STOP)
		{
			mRecordingState = RecordingState.STOP;
			mRecorder.stop();
			mRecordingTimer.stop();
		}
	}

	@Override
	public void onMoveChangeEvent(Move move, Location location)
	{
		switch (move)
		{
			case STOP:
			{
				stop();
				break;
			}
			case DEACCELERATE:
			{
				break;
			}
			case ACCELERATE:
			{
				break;
			}
			case MOVE:
			{
				start();
				break;
			}
		}
	}

	private class RecordingTimer extends AsyncTask<TextView, Void, Void>
	{
		TextView mTextView;
		boolean mRun = true;

		@Override
		protected Void doInBackground(TextView... param)
		{
			mTextView = param[0];
			while (mRun)
			{
				try
				{
					Thread.sleep(1000);
					mRecordingCount++;
					publishProgress();
				}
				catch (Exception e)
				{

				}
			}

			return null;
		}

		public void stop()
		{
			mRun = false;
		}

		@Override
		protected void onProgressUpdate(Void... values)
		{
			int min = mRecordingCount / 60;
			int sec = mRecordingCount % 60;

			String minute_string = ((min == 0) ? "00" : ((min < 10) ? "0" + Integer.toString(min) : Integer.toString(min)));
			String second_string = ((sec == 0) ? "00" : ((sec < 10) ? "0" + Integer.toString(sec) : Integer.toString(sec)));

			String time = minute_string + ":" + second_string;
			mTextView.setText(time);
			super.onProgressUpdate(values);
		}
	}
}
