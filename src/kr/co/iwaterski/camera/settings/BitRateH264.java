package kr.co.iwaterski.camera.settings;

public enum BitRateH264
{
	SD(0, "1 Mbyte", "1000000"),
	DVD(1, "2.5 Mbyte", "2500000"),
	HD720(2, "4.5 Mbyte", "4500000"),
	HD1080i(3, "10 Mbyte", "10000000"),
	HD1080p(4, "20 Mbyte", "20000000");

	private final int mPosition;
	private final String mText;
	private final String mValue;

	BitRateH264(int position, String text, String value)
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
		String[] text_list = new String[BitRateH264.values().length];

		for (BitRateH264 item : BitRateH264.values())
		{
			text_list[num++] = item.mText;
		}

		return text_list;
	}

	static public BitRateH264 get(String value)
	{
		int num = Integer.valueOf(value);
		switch (num)
		{
			case 0:
				return BitRateH264.SD;
			case 1:
				return BitRateH264.DVD;
			case 2:
				return BitRateH264.HD720;
			case 3:
				return BitRateH264.HD1080i;
			default:
				return BitRateH264.HD1080p;
		}
	}
}
