// Get references to elements on the page.
var userField = document.getElementById('username');
var passField = document.getElementById('password');
var registerBtn = document.getElementById('register_button');
var registerFormBtn = document.getElementById('register_form_button');
var loginBtn = document.getElementById('sign_in_button');
var logoutBtn = document.getElementById('logout_button');
var logoutForm = document.getElementById('navbar_form_logout');
var dashboardLink = document.getElementById('dashboard_link');
var jumbotron = document.getElementById('jumbotron');
var map;
var socket;

var cid = -1;
var conversations = {};

window.onload = function () {
  // Create a new WebSocket.
  socket = new WebSocket('wss://localhost:8443/');

  //Check if loggedIn with cookie
  var sessionId = getCookie("session_id");
  if (sessionId != null && sessionId.length > 5) {
    // TODO: check if the session is still valid on the server
    $('#navbar_form_logout').show();
    $('#navbar_form_login').hide();
    $('#dashboard_link').show();
  } else {
    $('#dashboard_link').hide();
    $('#navbar_form_logout').hide();
    $('#navbar_form_login').show();    
  }

  //check if allowed to view dashboard
  if (document.URL.indexOf('index.html') == -1) {    
      window.location.href = "index.html";
  }

  // Handle any errors that occur.
  socket.onerror = function (error) {
    console.log('WebSocket Error: ' + error);
  };

  // Show a connected message when the WebSocket is opened.
  socket.onopen = function (event) {
    cid = 0;
    console.log('Connected to: ' + event.currentTarget.URL);
  };

  // Handle messages sent by the server.
  socket.onmessage = function (event) {
    event.preventDefault();
    console.log(event.data);

    var response = JSON.parse(event.data);
    var mcid = response[0]["cid"];
    // --
    var responseTo = conversations[mcid];
    delete conversations[mcid];
    // --
    switch (responseTo) {
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
  socket.onclose = function (event) {
    console.log('Disconnected from WebSocket.');
  };

  if (dashboardLink != null) {
    dashboardLink.onclick = function (e) {
      e.preventDefault ? e.preventDefault() : e.returnValue = false;
      // --
      showDashboard();
    }
  }

  // Navigate to registration
  if (registerBtn != null) {
    registerBtn.onclick = function (e) {
      e.preventDefault ? e.preventDefault() : e.returnValue = false;
      hideMessage();
      // load page
      $('#page_content').load('register.html #content', function () {
        $('#register_submit').click(buttonHandlerRegister);
        // $('#navbar_form_login').hide();        
        $('#page_content').hide().fadeIn();
      });
    };
  }

  // Send login request
  if (loginBtn != null) {
    loginBtn.onclick = function (e) {
      console.log("login handler");
      e.preventDefault ? e.preventDefault() : e.returnValue = false;
      // --
      var mcid = ++cid;
      // Retrieve username and password
      var username = $('#username').val();
      var password = $('#password').val();

      if (username.length > 4 && password.length > 6) {
        var request = {
          "cid" : mcid,
          "message-type" : "request-login",
          "username" : username,
          "password" : password
        };
        conversations[mcid] = "request-login";
        socket.send(JSON.stringify(request));
        hideMessage();
      } else {
        console.log("login error");
        $('#username').val('');
        $('#password').val('');
        showErrorMessage("<b>Error!</b> Enter Username(min. length is 4) and Password(min. length is 6)");        
      }
    };
  }

  // Send logout request
  if (logoutBtn != null) {
    logoutBtn.onclick = function (e) {
      e.preventDefault ? e.preventDefault() : e.returnValue = false;
      // --
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
      showSuccessMessage("You have been logged out");      
    };
  }

};

window.onresize = function (event) {
  if (map) {
    var center = map.getCenter();
    google.maps.event.trigger(map, "resize");
    map.setCenter(center);
  }
};

/* ----------------------------------------------------------------------------
Functions for onClick handlers that have to be set after the page has been
loaded dynamically via jQuery.
--------------------------------------------------------------------------- */
function buttonHandlerRegister(e) {
  console.log("register on click");
  e.preventDefault ? e.preventDefault() : e.returnValue = false;
  // --
  var mcid = ++cid;
  // Retrieve username and password
  var username = $("#register_username").val();
  var password = $("#register_password").val();
  var passwordRepeat = $("#register_password_repeat").val();
  var visible = $("#register_observable").is(':checked');
  // --
  if (username.length > 4 && password.length > 6) {
    if (password == passwordRepeat) {
      console.log("passwords match");
      var request = {
        "cid" : mcid,
        "message-type" : "request-registration",
        "username" : username,
        "password" : password,
        "observable" : visible
      };
      conversations[mcid] = "request-registration";
      socket.send(JSON.stringify(request));
      console.log("registration request sent");
    } else {
      showErrorMessage("Entered passwords do not match.");
    }
  } else {
    showErrorMessage("<b>Error!</b> Enter Username(min. length is 4) and Password(min. length is 6)");
  }
  return false;
};

// ----------------------------------------------------------------------------

function showDashboard() {
  $('#dashboard_link').fadeIn();
  hideMessage();
  $('#page_content').load('dashboard.html #content', function () {
    // loadScript();
    initializeMap();
    getLocation();
    // --
    $('#jumbotron').hide();
    $('#navbar_form_logout').fadeIn();
    $('#navbar_form_login').hide();
    $('#page_content').hide().fadeIn();
  });
}

/* ----------------------------------------------------------------------------
Handler functions for messages received from the server
--------------------------------------------------------------------------- */

//handle registration response
function handleRegistration(data) {
  console.log('Handle Registration Response');
  //success
  if (data["message-type"] === 'response-ok') {
    // Perform auto login
    setCookie("session_id", data["message"], 7);
    $('#dashboard_link').fadeIn();
    $('#navbar_form_logout').fadeIn();
    $('#navbar_form_login').hide();
    // --
    showSuccessMessage("<b>Success!</b> You successfully registered for GeoTracker.");
    // reset values
    $("#register_username").val('');
    $("#register_password").val('');
    $("#register_password_repeat").val('');
    $("#register_observable").prop("checked", true);
  } else {
    showErrorMessage(" <b>Error!</b> " + data["message"]);
  }
}

// ----------------------------------------------------------------------------

//handle login response
function handleLogin(data) {
  console.log('Handle Login Response');
  //success
  if (data["message-type"] === 'response-ok') {
    setCookie("session_id", data["message"], 7);
    showDashboard();
  } else {
    showErrorMessage("<b>Error!</b> " + data["message"]);
  }
}

/* ----------------------------------------------------------------------------
Messages sent to the server go here
--------------------------------------------------------------------------- */

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

/* ----------------------------------------------------------------------------
Utilities
--------------------------------------------------------------------------- */

function showSuccessMessage(message) {
  $("#page_alert").removeClass("alert-danger");
  $("#page_alert").addClass("alert-success");
  $("#page_alert").html(message);
  $("#page_alert").fadeIn();
}
function showErrorMessage(message) {
  $("#page_alert").removeClass("alert-success");
  $("#page_alert").addClass("alert-danger");
  $("#page_alert").html(message);
  $("#page_alert").fadeIn();
}
function hideMessage() {  
  $("#page_alert").fadeOut();
}

// ----------------------------------------------------------------------------


function setCookie(cname, cvalue, exhours) {
  var d = new Date();
  d.setTime(d.getTime() + (exhours * 60 * 60 * 1000));
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

// ----------------------------------------------------------------------------

function loadScript() {
  var script = document.createElement('script');
  script.type = 'text/javascript';
  script.src = 'https://maps.googleapis.com/maps/api/js?key=AIzaSyCk21t6ICUW7xeQMvz0qL1jL_VNwl7sLtw&sensor=false&' +
    'callback=initializeMap';
  document.body.appendChild(script);
}

function initializeMap() {
  var mapProp = {
    center : new google.maps.LatLng(51.508742, -0.120850),
    zoom : 5,
    mapTypeId : google.maps.MapTypeId.ROADMAP
  };
  map = new google.maps.Map(document.getElementById("googleMap"), mapProp);
}

function getLocation() {
  if (navigator.geolocation) {
   console.log("showPosition");
    navigator.geolocation.watchPosition(showPosition);
  }
}

// ----------------------------------------------------------------------------

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
