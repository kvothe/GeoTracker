package at.jku.geotracker.rest;

import org.json.JSONObject;

import android.os.AsyncTask;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.rest.model.ResponseObject;

public class LocationUpdate extends AsyncTask<JSONObject, ResponseObject, ResponseObject> {

	@Override
	protected ResponseObject doInBackground(JSONObject... params) {
		return RESTUtils.post(Globals.restUrl + "/location/update", params[0]);
	}

	@Override
	protected void onPostExecute(ResponseObject result) {
		super.onPostExecute(result);
		if (result.getListener() != null) {
			result.getListener().receivedResponse(result);
		}
	}
}
