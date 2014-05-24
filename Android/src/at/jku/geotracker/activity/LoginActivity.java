package at.jku.geotracker.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.jku.geotracker.R;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.rest.LoginRequest;
import at.jku.geotracker.rest.model.LoginModel;
import at.jku.geotracker.rest.model.ResponseObject;

// TODO: Auto-generated Javadoc
/**
 * Activity which displays a login screen to the user.
 */
public class LoginActivity extends Activity {

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	private Button registerButton;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);
		
		registerButton = (Button) findViewById(R.id.register_button);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		showProgress(false);
		
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(registerIntent);
			}
		});
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		String errorMessage = "";
		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			focusView = mPasswordView;
			cancel = true;
			errorMessage = "Bitte Passwort angeben!";
		} else if (mPassword.length() < 4) {
			focusView = mPasswordView;
			cancel = true;
			errorMessage = "Passwort zu kurz!";
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			focusView = mEmailView;
			cancel = true;
			errorMessage = "Bitte Benutzernamen angeben!";
		}

		if (cancel) {
			Toast incorrectToast = Toast.makeText(getApplicationContext(),
					errorMessage, Toast.LENGTH_LONG);
			TextView toastview = (TextView) incorrectToast.getView()
					.findViewById(android.R.id.message);
			toastview.setTextColor(Color.RED);
			incorrectToast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
			incorrectToast.show();
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText("Log in...");
			showProgress(true);

			LoginModel loginModel = new LoginModel(this.mEmail, this.mPassword,
					this);
			new LoginRequest().execute(loginModel);
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
		inputMethodManager.hideSoftInputFromWindow(
				mEmailView.getApplicationWindowToken(), 0);

		mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	public void requestFinished(ResponseObject response) {
		showProgress(false);
		if (response.getStatusCode() == 200) {
			// go to main menu
			Globals.password = this.mPassword;
			Globals.username = this.mEmail;
			Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(mainIntent);
		} else {
			Toast incorrectToast = Toast.makeText(getApplicationContext(),
					"Die Benutzerdaten sind nicht korrekt", Toast.LENGTH_LONG);
			TextView toastview = (TextView) incorrectToast.getView()
					.findViewById(android.R.id.message);
			toastview.setTextColor(Color.RED);
			incorrectToast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
			incorrectToast.show();
		}

	}
}
