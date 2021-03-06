package at.jku.geotracker.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import at.jku.geotracker.R;
import at.jku.geotracker.activity.userlist.UserItem;
import at.jku.geotracker.activity.userlist.UserListAdapter;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.pushservice.PushServiceHandler;
import at.jku.geotracker.rest.ObservationStartRequest;
import at.jku.geotracker.rest.ObservationStopRequest;
import at.jku.geotracker.rest.UserListRequest;
import at.jku.geotracker.rest.interfaces.ResponseListener;
import at.jku.geotracker.rest.model.ObservationModel;
import at.jku.geotracker.rest.model.ResponseObject;

public class UserListFragment extends Fragment {

	private ListView userListView;
	private UserListAdapter listAdapter;
	private ArrayList<UserItem> userItems;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.userlist_fragment, container, false);
		super.onCreate(savedInstanceState);
		this.userItems = new ArrayList<UserItem>();

		userListView = (ListView) view.findViewById(R.id.user_list_view);

		this.listAdapter = new UserListAdapter(getActivity().getApplicationContext(), this.userItems);
		userListView.setAdapter(listAdapter);
		userListView.setClickable(true);
		userListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		PushServiceHandler pushservice = ((Globals) getActivity().getApplication()).getPushServiceHandler();
		if (pushservice != null) {
			pushservice.setUserList(listAdapter);
		}

		new UserListRequest().execute(new ResponseHandlerUserList());

		this.userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				UserItem clickedItem = (UserItem) listAdapter.getItem(position);

				if (clickedItem.isObserved()) {
					// stop observation
					((UserItem) listAdapter.getItem(position)).setObserved(false);
					new ObservationStopRequest().execute(new ObservationModel(clickedItem.getName(),
							new ResponseHandlerObservation()));
				} else {
					// start observation
					((UserItem) listAdapter.getItem(position)).setObserved(true);
					new ObservationStartRequest().execute(new ObservationModel(clickedItem.getName(),
							new ResponseHandlerObservation()));
				}
				listAdapter.notifyDataSetChanged();
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

	private class ResponseHandlerUserList implements ResponseListener {

		@Override
		public void receivedResponse(ResponseObject response) {
			if (response.getStatusCode() == 200) {
				if (response.getResponse() != null) {
					String respOkay = response.getResponse().substring(1, response.getResponse().length() - 1);
					try {
						JSONArray jsonArray = new JSONArray(respOkay);
						for (int i = 0; i < jsonArray.length(); i++) {
							UserItem ui = new UserItem();
							JSONObject user = jsonArray.getJSONObject(i);
							ui.setName(user.getString("name"));
							ui.setObservable(user.getBoolean("observable"));
							ui.setObserved(user.getBoolean("isObserved"));
							userItems.add(ui);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				listAdapter.notifyDataSetChanged();
			}
		}
	}

	private class ResponseHandlerObservation implements ResponseListener {

		@Override
		public void receivedResponse(ResponseObject response) {
			if (response.getStatusCode() == 200) {
				Toast.makeText(getActivity().getApplicationContext(), "Erfolgreich ge�ndert", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(getActivity().getApplicationContext(), "Es ist ein Fehler aufgetreten",
						Toast.LENGTH_SHORT).show();
			}
		}

	}
}
