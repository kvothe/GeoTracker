package at.jku.geotracker.rest;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import android.app.Fragment;
import android.os.AsyncTask;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.fragment.SettingsFragment;
import at.jku.geotracker.rest.model.ResponseObjectFragment;

public class SettingsGetRequest extends
		AsyncTask<Fragment, ResponseObjectFragment, ResponseObjectFragment> {

	@Override
	protected ResponseObjectFragment doInBackground(Fragment... params) {
		HttpClient httpclient = HTTPSClient.getNewHttpClient();
		HttpGet get = new HttpGet(Globals.restUrl + "/settings/getobservable/"
				+ Globals.username);

		int statusCode = 500;
		HttpResponse resp = null;
		try {
			resp = httpclient.execute(get);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String respStr = null;
		try {
			respStr = EntityUtils.toString(resp.getEntity());
			statusCode = resp.getStatusLine().getStatusCode();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseObjectFragment(respStr, params[0], statusCode);
	}

	@Override
	protected void onPostExecute(ResponseObjectFragment result) {
		super.onPostExecute(result);
		((SettingsFragment) result.getFragment()).requestGetFinished(result);
	}
}
