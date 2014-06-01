package at.jku.geotracker.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import at.jku.geotracker.R;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.rest.SettingsGetRequest;
import at.jku.geotracker.rest.SettingsSetRequest;
import at.jku.geotracker.rest.interfaces.ResponseListener;
import at.jku.geotracker.rest.model.ResponseObject;
import at.jku.geotracker.rest.model.SettingsModel;

public class SettingsFragment extends Fragment {
	public static final String PREFERENCE_NAME = "GeoTrackerPreferences";

	private CheckBox settingsObservable;
	private TextView labelUpdateInterval;
	private SeekBar seekUpdateInterval;
	private Button saveButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_fragment, container, false);
		super.onCreate(savedInstanceState);
		// --
		this.settingsObservable = (CheckBox) view.findViewById(R.id.settingsObservable);
		this.labelUpdateInterval = (TextView) view.findViewById(R.id.labelUpdateInterval);
		this.seekUpdateInterval = (SeekBar) view.findViewById(R.id.seekUpdateInterval);
		// --
		// Restore preferences
		SharedPreferences settings = getActivity().getSharedPreferences(PREFERENCE_NAME, 0);
		int updateInterval = settings.getInt("updateIntervalValue", 1000);
		this.seekUpdateInterval.setProgress(updateInterval);
		int y = calculateLogFromSliderValue(updateInterval, seekUpdateInterval.getMax());
		setUpdateIntervalLabel(y);
		// --
		this.seekUpdateInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int x = Math.max(1, seekBar.getProgress());
				int y = calculateLogFromSliderValue(x, seekBar.getMax());
				// --
				SharedPreferences settings = getActivity().getSharedPreferences(PREFERENCE_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt("updateIntervalValue", x);
				editor.commit();
				// --
				((Globals) getActivity().getApplication()).setLocationUpdateInterval(y * 1000);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				int y = calculateLogFromSliderValue(progress, seekBar.getMax());
				// --
				setUpdateIntervalLabel(y);
			}
		});
		// --
		this.saveButton = (Button) view.findViewById(R.id.saveButton);
		this.saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SettingsModel m = new SettingsModel(settingsObservable.isChecked(), new ResponseListener() {
					@Override
					public void receivedResponse(ResponseObject response) {
						if (response.getStatusCode() == 200) {
							Toast.makeText(getActivity().getApplicationContext(), "Erfolgreich gespeichert",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getActivity().getApplicationContext(), "Es ist ein Fehler aufgetreten",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
				new SettingsSetRequest().execute(m);
			}
		});

		new SettingsGetRequest().execute(new ResponseListener() {

			@Override
			public void receivedResponse(ResponseObject response) {
				if (response.getStatusCode() == 200) {
					JSONObject jObject;
					try {
						jObject = new JSONObject(response.getResponse().substring(1,
								response.getResponse().length() - 1));
						boolean observalbe = jObject.getBoolean("observable");
						if (observalbe) {
							settingsObservable.setChecked(true);
						} else {
							settingsObservable.setChecked(false);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		});

		final ImageButton menuButton = (ImageButton) view.findViewById(R.id.menu_button);

		menuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((Globals) getActivity().getApplication()).openMenu();
			}
		});

		return view;
	}
	private void setUpdateIntervalLabel(int seconds) {
		if (seconds == 1) {
			labelUpdateInterval.setText("Update Intervall: " + seconds + " Sekunde");
		} else if (seconds > 1 && seconds < 60) {
			labelUpdateInterval.setText("Update Intervall: " + seconds + " Sekunden");
		} else if (seconds == 60) {
			labelUpdateInterval.setText("Update Intervall: 1 Minute");
		} else if (seconds > 60 && seconds < 3600) {
			labelUpdateInterval.setText("Update Intervall: " + seconds / 60 + " Minuten");
		} else if (seconds == 3600) {
			labelUpdateInterval.setText("Update Intervall: 1 Stunde");
		}
	}
	private int calculateLogFromSliderValue(int progress, int max) {
		int x = progress;
		int minX = 1;
		int maxX = max;
		// Calculate logarithmic value in seconds
		int y = (int) Math.exp(Math.log(minX) + (x - minX) * (Math.log(maxX) - Math.log(minX)) / (maxX - minX));
		y = y < 1 ? 1 : y;
		// --
		return y;
	}
}
