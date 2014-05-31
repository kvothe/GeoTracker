package at.jku.geotracker.rest;

import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.rest.model.ResponseObject;
import at.jku.geotracker.rest.model.SettingsModel;

public class SettingsSetRequest extends AsyncTask<SettingsModel, ResponseObject, ResponseObject> {

	@Override
	protected ResponseObject doInBackground(SettingsModel... params) {
		JSONObject data = new JSONObject();
		try {
			data.put("username", params[0].getUsername());
			data.put("password", params[0].getPassword());
			data.put("observable", params[0].isObservable());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// --
		ResponseObject response = RESTUtils.put(Globals.restUrl + "/settings/setobservable", data);
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
