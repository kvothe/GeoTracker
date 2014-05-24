package at.jku.geotracker.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import at.jku.geotracker.activity.RegisterActivity;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.rest.model.RegisterModel;
import at.jku.geotracker.rest.model.ResponseObject;

public class RegisterRequest extends
		AsyncTask<RegisterModel, ResponseObject, ResponseObject> {

	@Override
	protected ResponseObject doInBackground(RegisterModel... params) {
		HttpClient httpclient = HTTPSClient.getNewHttpClient();
		HttpPost post = new HttpPost(Globals.restUrl + "/user/register");
		post.setHeader("content-type", "application/json; charset=UTF-8");
		post.setHeader("Accept", "application/json");
		JSONObject dato = new JSONObject();
		try {
			dato.put("username", params[0].getUsername());
			dato.put("password", params[0].getPassword());
			dato.put("observable", params[0].isObservAble());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringEntity entity = null;
		try {
			entity = new StringEntity(dato.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		post.setEntity(entity);

		HttpResponse resp = null;
		int statusCode = 500;
		try {
			resp = httpclient.execute(post);
			statusCode = resp.getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String respStr = null;
		try {
			respStr = EntityUtils.toString(resp.getEntity());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseObject(respStr, params[0].getRegisterActivity(),
				statusCode);
	}

	@Override
	protected void onPostExecute(ResponseObject result) {
		super.onPostExecute(result);
		((RegisterActivity) result.getActivity()).requestFinished(result);
	}
}
