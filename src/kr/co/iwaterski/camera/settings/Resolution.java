package kr.co.iwaterski.camera.settings;

public enum Resolution
{
	HD720(0, "1280x720", 1280, 720),
	HD1080(1, "1920x1080", 1920, 1080);

	private final int mPosition;
	private final String mText;
	private final int mWidth;
	private final int mHeight;

	Resolution(int position, String text, int width, int height)
	{
		mPosition = position;
		mText = text;
		mWidth = width;
		mHeight = height;
	}

	public int getPosition()
	{
		return mPosition;
	}

	public int getWidth()
	{
		return mWidth;
	}

	public int getHeight()
	{
		return mHeight;
	}

	static public String[] getTextList()
	{
		int num = 0;
		String[] text_list = new String[Resolution.values().length];

		for (Resolution item : Resolution.values())
		{
			text_list[num++] = item.mText;
		}

		return text_list;
	}

	static public Resolution get(String value)
	{
		int num = Integer.valueOf(value);
		switch (num)
		{
			case 0:
				return Resolution.HD720;
			default:
				return Resolution.HD1080;
		}
	}
}
