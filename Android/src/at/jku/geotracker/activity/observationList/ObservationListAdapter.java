package at.jku.geotracker.activity.observationList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import at.jku.geotracker.R;

public class ObservationListAdapter extends BaseAdapter {

	private ArrayList<ObservationItem> listData;
	private LayoutInflater layoutInflater;

	public ObservationListAdapter(Context context, ArrayList<ObservationItem> listData) {
		this.listData = listData;
		layoutInflater = LayoutInflater.from(context);
	}

	public ArrayList<ObservationItem> getAllItems() {
		return listData;
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		convertView = layoutInflater.inflate(R.layout.observation_list_style, null);

		TextView titleView = (TextView) convertView
				.findViewById(R.id.session_list_title);
		
		TextView dateView = (TextView) convertView.findViewById(R.id.session_list_date);


		titleView.setText(listData.get(position).getObserved());
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		dateView.setText(sdf.format(new Date(listData.get(position).getStartTime())));
		if(listData.get(position).getEndTime() != 0 && listData.get(position).getEndTime() > listData.get(position).getStartTime()) {
			dateView.setText(dateView.getText() + " - " + sdf.format(new Date(listData.get(position).getEndTime())));
		} else {
			dateView.setText(dateView.getText() + " - jetzt");
		}

		return convertView;
	}
}