package kr.co.iwaterski.camera.settings;

public enum FrameRate
{
	F5(0, "5"),
	F10(1, "10"),
	F15(2, "15"),
	F20(3, "20"),
	F25(4, "25"),
	F30(5, "30");

	private final int mPosition;
	private final String mText;

	FrameRate(int position, String text)
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
		return Integer.valueOf(mText);
	}

	static public String[] getTextList()
	{
		int num = 0;
		String[] text_list = new String[FrameRate.values().length];

		for (FrameRate item : FrameRate.values())
		{
			text_list[num++] = item.mText;
		}

		return text_list;
	}

	static public FrameRate get(String value)
	{
		int num = Integer.valueOf(value);
		switch (num)
		{
			case 0:
				return FrameRate.F5;
			case 1:
				return FrameRate.F10;
			case 2:
				return FrameRate.F15;
			case 3:
				return FrameRate.F20;
			case 4:
				return FrameRate.F25;
			default:
				return FrameRate.F30;
		}
	}
}
