package at.jku.geotracker.activity;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.drafts.Draft_10;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import at.jku.geotracker.R;
import at.jku.geotracker.ws.client.WSClient;

public class MainActivity extends Activity {

	public TextView debugView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		debugView = (TextView) findViewById(R.id.debugview);

		WSClient c = null;
		try {
			c = new WSClient(new URI("ws://192.168.0.16:80"), new Draft_10());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		debugView.setText("start connect");
		c.connect();
	}

}
