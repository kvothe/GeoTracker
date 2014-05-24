package at.jku.geotracker.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import at.jku.geotracker.R;
import at.jku.geotracker.activity.MapActivity;
import at.jku.geotracker.activity.observationList.ObservationItem;
import at.jku.geotracker.activity.observationList.ObservationListAdapter;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.rest.SessionListRequest;
import at.jku.geotracker.rest.model.ResponseObjectFragment;

public class SessionListFragment extends Fragment {

	private ListView observationListView;
	private ObservationListAdapter listAdapter;
	private ArrayList<ObservationItem> observationItems;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.observationlist_fragment,
				container, false);
		super.onCreate(savedInstanceState);
		this.observationItems = new ArrayList<ObservationItem>();

		observationListView = (ListView) view
				.findViewById(R.id.observation_list_view);

		this.listAdapter = new ObservationListAdapter(getActivity()
				.getApplicationContext(), this.observationItems);
		observationListView.setAdapter(listAdapter);
		observationListView.setClickable(true);
		observationListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		new SessionListRequest().execute(this);

		this.observationListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						ObservationItem clickedItem = (ObservationItem) listAdapter
								.getItem(position);

						Intent mapIntent = new Intent(getActivity(), MapActivity.class);
						mapIntent.putExtra("id", clickedItem.getId());
						startActivity(mapIntent);
					}

				});

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

	public void requestFinished(ResponseObjectFragment response) {
		if (response.getStatusCode() == 200) {
			try {
				String respOkay = response.getResponse().substring(1,
						response.getResponse().length() - 1);
				JSONArray jsonArray = new JSONArray(respOkay);
				for (int i = 0; i < jsonArray.length(); i++) {
					ObservationItem ui = new ObservationItem();
					JSONObject user = jsonArray.getJSONObject(i);
					ui.setEndTime(user.getLong("endtime"));
					ui.setId(user.getInt("observation-id"));
					ui.setObserved(user.getString("observed"));
					ui.setStartTime(user.getLong("starttime"));
					this.observationItems.add(ui);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			this.listAdapter.notifyDataSetChanged();
		}
	}
}
