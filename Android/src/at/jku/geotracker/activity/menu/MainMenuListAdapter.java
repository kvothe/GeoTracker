package at.jku.geotracker.activity.menu;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import at.jku.geotracker.R;

public class MainMenuListAdapter extends BaseAdapter {

	private ArrayList<MainMenuItem> listData;

	private LayoutInflater layoutInflater;

	public MainMenuListAdapter(Context context, ArrayList<MainMenuItem> listData) {
		this.listData = listData;
		layoutInflater = LayoutInflater.from(context.getApplicationContext());
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	public ArrayList<MainMenuItem> getAllItems() {
		return listData;
	}

	public void setAllItems(ArrayList<MainMenuItem> listData) {
		this.listData = listData;
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
		convertView = layoutInflater.inflate(R.layout.menu_list_style, null);

		TextView titleView = (TextView) convertView
				.findViewById(R.id.menu_item_title);

		titleView.setText(listData.get(position).getTitle());

		return convertView;
	}
}