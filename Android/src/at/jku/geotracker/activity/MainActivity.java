package at.jku.geotracker.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import at.jku.geotracker.R;
import at.jku.geotracker.activity.menu.MainMenuListAdapter;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.fragment.SessionListFragment;
import at.jku.geotracker.fragment.SettingsFragment;
import at.jku.geotracker.fragment.UserListFragment;
import at.jku.geotracker.service.GPSTrackerService;

public class MainActivity extends Activity {

	public static final String TAG = "GeoTracker";

	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private ListView menuListView;
	private Intent gspIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final LinearLayout menuLayout = (LinearLayout) findViewById(R.id.left_drawer);
		menuListView = (ListView) findViewById(R.id.menu_list);
		menuListView.addFooterView(new View(getApplicationContext()), null, true);

		((Globals) getApplication()).initWSS();

		this.gspIntent = new Intent(this, GPSTrackerService.class);
		startService(this.gspIntent);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.menu, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu();
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);

		final MainMenuListAdapter menuListAdapter = new MainMenuListAdapter(getApplicationContext(),
				((Globals) getApplication()).getMenuList());
		menuListView.setAdapter(menuListAdapter);

		menuListView.setClickable(true);
		menuListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				if (position == 0) {
					UserListFragment userListFragment = new UserListFragment();

					FragmentTransaction transaction = getFragmentManager().beginTransaction();
					transaction.replace(R.id.main_container, userListFragment, UserListFragment.class.getSimpleName());
					transaction.commit();
				} else if (position == 1) {
					SettingsFragment settingsFragment = new SettingsFragment();

					FragmentTransaction transaction = getFragmentManager().beginTransaction();
					transaction.replace(R.id.main_container, settingsFragment, SettingsFragment.class.getSimpleName());
					transaction.commit();
				} else if (position == 2) {
					SessionListFragment sessionListFragment = new SessionListFragment();

					FragmentTransaction transaction = getFragmentManager().beginTransaction();
					transaction.replace(R.id.main_container, sessionListFragment,
							SessionListFragment.class.getSimpleName());
					transaction.commit();
				}
				((Globals) getApplication()).closeMenu();

			}
		});

		((Globals) getApplication()).setMenuLayout(menuLayout, this.drawerLayout);

		UserListFragment userListFragment = new UserListFragment();

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.main_container, userListFragment, UserListFragment.class.getSimpleName());
		transaction.commit();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopService(this.gspIntent);
	}

}
