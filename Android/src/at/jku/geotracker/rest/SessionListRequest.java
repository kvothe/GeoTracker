package at.jku.geotracker.rest;

import android.os.AsyncTask;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.rest.interfaces.ResponseListener;
import at.jku.geotracker.rest.model.ResponseObject;

public class SessionListRequest extends AsyncTask<ResponseListener, ResponseObject, ResponseObject> {

	@Override
	protected ResponseObject doInBackground(ResponseListener... params) {
		ResponseObject response = RESTUtils.get(Globals.restUrl + "/session/getsessionlist/" + Globals.username);
		// --
		response.setListener(params[0]);
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
