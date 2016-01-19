package kr.co.iwaterski.camera.video;

import kr.co.iwaterski.camera.settings.BitRateH264;
import kr.co.iwaterski.camera.settings.BitRateMpeg4;
import kr.co.iwaterski.camera.settings.Data;
import kr.co.iwaterski.camera.settings.FrameRate;
import kr.co.iwaterski.camera.settings.Resolution;
import kr.co.iwaterski.camera.settings.VideoFormat;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaRecorder;
import android.view.SurfaceHolder;
import android.widget.Toast;

public class Recorder
{
	Context mContext;
	private MediaRecorder mMediaRecorder = null;
	private Camera mCamera;
	private SurfaceHolder mSurfaceHolder;

	public Recorder(Context context, Camera camera, SurfaceHolder surfaceholder)
	{
		mContext = context;
		mCamera = camera;
		mSurfaceHolder = surfaceholder;
	}

	private void init(String file_name)
	{
		VideoFormat video_format = VideoFormat.get(Data.getData(mContext, Data.Item.VIDEO_FORMAT));
		Resolution resolution = Resolution.get(Data.getData(mContext, Data.Item.VIDEO_RESOLUTION));
		FrameRate frame_rate = FrameRate.get(Data.getData(mContext, Data.Item.VIDEO_FRAME_RATE));
		BitRateH264 bit_rate_h264 = BitRateH264.get(Data.getData(mContext, Data.Item.VIDEO_BIT_RATE_H264));
		BitRateMpeg4 bit_rate_mpeg4 = BitRateMpeg4.get(Data.getData(mContext, Data.Item.VIDEO_BIT_RATE_MPEG4));

		try
		{
			if (mMediaRecorder == null)
			{
				mMediaRecorder = new MediaRecorder();

				Camera.Parameters parameter = mCamera.getParameters();

				if (parameter.isVideoStabilizationSupported() == true)
				{
					parameter.setVideoStabilization(true);
				}
				parameter.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

				mCamera.setParameters(parameter);

				mCamera.unlock();

				mMediaRecorder.setCamera(mCamera);
				mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
				mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
				mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
				// mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
				mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
				mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
				mMediaRecorder.setVideoEncoder(video_format.getValue());
				mMediaRecorder.setOutputFile(file_name);

				mMediaRecorder.setMaxDuration(0);
				mMediaRecorder.setMaxFileSize(0);

				mMediaRecorder.setAudioSamplingRate(48000);
				mMediaRecorder.setAudioEncodingBitRate(128000);

				mMediaRecorder.setVideoSize(resolution.getWidth(), resolution.getHeight());
				mMediaRecorder.setVideoFrameRate(frame_rate.getValue());

				if (video_format == VideoFormat.H_264)
				{
					mMediaRecorder.setVideoEncodingBitRate(bit_rate_h264.getValue());
				}
				else
				{
					mMediaRecorder.setVideoEncodingBitRate(bit_rate_mpeg4.getValue());
				}
			}
		}
		catch (Exception e)
		{
			Toast.makeText(mContext, "Error setting camera preview: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public boolean start(String file_name)
	{
		try
		{
			init(file_name);
			mMediaRecorder.prepare();
			mMediaRecorder.start();
			return true;
		}
		catch (Exception e)
		{
			Toast.makeText(mContext, "Error setting camera preview: " + e.getMessage(), Toast.LENGTH_LONG).show();
			return false;
		}
	}

	public boolean stop()
	{
		try
		{
			mMediaRecorder.stop();
			mMediaRecorder.reset();
			mMediaRecorder = null;
			mCamera.lock();
			return true;
		}
		catch (Exception e)
		{
			Toast.makeText(mContext, "Error setting camera preview: " + e.getMessage(), Toast.LENGTH_LONG).show();
			return false;
		}
	}
}
