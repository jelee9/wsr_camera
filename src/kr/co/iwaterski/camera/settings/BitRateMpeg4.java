package kr.co.iwaterski.camera.settings;

public enum BitRateMpeg4
{
	SD(0, "3.5 Mbyte", "3500000"),
	DVD(1, "9.8 Mbyte", "9800000"),
	HD720(2, "19Mbyte", "19000000"),
	HD1080i(3, "25 Mbyte", "25000000"),
	HD1080p(4, "40 Mbyte", "40000000");

	private final int mPosition;
	private final String mText;
	private final String mValue;

	BitRateMpeg4(int position, String text, String value)
	{
		mPosition = position;
		mText = text;
		mValue = value;
	}

	public int getPosition()
	{
		return mPosition;
	}

	public int getValue()
	{
		return Integer.valueOf(mValue);
	}

	static public String[] getTextList()
	{
		int num = 0;
		String[] text_list = new String[BitRateMpeg4.values().length];

		for (BitRateMpeg4 item : BitRateMpeg4.values())
		{
			text_list[num++] = item.mText;
		}

		return text_list;
	}

	static public BitRateMpeg4 get(String value)
	{
		int num = Integer.valueOf(value);
		switch (num)
		{
			case 0:
				return BitRateMpeg4.SD;
			case 1:
				return BitRateMpeg4.DVD;
			case 2:
				return BitRateMpeg4.HD720;
			case 3:
				return BitRateMpeg4.HD1080i;
			default:
				return BitRateMpeg4.HD1080p;
		}
	}
}
