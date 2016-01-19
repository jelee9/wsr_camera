package kr.co.iwaterski.camera.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class Data
{
	static public enum Item
	{
		GPS_USING_BOUNDARY("true"),
		GPS_LATITUDE("37.4938672"),
		GPS_LONGITUDE("127.4742325"),
		GPS_BOUNDARY("10"),
		GPS_MIN_SPEED("10"),
		GPS_MIN_START_TIME("3"),
		GPS_MIN_STOP_TIME("3"),

		NETWORK_AP_NAME("IWATERSKI"),
		NETWORK_SERVER_ADDRESS_1("192"),
		NETWORK_SERVER_ADDRESS_2("168"),
		NETWORK_SERVER_ADDRESS_3("0"),
		NETWORK_SERVER_ADDRESS_4("1"),

		VIDEO_MIN_RECORDING_DURATION("11"),
		VIDEO_FORMAT("1"),
		VIDEO_RESOLUTION("0"),
		VIDEO_FRAME_RATE("30"),
		VIDEO_BIT_RATE_MPEG4("4"),
		VIDEO_BIT_RATE_H264("4");

		private final String mDefaultValue;

		Item(String default_value)
		{
			mDefaultValue = default_value;
		}

		String getDefaultValue()
		{
			return mDefaultValue;
		}
	}

	static public String getData(Context context, Item item)
	{
		return context.getSharedPreferences("kr.co.iwaterski.camera", Context.MODE_PRIVATE).getString(item.toString(), item.getDefaultValue());
	}

	static public void setData(Context context, Item item, String value)
	{
		SharedPreferences.Editor editor = context.getSharedPreferences("kr.co.iwaterski.camera", Context.MODE_PRIVATE).edit();

		editor.putString(item.toString(), value);

		editor.commit();
	}
}
