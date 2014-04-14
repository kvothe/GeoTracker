// Get references to elements on the page.
var userField = document.getElementById('username');
var passField = document.getElementById('password');
var registerBtn = document.getElementById('register_button');
var registerFormBtn = document.getElementById('register_form_button');
var loginBtn = document.getElementById('sign_in_button');
var registerUsername = document.getElementById('register_username');
var registerPassword = document.getElementById('register_password');
var registerVisible = document.getElementById('register_visible');
var registerAlertBox = document.getElementById('register_alert_box');
var loginAlertBox = document.getElementById('navbar_alert');
var logoutBtn = document.getElementById('logout_button');
var dashboardAnchor = document.getElementById('dashboard_link');
var map;
var socket;

var cid = -1;
var conversations = {};

window.onresize = function(event) {
	var center = map.getCenter();
	google.maps.event.trigger(map, "resize");
	map.setCenter(center);
};

window.onload = function() {
	
	// Create a new WebSocket.
	socket = new WebSocket('wss://localhost:8443/');

	//Check if loggedIn with cookie
	var sessionId = getCookie("session_id");
	if (sessionId != null && sessionId.length > 5) {
		// TODO: check if the session is still valid on the server
		userField.setAttribute("type", "hidden");
		passField.setAttribute("type", "hidden");
		loginBtn.style.visibility = "hidden";
		registerBtn.style.visibility = "hidden";
		logoutBtn.style.visibility = "visible";
		if (dashboardAnchor != null) {
			dashboardAnchor.style.visibility = "visible";
		}
	} else {
		userField.setAttribute("type", "visible");
		passField.setAttribute("type", "visible");
		loginBtn.style.visibility = "visible";
		registerBtn.style.visibility = "visible";
		logoutBtn.style.visibility = "hidden";
		if (dashboardAnchor != null) {
			dashboardAnchor.style.visibility = "hidden";
		}
	}

	//check if allowed to view dashboard
	if (document.URL.indexOf('dashboard') > -1) {
		if (sessionId == null || sessionId.length < 5) {
			window.location.href = "index.html";

		} else {
			initialize();
			getLocation();
		}
	}

	// Handle any errors that occur.
	socket.onerror = function(error) {
		console.log('WebSocket Error: ' + error);
	};

	// Show a connected message when the WebSocket is opened.
	socket.onopen = function(event) {
		cid = 0;
		console.log('Connected to: ' + event.currentTarget.URL);
	};

	// Handle messages sent by the server.
	socket.onmessage = function(event) {
		event.preventDefault();
		console.log(event.data);

		var response = JSON.parse(event.data);		
		var mcid = response[0]["cid"];
		// --
		var responseTo = conversations[mcid];		
		delete conversations[mcid];
		// --
		switch(responseTo) {
			case "request-registration":
				handleRegistration(response[0]);
				break;
			case "request-login":
				handleLogin(response[0]);
				break;
		}
		return false;
	};

	// Show a disconnected message when the WebSocket is closed.
	socket.onclose = function(event) {
		console.log('Disconnected from WebSocket.');
	};

	// Navigate to registration
	if (registerBtn != null) {
		registerBtn.onclick = function(e) {
			window.location.href = "register.html";
			return false;
		};
	}

	// Send registration request
	if (registerFormBtn != null) {
		registerFormBtn.onclick = function(e) {
			e.preventDefault();
			var mcid = ++cid;
			// Retrieve username and password
			var username = registerUsername.value;
			var password = registerPassword.value;
			var visible = registerVisible.checked;
			
			if (username.length > 6 && password.length > 6) {
				var request = {
					"cid" : mcid,
					"message-type" : "request-registration",					
					"username" : username,
					"password" : password,
					"observable" : visible
				};
				conversations[mcid] = "request-registration";
				socket.send(JSON.stringify(request));
			} else {
				registerAlertBox.removeAttribute('hidden');
				$("#register_alert_box").removeClass("alert-success");
				$("#register_alert_box").addClass("alert-danger");
				registerAlertBox.innerHTML = " <b>Error!</b> Enter Username(min. length is 6) and Password(min. length is 6)";
			}
			return false;
		};
	}

	// Send login request
	if (loginBtn != null) {
		loginBtn.onclick = function(e) {
			e.preventDefault();
			var mcid = ++cid;
			// Retrieve username and password
			var username = userField.value;
			var password = passField.value;

			if (username.length > 6 && password.length > 6) {

				var request = {
					"cid" : mcid,
					"message-type" : "request-login",
					"username" : username,
					"password" : password
				};
				conversations[mcid] = "request-login";
				socket.send(JSON.stringify(request));
			} else {
				loginAlertBox.removeAttribute('hidden');
				loginAlertBox.innerHTML = " <b>Error!</b> Enter Username(min. length is 6) and Password(min. length is 6)";
			}
			return false;
		};
	}

	// Send logout request
	if (logoutBtn != null) {
		logoutBtn.onclick = function(e) {
			e.preventDefault();
			var mcid = ++cid;
			var request = {
				"cid" : mcid,
				"message-type" : "request-logout",
				"session-id" : getCookie("session_id")
			};
			conversations[mcid] = "request-logout";
			socket.send(JSON.stringify(request));

			//remove cookie
			eraseCookie("session_id");

			//navigate to home
			window.location.href = "index.html";

			return false;
		};
	}

};

//handle registration response
function handleRegistration(data) {	
	console.log('Handle Registration Response');
	registerAlertBox.removeAttribute('hidden');
	//success
	if (data["message-type"] === 'response-ok') {
		$("#register_alert_box").removeClass("alert-danger");
		$("#register_alert_box").addClass("alert-success");
		//registerAlertBox.className.replace('alert-danger', 'alert-success');
		registerAlertBox.innerHTML = "<b>Success!</b> You successfully registered for GeoTracker.";
		registerUsername.value = '';
		registerPassword.value = '';
		registerVisible.checked = false;
	} else {
		//registerAlertBox.className.replace('alert-success', 'alert-danger');
		registerAlertBox.innerHTML = " <b>Error!</b> " + data["message"];
		$("#register_alert_box").removeClass("alert-success");
		$("#register_alert_box").addClass("alert-danger");
	}
	//alert(data["message"]);
}

//handle login response
function handleLogin(data) {	
	console.log('Handle Registration Response');
	//success
	if (data["message-type"] === 'response-ok') {
		setCookie("session_id", data["message"], 7);
		window.location.href = "dashboard.html";
	} else {
		loginAlertBox.removeAttribute('hidden');
		loginAlertBox.innerHTML = " <b>Error!</b> " + data["message"];
	}
}

function setCookie(cname, cvalue, exdays) {
	var d = new Date();
	d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
	var expires = "expires=" + d.toGMTString();
	document.cookie = cname + "=" + cvalue + "; " + expires;
}

function getCookie(cname) {
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i].trim();
		if (c.indexOf(name) == 0)
			return c.substring(name.length, c.length);
	}
	return "";
}

function eraseCookie(cname) {
	setCookie(cname, "", -1);
}

function initialize() {
	var mapProp = {
		center : new google.maps.LatLng(51.508742, -0.120850),
		zoom : 5,
		mapTypeId : google.maps.MapTypeId.ROADMAP
	};
	map = new google.maps.Map(document.getElementById("googleMap"), mapProp);
}

function getLocation() {
	if (navigator.geolocation) {
		navigator.geolocation.watchPosition(showPosition);
	}
}

function showPosition(position) {
	var mapProp = {
		center : new google.maps.LatLng(position.coords.latitude, position.coords.longitude),
		zoom : 11,
		mapTypeId : google.maps.MapTypeId.ROADMAP
	};
	map = new google.maps.Map(document.getElementById("googleMap"), mapProp);

	var myLatlng = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);

	var marker = new google.maps.Marker({
		position : myLatlng,
		map : map
	});
	
	sendLocationUpdate(position);
}

//send Location Update
function sendLocationUpdate(position) {
	console.log('Send new Location Update');	
	var request = {		
		"message-type" : "location-update",
		"session-id" : getCookie("session_id"),
		"latitude" : position.coords.latitude,
		"logitude" : position.coords.longitude,
		"accuracy" : position.coords.accuracy,
		"altitude" : position.coords.altitude,
		"altitude-accuracy" : position.coords.altitudeAccuracy,
		"heading" : position.coords.heading,
		"speed" : position.coords.speed,
		"timestamp" : position.timestamp
	}; 
	socket.send(JSON.stringify(request));
}

