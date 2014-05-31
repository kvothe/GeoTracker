package at.jku.geotracker.rest;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.rest.model.ResponseObject;
import at.jku.geotracker.rest.model.ObservationModel;

public class ObservationStartRequest extends AsyncTask<ObservationModel, ResponseObject, ResponseObject> {

	@Override
	protected ResponseObject doInBackground(ObservationModel... params) {
		JSONObject data = new JSONObject();
		try {			
			data.put("targetUsername", params[0].getTarget());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// --
		ResponseObject response = RESTUtils.post(Globals.restUrl + "/observation/start", data);
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
