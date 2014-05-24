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
import at.jku.geotracker.rest.model.ResponseObjectFragment;
import at.jku.geotracker.rest.model.SettingsModel;

public class SettingsFragment extends Fragment {

	private CheckBox settingsObservable;
	private Button saveButton;
	private SettingsModel sm;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_fragment, container,
				false);
		super.onCreate(savedInstanceState);
		this.sm = new SettingsModel();
		
		this.settingsObservable = (CheckBox) view.findViewById(R.id.settingsObservable);
		this.saveButton = (Button) view.findViewById(R.id.saveButton);
		
		sm.setPassword(Globals.password);
		sm.setUsername(Globals.username);
		sm.setSettingsFragment(this);
		
		this.saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sm.setObservable(settingsObservable.isChecked());
				new SettingsSetRequest().execute(sm);
			}
		});

		new SettingsGetRequest().execute(this);

		final ImageButton menuButton = (ImageButton) view
				.findViewById(R.id.menu_button);

		menuButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((Globals) getActivity().getApplication()).openMenu();
			}
		});

		return view;
	}

	public void requestGetFinished(ResponseObjectFragment response) {
		if (response.getStatusCode() == 200) {
			JSONObject jObject;
			try {
				jObject = new JSONObject(response.getResponse().substring(1, response.getResponse().length()-1));
				boolean observalbe = jObject.getBoolean("observable");
				if (observalbe) {
					this.settingsObservable.setChecked(true);
				} else {
					this.settingsObservable.setChecked(false);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	public void requestPutFinished(ResponseObjectFragment response) {
		if (response.getStatusCode() == 200) {
			Toast.makeText(getActivity().getApplicationContext(),
					"Erfolgreich gespeichert", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getActivity().getApplicationContext(),
					"Es ist ein Fehler aufgetreten", Toast.LENGTH_SHORT).show();
		}
	}
}
