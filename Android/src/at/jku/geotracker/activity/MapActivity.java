package at.jku.geotracker.activity;

import android.app.Activity;
import android.os.Bundle;
import at.jku.geotracker.R;
import at.jku.geotracker.rest.SessionPointListRequest;
import at.jku.geotracker.rest.model.ResponseObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

public class MapActivity extends Activity {
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		
		
		SessionPointListRequest.id = String.valueOf(getIntent().getIntExtra("id", 0));
		new SessionPointListRequest().execute(this);
	}

	public void requestFinished(ResponseObject response) {
		if (response.getStatusCode() == 200) {

		}
	}

}