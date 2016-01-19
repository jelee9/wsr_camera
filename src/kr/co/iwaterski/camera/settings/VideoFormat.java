package kr.co.iwaterski.camera.settings;

import android.media.MediaRecorder;

public enum VideoFormat
{
	MPEG2(0, "MPEG2", MediaRecorder.VideoEncoder.MPEG_4_SP),
	H_264(1, "H.264", MediaRecorder.VideoEncoder.H264);

	private final int mPosition;
	private final String mText;
	private final int mVideoEncoder;

	VideoFormat(int position, String text, int video_encoder)
	{
		mPosition = position;
		mText = text;
		mVideoEncoder = video_encoder;
	}

	public int getPosition()
	{
		return mPosition;
	}

	public int getValue()
	{
		return mVideoEncoder;
	}

	static public String[] getTextList()
	{
		int num = 0;
		String[] text_list = new String[VideoFormat.values().length];

		for (VideoFormat item : VideoFormat.values())
		{
			text_list[num++] = item.mText;
		}

		return text_list;
	}

	static public VideoFormat get(String value)
	{
		int num = Integer.valueOf(value);
		switch (num)
		{
			case 0:
				return VideoFormat.MPEG2;
			default:
				return VideoFormat.H_264;
		}
	}
}
