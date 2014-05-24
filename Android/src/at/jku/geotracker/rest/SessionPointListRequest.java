package at.jku.geotracker.rest;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.AsyncTask;
import at.jku.geotracker.activity.MapActivity;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.rest.model.ResponseObject;
import at.jku.geotracker.rest.model.ResponseObjectFragment;

public class SessionPointListRequest extends
		AsyncTask<Activity, ResponseObject, ResponseObject> {

	public static String id = null;

	@Override
	protected ResponseObject doInBackground(Activity... params) {
		HttpClient httpclient = HTTPSClient.getNewHttpClient();
		HttpGet get = new HttpGet(Globals.restUrl
				+ "/session/getsessionpoints/" + id);

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
		return new ResponseObject(respStr, params[0], statusCode);
	}

	@Override
	protected void onPostExecute(ResponseObject result) {
		super.onPostExecute(result);
		((MapActivity) result.getActivity()).requestFinished(result);
	}
}
