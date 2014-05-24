package at.jku.geotracker.activity.userlist;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import at.jku.geotracker.R;

public class UserListAdapter extends BaseAdapter {

	private ArrayList<UserItem> listData;
	private LayoutInflater layoutInflater;

	public UserListAdapter(Context context, ArrayList<UserItem> listData) {
		this.listData = listData;
		layoutInflater = LayoutInflater.from(context);
	}

	public ArrayList<UserItem> getAllItems() {
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

		convertView = layoutInflater.inflate(R.layout.user_list_style, null);

		TextView titleView = (TextView) convertView
				.findViewById(R.id.user_list_title);

		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.user_list_image);

		titleView.setText(listData.get(position).getName());

		if (listData.get(position).isObserved()) {
			imageView.setVisibility(View.VISIBLE);
		} else {
			imageView.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}
}