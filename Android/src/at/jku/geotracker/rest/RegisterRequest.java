package at.jku.geotracker.rest;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.rest.model.RegisterModel;
import at.jku.geotracker.rest.model.ResponseObject;

public class RegisterRequest extends AsyncTask<RegisterModel, ResponseObject, ResponseObject> {

	@Override
	protected ResponseObject doInBackground(RegisterModel... params) {
		JSONObject data = new JSONObject();
		try {
			data.put("username", params[0].getUsername());
			data.put("password", params[0].getPassword());
			data.put("observable", params[0].isObservAble());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// --
		ResponseObject response = RESTUtils.post(Globals.restUrl + "/user/register", data);
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
