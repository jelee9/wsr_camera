package kr.co.iwaterski.camera.menu;

import kr.co.iwaterski.camera.R;
import kr.co.iwaterski.camera.settings.BitRateH264;
import kr.co.iwaterski.camera.settings.BitRateMpeg4;
import kr.co.iwaterski.camera.settings.Data;
import kr.co.iwaterski.camera.settings.Data.Item;
import kr.co.iwaterski.camera.settings.FrameRate;
import kr.co.iwaterski.camera.settings.MinRecordingDuration;
import kr.co.iwaterski.camera.settings.Resolution;
import kr.co.iwaterski.camera.settings.VideoFormat;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MenuVideoFragment extends Fragment implements OnItemSelectedListener
{
	Context mContext;
	Spinner mSpinnerMinRecordingDuration;
	Spinner mSpinnerVideoFormat;
	Spinner mSpinnerResolution;
	Spinner mSpinnerFrameRate;
	Spinner mSpinnerBitRate;

	MinRecordingDuration mMinRecordingDuration;
	VideoFormat mVideoFormat;
	Resolution mResolution;
	FrameRate mFrameRate;
	BitRateMpeg4 mBitRateMpeg4;
	BitRateH264 mBitRateH264;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.menu_video, container, false);
		
		mContext = getActivity();

		loadData();

		mSpinnerMinRecordingDuration = (Spinner) view.findViewById(R.id.camera_menu_min_recording_duration_spinner);
		mSpinnerVideoFormat = (Spinner) view.findViewById(R.id.camera_menu_video_format_spinner);
		mSpinnerResolution = (Spinner) view.findViewById(R.id.camera_menu_resolution_spinner);
		mSpinnerFrameRate = (Spinner) view.findViewById(R.id.camera_menu_framerate_spinner);
		mSpinnerBitRate = (Spinner) view.findViewById(R.id.camera_menu_bitrate_spinner);

		setMinRecordingDurationSpinner();
		setVideoFormatSpinner();
		setResolutionSpinner();
		setFrameRateSpinner();
		setBitRateSpinner();

		setPosition();

		//return super.onCreateView(inflater, container, savedInstanceState);
		return view;
	}

	private void setMinRecordingDurationSpinner()
	{
		ArrayAdapter<String> min_recording_duration_adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_40, MinRecordingDuration.getTextList());
		mSpinnerMinRecordingDuration.setAdapter(min_recording_duration_adapter);
		mSpinnerMinRecordingDuration.setOnItemSelectedListener(this);
	}

	private void setVideoFormatSpinner()
	{
		ArrayAdapter<String> video_format_adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_100, VideoFormat.getTextList());
		mSpinnerVideoFormat.setAdapter(video_format_adapter);
		mSpinnerVideoFormat.setOnItemSelectedListener(this);
	}

	private void setResolutionSpinner()
	{
		ArrayAdapter<String> resolution_adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_100, Resolution.getTextList());
		mSpinnerResolution.setAdapter(resolution_adapter);
		mSpinnerResolution.setOnItemSelectedListener(this);
	}

	private void setFrameRateSpinner()
	{
		ArrayAdapter<String> frame_rate_adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_40, FrameRate.getTextList());
		mSpinnerFrameRate.setAdapter(frame_rate_adapter);
		mSpinnerFrameRate.setOnItemSelectedListener(this);
	}

	private void setBitRateSpinner()
	{
		ArrayAdapter<String> bit_rate_adapter;

		if (mVideoFormat == VideoFormat.MPEG2)
		{
			bit_rate_adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_100, BitRateMpeg4.getTextList());
		}
		else
		{
			bit_rate_adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_100, BitRateH264.getTextList());
		}

		mSpinnerBitRate.setAdapter(bit_rate_adapter);
		mSpinnerBitRate.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		saveData();
		loadData();
		setBitRateSpinner();
		setPosition();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0)
	{

	}

	private void loadData()
	{
		mMinRecordingDuration = MinRecordingDuration.get(Data.getData(mContext, Item.VIDEO_MIN_RECORDING_DURATION));
		mVideoFormat = VideoFormat.get(Data.getData(mContext, Item.VIDEO_FORMAT));
		mResolution = Resolution.get(Data.getData(mContext, Item.VIDEO_RESOLUTION));
		mFrameRate = FrameRate.get(Data.getData(mContext, Item.VIDEO_FRAME_RATE));
		mBitRateMpeg4 = BitRateMpeg4.get(Data.getData(mContext, Item.VIDEO_BIT_RATE_MPEG4));
		mBitRateH264 = BitRateH264.get(Data.getData(mContext, Item.VIDEO_BIT_RATE_H264));
	}

	private void saveData()
	{
		Data.setData(mContext, Item.VIDEO_MIN_RECORDING_DURATION, String.valueOf(mSpinnerMinRecordingDuration.getSelectedItemPosition()));
		Data.setData(mContext, Item.VIDEO_FORMAT, String.valueOf(mSpinnerVideoFormat.getSelectedItemPosition()));
		Data.setData(mContext, Item.VIDEO_RESOLUTION, String.valueOf(mSpinnerResolution.getSelectedItemPosition()));
		Data.setData(mContext, Item.VIDEO_FRAME_RATE, String.valueOf(mSpinnerFrameRate.getSelectedItemPosition()));

		if (mVideoFormat == VideoFormat.MPEG2)
		{
			Data.setData(mContext, Item.VIDEO_BIT_RATE_MPEG4, String.valueOf(mSpinnerBitRate.getSelectedItemPosition()));
		}
		else
		{
			Data.setData(mContext, Item.VIDEO_BIT_RATE_H264, String.valueOf(mSpinnerBitRate.getSelectedItemPosition()));
		}
	}

	private void setPosition()
	{
		mSpinnerMinRecordingDuration.setSelection(mMinRecordingDuration.getPosition());
		mSpinnerVideoFormat.setSelection(mVideoFormat.getPosition());
		mSpinnerResolution.setSelection(mResolution.getPosition());
		mSpinnerFrameRate.setSelection(mFrameRate.getPosition());

		if (mVideoFormat == VideoFormat.MPEG2)
		{
			mSpinnerBitRate.setSelection(mBitRateMpeg4.getPosition());
		}
		else
		{
			mSpinnerBitRate.setSelection(mBitRateH264.getPosition());
		}
	}
}
