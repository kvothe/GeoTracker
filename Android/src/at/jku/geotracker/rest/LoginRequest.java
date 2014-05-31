package at.jku.geotracker.rest;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.rest.model.LoginModel;
import at.jku.geotracker.rest.model.ResponseObject;

public class LoginRequest extends AsyncTask<LoginModel, ResponseObject, ResponseObject> {
	private static final String TAG = "GeoTracker";

	@Override
	protected ResponseObject doInBackground(LoginModel... params) {
		JSONObject credentials = new JSONObject();
		try {
			credentials.put("username", params[0].getUsername());
			credentials.put("password", params[0].getPassword());
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		// --
		ResponseObject response = RESTUtils.put(Globals.restUrl + "/login", credentials);
		// --
		response.setListener(params[0].getListener());
		return response;
	}

	@Override
	protected void onPostExecute(ResponseObject result) {
		super.onPostExecute(result);
		if (result.getListener() != null) {
			result.getListener().receivedResponse(result);
		}
	}
}
