package kr.co.iwaterski.camera.video;

import java.io.File;
import java.util.Calendar;

import kr.co.iwaterski.camera.settings.Data;
import kr.co.iwaterski.camera.settings.MinRecordingDuration;
import kr.co.iwaterski.camera.speedmanager.Speed;
import kr.co.iwaterski.camera.speedmanager.SpeedListener;
import android.content.Context;
import android.hardware.Camera;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.widget.Toast;

public class RecordManager implements SpeedListener
{
	static String SAVE_PATH = Environment.getExternalStorageDirectory() + "/waterski/movie/";

	public enum State
	{
		STOP,
		PAUSE,
		RESUME,
		START,
	};

	Context mContext;
	Recorder mRecorder;
	RecordManagerListener mListener;
	State mState = State.STOP;
	boolean mIsAuto = false;

	Speed mSpeed;
	long mStartTime = 0;

	String mFileName;

	public RecordManager(Context context, Camera camera, SurfaceHolder surfaceholder, RecordManagerListener listener)
	{
		mContext = context;
		mRecorder = new Recorder(context, camera, surfaceholder);
		mListener = listener;

		File save_directory = new File(SAVE_PATH);

		if (save_directory.exists() == false)
		{
			save_directory.mkdir();
		}
	}

	public void changeAutoMode(boolean is_auto)
	{
		if (is_auto == true)
		{
			if (mIsAuto == false)
			{
				mIsAuto = true;

				if (mState == State.START || mState == State.RESUME)
				{
					final SpeedListener speed_listener = this;

					new AsyncTask<Void, Void, Void>()
					{
						@Override
						protected Void doInBackground(Void... params)
						{
							mRecorder.stop();
							return null;
						}

						@Override
						protected void onPostExecute(Void result)
						{
							checkMinDuration();
							mListener.handler(RecordManagerListener.VALUE.STATE_AUTO_STOP);

							mSpeed = new Speed(mContext);
							mSpeed.addEventListener(speed_listener);
							mSpeed.startChecking();

							mListener.handler(RecordManagerListener.VALUE.RESULT_SUCCESS_AUTO_ENABLE);
							super.onPostExecute(result);
						}
					}.execute();
				}
				else
				{
					mSpeed = new Speed(mContext);
					mSpeed.addEventListener(this);
					mSpeed.startChecking();

					mListener.handler(RecordManagerListener.VALUE.RESULT_SUCCESS_AUTO_ENABLE);
				}
			}
			else
			{
				mListener.handler(RecordManagerListener.VALUE.RESULT_FAIL_AUTO_ENABLE);
			}
		}
		else
		{
			if (mIsAuto == true)
			{
				if (mState == State.START || mState == State.RESUME)
				{
					final SpeedListener speed_listener = this;

					new AsyncTask<Void, Void, Void>()
					{
						@Override
						protected Void doInBackground(Void... params)
						{
							mRecorder.stop();
							return null;
						}

						@Override
						protected void onPostExecute(Void result)
						{
							mState = State.STOP;

							checkMinDuration();
							mListener.handler(RecordManagerListener.VALUE.STATE_AUTO_STOP);

							if (mSpeed != null)
							{
								mSpeed.stopChecking();
								mSpeed.deleteEventListener(speed_listener);
							}

							mIsAuto = false;

							mListener.handler(RecordManagerListener.VALUE.RESULT_SUCCESS_AUTO_DISABLE);
							super.onPostExecute(result);
						}
					}.execute();
				}
				else
				{
					if (mSpeed != null)
					{
						mSpeed.stopChecking();
						mSpeed.deleteEventListener(this);
					}

					mIsAuto = false;

					mListener.handler(RecordManagerListener.VALUE.RESULT_SUCCESS_AUTO_DISABLE);
				}
			}
			else
			{
				mListener.handler(RecordManagerListener.VALUE.RESULT_FAIL_AUTO_DISABLE);
			}
		}
	}

	public void start()
	{
		if (mState == State.STOP)
		{
			new AsyncTask<Void, Void, Boolean>()
			{

				@Override
				protected void onPreExecute()
				{
					mStartTime = Calendar.getInstance().getTimeInMillis();
					mFileName = SAVE_PATH + get_date_string(mStartTime) + ".mp4";
					super.onPreExecute();
				}

				@Override
				protected Boolean doInBackground(Void... params)
				{
					return  mRecorder.start(mFileName);
				}

				@Override
				protected void onPostExecute(Boolean result)
				{
					if (result == true)
					{
						mState = State.START;

						if (mIsAuto == true)
						{
							mListener.handler(RecordManagerListener.VALUE.STATE_AUTO_START);
						}
						else
						{
							mListener.handler(RecordManagerListener.VALUE.RESULT_SUCCESS_START);
						}
					}
					else
					{
						if (mIsAuto == true)
						{
							// Do nothing
						}
						else
						{
							mListener.handler(RecordManagerListener.VALUE.RESULT_FAIL_START);
						}
					}

					super.onPostExecute(result);
				}
			}.execute();
		}
		else
		{
			if (mIsAuto != true)
			{
				mListener.handler(RecordManagerListener.VALUE.RESULT_FAIL_START);
			}
		}
	}

	public void stop()
	{
		if (mState == State.START || mState == State.RESUME)
		{
			new AsyncTask<Void, Void, Boolean>()
			{
				@Override
				protected Boolean doInBackground(Void... params)
				{
					return mRecorder.stop();
				}

				@Override
				protected void onPostExecute(Boolean result)
				{
					if (result == true)
					{
						mState = State.STOP;

						checkMinDuration();
						if (mIsAuto == true)
						{
							mListener.handler(RecordManagerListener.VALUE.STATE_AUTO_STOP);
						}
						else
						{
							mListener.handler(RecordManagerListener.VALUE.RESULT_SUCCESS_STOP);
						}
					}
					else
					{
						if (mIsAuto == true)
						{
							// Do nothing
						}
						else
						{
							mListener.handler(RecordManagerListener.VALUE.RESULT_FAIL_STOP);
						}
					}

					super.onPostExecute(result);
				}
			}.execute();
		}
		else
		{
			if (mIsAuto != true)
			{
				mListener.handler(RecordManagerListener.VALUE.RESULT_FAIL_STOP);
			}
		}
	}

	private String get_date_string(long time)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		String year_string = Integer.toString(calendar.get(Calendar.YEAR));
		String month_string = (calendar.get(Calendar.MONTH) + 1 >= 10) ? Integer.toString(calendar.get(Calendar.MONTH) + 1) : "0" + Integer.toString(calendar.get(Calendar.MONTH) + 1);
		String date_string = (calendar.get(Calendar.DATE) >= 10) ? Integer.toString(calendar.get(Calendar.DATE)) : "0" + Integer.toString(calendar.get(Calendar.DATE));
		String hour_string = ((calendar.get(Calendar.HOUR_OF_DAY) == 0) ? "00" : ((calendar.get(Calendar.HOUR_OF_DAY) < 10) ? "0" + Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)) : Integer.toString(calendar.get(Calendar.HOUR_OF_DAY))));
		String minute_string = ((calendar.get(Calendar.MINUTE) == 0) ? "00" : ((calendar.get(Calendar.MINUTE) < 10) ? "0" + Integer.toString(calendar.get(Calendar.MINUTE)) : Integer.toString(calendar.get(Calendar.MINUTE))));
		String second_string = ((calendar.get(Calendar.SECOND) == 0) ? "00" : ((calendar.get(Calendar.SECOND) < 10) ? "0" + Integer.toString(calendar.get(Calendar.SECOND)) : Integer.toString(calendar.get(Calendar.SECOND))));
		return year_string + month_string + date_string + "_" + hour_string + minute_string + second_string;
	}

	private void checkMinDuration()
	{
		long end_time = Calendar.getInstance().getTimeInMillis();

		MinRecordingDuration min_recording_duration = MinRecordingDuration.get(Data.getData(mContext, Data.Item.VIDEO_MIN_RECORDING_DURATION));

		if (end_time - mStartTime < (min_recording_duration.getValue() * 1000))
		{
			File delete_file = new File(mFileName);

			if (delete_file.delete() == false)
			{
				Toast.makeText(mContext, "Can not delete short-timem recorded file", Toast.LENGTH_LONG).show();
			}
		}
	}

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
}
