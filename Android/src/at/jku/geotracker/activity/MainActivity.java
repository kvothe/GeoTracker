package at.jku.geotracker.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import at.jku.geotracker.R;
import at.jku.geotracker.ws.client.WSClient;
import at.jku.geotracker.ws.ssl.MySSLSocketFactory;

public class MainActivity extends Activity {

	public TextView debugView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		debugView = (TextView) findViewById(R.id.debugview);

		WSClient c = null;
		try {
			c = new WSClient(new URI("wss://schnelleflitzer.at"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		debugView.setText("start connect");

		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			c.setSocket(sf.createSocket());

			c.connectBlocking();

			JSONObject jo = new JSONObject();
			try {
				jo.put("cid", 2);
				jo.put("message-type", "request-login");
				jo.put("username", "asdfasdf");
				jo.put("password", "test11");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String message = jo.toString();
			System.out.println(message);

			c.send(message);

		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
