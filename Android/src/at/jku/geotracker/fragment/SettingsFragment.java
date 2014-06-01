package at.jku.geotracker.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;
import at.jku.geotracker.R;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.rest.SettingsGetRequest;
import at.jku.geotracker.rest.SettingsSetRequest;
import at.jku.geotracker.rest.interfaces.ResponseListener;
import at.jku.geotracker.rest.model.ResponseObject;
import at.jku.geotracker.rest.model.SettingsModel;

public class SettingsFragment extends Fragment {
	private CheckBox settingsObservable;
	private Button saveButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_fragment, container, false);
		super.onCreate(savedInstanceState);
		// --
		this.settingsObservable = (CheckBox) view.findViewById(R.id.settingsObservable);
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
}
