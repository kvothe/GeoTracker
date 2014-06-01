package at.jku.geotracker.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.jku.geotracker.R;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.rest.RegisterRequest;
import at.jku.geotracker.rest.interfaces.ResponseListener;
import at.jku.geotracker.rest.model.RegisterModel;
import at.jku.geotracker.rest.model.ResponseObject;

// TODO: Auto-generated Javadoc
/**
 * Activity which displays a login screen to the user.
 */
public class RegisterActivity extends Activity {

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
	private String mPasswordAgain;
	private boolean mObservable;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mPasswordAgainView;
	private CheckBox mObservableView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);

		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);

		mPasswordAgainView = (EditText) findViewById(R.id.password_again);

		mObservableView = (CheckBox) findViewById(R.id.observable_view);

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});
		showProgress(false);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form. If there are form errors (invalid email,
	 * missing fields, etc.), the errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mPasswordAgain = mPasswordAgainView.getText().toString();
		mObservable = mObservableView.isChecked();

		boolean cancel = false;
		View focusView = null;

		String errorMessage = "";
		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			focusView = mPasswordView;
			cancel = true;
			errorMessage = "Bitte Passwort angeben!";
		} else if (mPassword.length() < 4 || mPasswordAgain.length() < 4) {
			focusView = mPasswordView;
			cancel = true;
			errorMessage = "Passwort zu kurz!";
		} else if (!mPassword.equals(mPasswordAgain)) {
			focusView = mPasswordView;
			cancel = true;
			errorMessage = "Passwörter stimmen nicht überein!";
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			focusView = mEmailView;
			cancel = true;
			errorMessage = "Bitte Benutzernamen angeben!";
		}

		if (cancel) {
			Toast incorrectToast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
			incorrectToast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
			incorrectToast.show();
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText("Registriere...");
			showProgress(true);

			RegisterModel registerModel = new RegisterModel(this.mEmail, this.mPassword, this.mObservable,
					new ResponseListener() {

						@Override
						public void receivedResponse(ResponseObject response) {
							showProgress(false);
							if (response.getStatusCode() == 200) {

								Toast toast = Toast.makeText(getApplicationContext(), "Erfolgreich registriert",
										Toast.LENGTH_LONG);
								toast.show();
								// go to main menu
								Globals.password = mPassword;
								Globals.username = mEmail;
								Globals.setSessionId(response.getResponse()); // TODO: renew when expired
								Log.d("GeoTracker", "session-token=" + Globals.getSessionId());
								Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
								startActivity(mainIntent);
								// --
								finish();
							} else {
								Globals.setSessionId(null);
								Toast incorrectToast = Toast.makeText(getApplicationContext(), "Fehler aufgetreten",
										Toast.LENGTH_LONG);
								incorrectToast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
								incorrectToast.show();
							}
						}
					});
			new RegisterRequest().execute(registerModel);
		}
	}

	/**
	 * Show progress.
	 * 
	 * @param show
	 *            the show
	 */
	private void showProgress(final boolean show) {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(mEmailView.getApplicationWindowToken(), 0);

		mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
	}
}
