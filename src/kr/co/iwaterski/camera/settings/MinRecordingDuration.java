package kr.co.iwaterski.camera.settings;

public enum MinRecordingDuration
{
	MINUTE_5(0, "5"),
	MINUTE_10(1, "10"),
	MINUTE_15(2, "15"),
	MINUTE_20(3, "20"),
	MINUTE_25(4, "25"),
	MINUTE_30(5, "30"),
	MINUTE_35(6, "35"),
	MINUTE_40(7, "40"),
	MINUTE_45(8, "45"),
	MINUTE_50(9, "50"),
	MINUTE_55(10, "55"),
	MINUTE_60(11, "60");

	private final int mPosition;
	private final String mText;

	MinRecordingDuration(int position, String text)
	{
		mPosition = position;
		mText = text;
	}

	public int getPosition()
	{
		return mPosition;
	}

	public int getValue()
	{
		return Integer.parseInt(mText);
	}

	static public String[] getTextList()
	{
		int num = 0;
		String[] text_list = new String[MinRecordingDuration.values().length];

		for (MinRecordingDuration item : MinRecordingDuration.values())
		{
			text_list[num++] = item.mText;
		}

		return text_list;
	}

	static public MinRecordingDuration get(String value)
	{
		int num = Integer.valueOf(value);
		switch (num)
		{
			case 0:
				return MinRecordingDuration.MINUTE_5;
			case 1:
				return MinRecordingDuration.MINUTE_10;
			case 2:
				return MinRecordingDuration.MINUTE_15;
			case 3:
				return MinRecordingDuration.MINUTE_20;
			case 4:
				return MinRecordingDuration.MINUTE_25;
			case 5:
				return MinRecordingDuration.MINUTE_30;
			case 6:
				return MinRecordingDuration.MINUTE_35;
			case 7:
				return MinRecordingDuration.MINUTE_40;
			case 8:
				return MinRecordingDuration.MINUTE_45;
			case 9:
				return MinRecordingDuration.MINUTE_50;
			case 10:
				return MinRecordingDuration.MINUTE_55;	
			default:
				return MinRecordingDuration.MINUTE_60;
		}
	}
}
