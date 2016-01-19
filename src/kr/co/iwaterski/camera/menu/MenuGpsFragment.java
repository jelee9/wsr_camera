package kr.co.iwaterski.camera.menu;

import kr.co.iwaterski.camera.R;
import kr.co.iwaterski.camera.settings.Data;
import kr.co.iwaterski.camera.settings.Data.Item;
import kr.co.iwaterski.camera.speedmanager.Speed;
import kr.co.iwaterski.camera.speedmanager.SpeedListener;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class MenuGpsFragment extends Fragment implements OnItemSelectedListener, TextWatcher
{
	Context mContext;
	Speed mSpeed = null;

	CheckBox mUsingBoundaryCheckBox;
	EditText mLatitudeEditText;
	EditText mLongitudeEditText;
	Button mCurrentLocationButton;
	Spinner mBoundarySpinner;
	Spinner mMinSpeedSpinner;
	Spinner mMinStartTimeSpinner;
	Spinner mMinStopTimeSpinner;
	EditText mApNameEditText;
	EditText mServerAddressEditText1;
	EditText mServerAddressEditText2;
	EditText mServerAddressEditText3;
	EditText mServerAddressEditText4;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		String[] boundary_selector = { "0", "10", "20", "30", "30", "40", "50", "60", "70", "80", "90", "100", "110", "120", "130", "140", "150", "160", "170", "180", "190", "200", "210", "220", "230", "240", "250", "260", "270", "280", "290",
				"300", "310", "320", "330", "340", "350", "360", "370", "380", "390", "400", "410", "420", "430", "440", "450", "460", "470", "480", "490", "500" };
		String[] speed_selector = { "0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "65", "70", "75", "80", "85", "90", "95" };
		String[] start_stop_time_selector = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19" };

		View view = inflater.inflate(R.layout.menu_gps, container, false);

		mContext = getActivity();

		mUsingBoundaryCheckBox = (CheckBox) view.findViewById(R.id.using_boundary_check);
		mLatitudeEditText = (EditText) view.findViewById(R.id.latitude);
		mLatitudeEditText.addTextChangedListener(this);
		mLongitudeEditText = (EditText) view.findViewById(R.id.longitude);
		mLongitudeEditText.addTextChangedListener(this);
		
		mUsingBoundaryCheckBox.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v)
			{
				if(mUsingBoundaryCheckBox.isChecked())
				{
					mLatitudeEditText.setEnabled(true);
					mLongitudeEditText.setEnabled(true);
					mCurrentLocationButton.setEnabled(true);
					mBoundarySpinner.setEnabled(true);
				}
				else
				{
					mLatitudeEditText.setEnabled(false);
					mLongitudeEditText.setEnabled(false);
					mCurrentLocationButton.setEnabled(false);
					mBoundarySpinner.setEnabled(false);
				}
			}
		});

		mCurrentLocationButton = (Button) view.findViewById(R.id.set_current_location);
		mCurrentLocationButton.setOnClickListener(new android.view.View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Speed speed = new Speed(mContext);
				speed.startChecking();
				speed.addEventListener(new SpeedListener()
				{

					@Override
					public void onMoveChangeEvent(Move move, Location location)
					{
						double latitude = location.getLatitude();
						double longitude = location.getLongitude();

						mLatitudeEditText.setText(Double.toString(latitude));
						mLongitudeEditText.setText(Double.toString(longitude));

						speed.deleteEventListener(this);
						speed.stopChecking();
					}
				});
			}
		});

		mBoundarySpinner = (Spinner) view.findViewById(R.id.boundary_spinner);
		ArrayAdapter<String> boundary_adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_60, boundary_selector);
		mBoundarySpinner.setAdapter(boundary_adapter);
		mBoundarySpinner.setOnItemSelectedListener(this);

		mMinSpeedSpinner = (Spinner) view.findViewById(R.id.min_speed_spinner);
		ArrayAdapter<String> min_speed_adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_40, speed_selector);
		mMinSpeedSpinner.setAdapter(min_speed_adapter);
		mMinSpeedSpinner.setOnItemSelectedListener(this);

		mMinStartTimeSpinner = (Spinner) view.findViewById(R.id.min_start_time_spinner);
		ArrayAdapter<String> min_start_time_adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_40, start_stop_time_selector);
		mMinStartTimeSpinner.setAdapter(min_start_time_adapter);
		mMinStartTimeSpinner.setOnItemSelectedListener(this);

		mMinStopTimeSpinner = (Spinner) view.findViewById(R.id.min_stop_time_spinner);
		ArrayAdapter<String> min_stop_time_adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_40, start_stop_time_selector);
		mMinStopTimeSpinner.setAdapter(min_stop_time_adapter);
		mMinStopTimeSpinner.setOnItemSelectedListener(this);

		mApNameEditText = (EditText) view.findViewById(R.id.ap_name);
		mApNameEditText.addTextChangedListener(this);

		mServerAddressEditText1 = (EditText) view.findViewById(R.id.server_address1);
		mServerAddressEditText2 = (EditText) view.findViewById(R.id.server_address2);
		mServerAddressEditText3 = (EditText) view.findViewById(R.id.server_address3);
		mServerAddressEditText4 = (EditText) view.findViewById(R.id.server_address4);
		mServerAddressEditText1.addTextChangedListener(this);
		mServerAddressEditText2.addTextChangedListener(this);
		mServerAddressEditText3.addTextChangedListener(this);
		mServerAddressEditText4.addTextChangedListener(this);

		setIpFilter();

		// return super.onCreateView(inflater, container, savedInstanceState);
		return view;
	}

	@Override
	public void onPause()
	{
		saveData();
		super.onPause();
	}

	@Override
	public void onStart()
	{
		loadData();
		super.onStart();
	}

	private void loadData()
	{
		boolean using_boundary = Boolean.parseBoolean(Data.getData(mContext, Item.GPS_USING_BOUNDARY));
		String latitude = Data.getData(mContext, Item.GPS_LATITUDE);
		String longitude = Data.getData(mContext, Item.GPS_LONGITUDE);
		int boundary = Integer.valueOf(Data.getData(mContext, Item.GPS_BOUNDARY));
		int min_speed = Integer.valueOf(Data.getData(mContext, Item.GPS_MIN_SPEED));
		int min_start_time = Integer.valueOf(Data.getData(mContext, Item.GPS_MIN_START_TIME));
		int min_stop_time = Integer.valueOf(Data.getData(mContext, Item.GPS_MIN_STOP_TIME));
		String ap_name = Data.getData(mContext, Item.NETWORK_AP_NAME);
		String server_address_1 = Data.getData(mContext, Item.NETWORK_SERVER_ADDRESS_1);
		String server_address_2 = Data.getData(mContext, Item.NETWORK_SERVER_ADDRESS_2);
		String server_address_3 = Data.getData(mContext, Item.NETWORK_SERVER_ADDRESS_3);
		String server_address_4 = Data.getData(mContext, Item.NETWORK_SERVER_ADDRESS_4);

		mUsingBoundaryCheckBox.setChecked(using_boundary);
		mLatitudeEditText.setText(latitude);
		mLongitudeEditText.setText(longitude);
		mBoundarySpinner.setSelection(boundary);
		mMinSpeedSpinner.setSelection(min_speed);
		mMinStartTimeSpinner.setSelection(min_start_time);
		mMinStopTimeSpinner.setSelection(min_stop_time);
		mApNameEditText.setText(ap_name);
		mServerAddressEditText1.setText(server_address_1);
		mServerAddressEditText2.setText(server_address_2);
		mServerAddressEditText3.setText(server_address_3);
		mServerAddressEditText4.setText(server_address_4);
		
		if(using_boundary == false)
		{
			mLatitudeEditText.setEnabled(false);
			mLongitudeEditText.setEnabled(false);
			mCurrentLocationButton.setEnabled(false);
			mBoundarySpinner.setEnabled(false);
		}
	}

	private void saveData()
	{
		Data.setData(mContext, Item.GPS_USING_BOUNDARY, Boolean.toString(mUsingBoundaryCheckBox.isChecked()));
		Data.setData(mContext, Item.GPS_LATITUDE, mLatitudeEditText.getText().toString());
		Data.setData(mContext, Item.GPS_LONGITUDE, mLongitudeEditText.getText().toString());
		Data.setData(mContext, Item.GPS_BOUNDARY, Integer.toString(mBoundarySpinner.getSelectedItemPosition()));
		Data.setData(mContext, Item.GPS_MIN_SPEED, Integer.toString(mMinSpeedSpinner.getSelectedItemPosition()));
		Data.setData(mContext, Item.GPS_MIN_START_TIME, Integer.toString(mMinStartTimeSpinner.getSelectedItemPosition()));
		Data.setData(mContext, Item.GPS_MIN_STOP_TIME, Integer.toString(mMinStopTimeSpinner.getSelectedItemPosition()));
		Data.setData(mContext, Item.NETWORK_AP_NAME, mApNameEditText.getText().toString());
		Data.setData(mContext, Item.NETWORK_SERVER_ADDRESS_1, mServerAddressEditText1.getText().toString());
		Data.setData(mContext, Item.NETWORK_SERVER_ADDRESS_2, mServerAddressEditText2.getText().toString());
		Data.setData(mContext, Item.NETWORK_SERVER_ADDRESS_3, mServerAddressEditText3.getText().toString());
		Data.setData(mContext, Item.NETWORK_SERVER_ADDRESS_4, mServerAddressEditText4.getText().toString());
	}

	private void setIpFilter()
	{
		InputFilter[] filters = new InputFilter[1];
		filters[0] = new InputFilter()
		{
			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
			{
				if (end > start)
				{
					String destTxt = dest.toString();
					String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
					if (!resultingTxt.matches("^\\d{1,3}(\\." + "(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?"))
					{
						return "";
					}
					else
					{
						String[] splits = resultingTxt.split("\\.");
						for (int i = 0; i < splits.length; i++)
						{
							if (Integer.valueOf(splits[i]) > 255)
							{
								return "";
							}
						}
					}
				}
				return null;
			}
		};

		mServerAddressEditText1.setFilters(filters);
		mServerAddressEditText2.setFilters(filters);
		mServerAddressEditText3.setFilters(filters);
		mServerAddressEditText4.setFilters(filters);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
	}

	@Override
	public void afterTextChanged(Editable s)
	{
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
	}
}
