package at.jku.geotracker.rest;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.rest.model.ResponseObject;
import at.jku.geotracker.rest.model.ObservationModel;

public class ObservationStopRequest extends AsyncTask<ObservationModel, ResponseObject, ResponseObject> {

	@Override
	protected ResponseObject doInBackground(ObservationModel... params) {
		JSONObject data = new JSONObject();
		try {
			data.put("username", Globals.username);
			data.put("password", Globals.password);
			data.put("targetUsername", params[0].getTarget());
			data.put("targetIsObserver", false);			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// --
		ResponseObject response = RESTUtils.put(Globals.restUrl + "/observation/stop", data);
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
