package kr.co.iwaterski.camera.speedmanager;

import android.location.Location;

public interface SpeedListener
{
	public enum Move
	{
		STOP,
		DEACCELERATE,
		ACCELERATE,
		MOVE,
	};

	void onMoveChangeEvent(Move move, Location location);
}
