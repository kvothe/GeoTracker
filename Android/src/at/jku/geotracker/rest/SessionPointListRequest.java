package at.jku.geotracker.rest;

import android.os.AsyncTask;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.rest.model.ResponseObject;
import at.jku.geotracker.rest.model.SessionModel;

public class SessionPointListRequest extends AsyncTask<SessionModel, ResponseObject, ResponseObject> {

	@Override
	protected ResponseObject doInBackground(SessionModel... params) {
		ResponseObject response = RESTUtils.get(Globals.restUrl + "/session/points/" + params[0].getSessionId());
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
