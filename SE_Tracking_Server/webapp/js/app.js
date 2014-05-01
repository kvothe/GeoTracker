var map;
var socket;
// --
var cid = -1;
var conversations = {};
var currentUsername;

var browserSupportsLocation = new Boolean();

var path;
var points;
var markers;

window.onload = function () {
  // Create a new WebSocket.
  if (location.port) {
    socket = new WebSocket('wss://' + location.hostname + ':' + location.port);
  } else {
    socket = new WebSocket('wss://' + location.hostname);
  }

  // always redirect to index.html
  if (document.URL.indexOf('index.html') == -1) {
    window.location.href = "index.html";
  }

  // Initialize UI for anonymous user
  $('#dashboard_menu').hide();
  $('#navbar_form_logout').hide();
  $('#navbar_form_login').hide();

  /* --------------------------------------------------------------------------
  WebSocket callbacks
  --------------------------------------------------------------------------- */

  // Show a connected message when the WebSocket is opened.
  socket.onopen = function (event) {
    cid = 0;
    console.log('Connected to: ' + event.currentTarget.URL);
    // check if the session cookie is still valid and act accordingly
    sendSessionCheck();
  };

  // Handle messages sent by the server.
  socket.onmessage = function (event) {
    event.preventDefault();
    console.log(event.data);

    try {
      var message = JSON.parse(event.data);
    } catch (err) {
      console.log(err.message);
      return false;
    }

    if (message[0]["cid"]) {
      var mcid = message[0]["cid"];
      // --
      var responseTo = conversations[mcid];
      delete conversations[mcid];
      // --
      switch (responseTo) {
      case "request-registration":
        handleResponseRegistration(message[0]);
        break;
      case "request-login":
        handleResponseLogin(message[0]);
        break;
      case "request-session-check":
        handleResponseSessionCheck(message[0]);
        break;
      case "request-user-list":
        handleResponseUserList(message[0]);
        break;
      case "request-session-list":
        handleResponseSessionList(message[0]);
        break;
      case "request-session-points":
        handleResponseSessionPoints(message[0]);
        break;
      case "request-start-observation":
        handleResponseStartObservation(message[0]);
        break;
      case "request-stop-observation":
        handleResponseStopObservation(message[0]);
        break;
      }
    } else {
      console.log(message[0]);
      var type = message[0]["message-type"];
      // --
      switch (type) {
      case "notification":
        handleNotification(message[0]);
        break;
      case "location-update":
        handleLocationUpdateNotification(message[0]);
        break;
      }
    }
    return false;
  };

  // Handle any errors that occur.
  socket.onerror = function (error) {
    console.log('WebSocket Error: ' + error);
  };

  // Show a disconnected message when the WebSocket is closed.
  socket.onclose = function (event) {
    console.log('Disconnected from WebSocket.');
    showErrorMessage("You have lost the connection to the server");
  };

  /* --------------------------------------------------------------------------
  Click handler for elements on index.html
  --------------------------------------------------------------------------- */

  if ($('#home_link')) {
    $('#home_link').click(function (e) {
      e.preventDefault ? e.preventDefault() : e.returnValue = false;
      // --
      $('#page_content').load('index.html #page_content', function () {
        $('#jumbotron').fadeIn();
        $('#page_content').hide().fadeIn();
      });
    });
  }

  if ($('#dashboard_menu')) {
    $('#navbar_user_list').click(function (e) {
      e.preventDefault ? e.preventDefault() : e.returnValue = false;
      // --
      showDashboard("user-list");
    });
    $('#navbar_session_list').click(function (e) {
      e.preventDefault ? e.preventDefault() : e.returnValue = false;
      // --
      showDashboard("session-list");
    });
  }

  // Navigate to registration
  if ($('#register_button')) {
    $('#register_button').click(function (e) {
      e.preventDefault ? e.preventDefault() : e.returnValue = false;
      hideMessage();
      // load page
      $('#page_content').load('register.html #content', function () {
        $('#register_submit').click(buttonHandlerRegister);
        $('#register_password').keyup(keypressHandlerPasswordCheck);
        $('#register_password_repeat').keyup(keypressHandlerPasswordCheck);
        $('#register_observable').prop('checked', true);
        // $('#navbar_form_login').hide();
        $('#page_content').hide().fadeIn();
      });
    });
  }

  // Send login request
  if ($('#login_button')) {
    $('#login_button').click(function (e) {
      console.log("login handler");
      e.preventDefault ? e.preventDefault() : e.returnValue = false;

      // Retrieve username and password
      var username = $('#login_username').val();
      var password = $('#login_password').val();

      if (username.length >= 4 && password.length >= 6) {
        sendLoginRequest(username, password);
        hideMessage();
      } else {
        console.log("login error");
        $('#login_username').val('');
        $('#login_password').val('');
        showErrorMessage("<b>Error!</b> Enter Username(min. length is 4) and Password(min. length is 6)");
      }
    });
  }

  // Send logout request
  if ($('#logout_button')) {
    $('#logout_button').click(function (e) {
      e.preventDefault ? e.preventDefault() : e.returnValue = false;
      // --
      sendLogout();

      //remove cookie
      eraseCookie("session_id");
      currentUsername = null;

      //navigate to home
      $('#dashboard_menu').hide();
      $('#navbar_form_logout').hide();
      $('#navbar_form_login').show();

      $('#page_content').load('index.html #page_content', function () {
        $('#page_content').hide().fadeIn();
        showSuccessMessage("You have been logged out");
      });
    });
  }
};

window.onresize = function (event) {
  /*if ($('#googleMap')) {
  $('#googleMap').width($('#dashboard_map_container').width());
  $('#googleMap').height($('#dashboard_map_container').height());
  }*/
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
  e.preventDefault ? e.preventDefault() : e.returnValue = false;

  // Retrieve username and password
  var username = $("#register_username").val();
  var password = $("#register_password").val();
  var passwordRepeat = $("#register_password_repeat").val();
  var observable = $("#register_observable").is(':checked');
  // --
  if (username.length >= 4 && password.length >= 6) {
    if (password == passwordRepeat) {
      sendRegistrationRequest(username, password, observable);
      console.log("registration request sent");
    } else {
      showErrorMessage("Entered passwords do not match.");
    }
  } else {
    showErrorMessage("<b>Error!</b> Enter Username(min. length is 4) and Password(min. length is 6)");
  }
  return false;
};

function keypressHandlerPasswordCheck(event) {
  var pass = $('#register_password').val();
  var check = $('#register_password_repeat').val();
  // --
  if (pass === check) {
    $('#register_form_password_check').addClass("has-success");
  } else {
    $('#register_form_password_check').removeClass("has-success");
  }
}

function searchObservationKeyUp(e) {
  var query = $('#dashboard_session_search').val();
  searchObservationList(query);
}

function buttonHandlerRefreshUserList(e) {
  e.preventDefault ? e.preventDefault() : e.returnValue = false;
  sendRequestUserList(false);
}

function buttonHandlerRefreshSessionList(e) {
  e.preventDefault ? e.preventDefault() : e.returnValue = false;
  sendRequestSessionList(true, false);
}

function listItemHandlerStartObservation(name, observable) {
  console.log("clicked link for user " + name + " (" + observable + ")");
  hideMessage();
  if (observable) {
    sendRequestStartObservation(name);
  } else {
    showErrorMessage(name + " is not observable.");
  }
}

function listItemHandlerStopObservation(observationid, name, starttime, endtime) {
  console.log("clicked link for user " + name + " (" + starttime + "-" + endtime + ")");
  hideMessage();
  if (!endtime) {
    sendRequestStopObservation(observationid, name, false);
  } else {
    showErrorMessage("Session already stopped.");
  }
}

// ----------------------------------------------------------------------------

function showDashboard(content) {
  $('#dashboard_menu').fadeIn();
  if (!$('#googleMap').length) {
    hideMessage();
    $('#page_content').load('dashboard.html #content', function () {
      // loadScript();
      initializeMap();
      // --
      if (content == "user-list") {
        $('#dashboard_user_panel').show();
        $('#dashboard_user_refresh').click(buttonHandlerRefreshUserList);
        sendRequestUserList(false);
      } else if (content == "session-list") {
        $('#dashboard_session_panel').show();
        $('#dashboard_session_refresh').click(buttonHandlerRefreshSessionList);
        $('#dashboard_session_search').keyup(searchObservationKeyUp);
        sendRequestSessionList(true);
      }
      // --
      $('#jumbotron').hide();
      $('#navbar_form_logout').fadeIn();
      $('#navbar_form_login').hide();
      $('#page_content').hide().fadeIn();
    });
  } else {
    if (content == "user-list") {
      $('#dashboard_session_panel').hide();
      $('#dashboard_user_panel').fadeIn();
      $('#dashboard_user_refresh').click(buttonHandlerRefreshUserList);
      sendRequestUserList(false);
    } else if (content == "session-list") {
      $('#dashboard_user_panel').hide();
      $('#dashboard_session_panel').fadeIn();
      $('#dashboard_session_refresh').click(buttonHandlerRefreshSessionList);
      sendRequestSessionList(true);
    }
  }
  scrollToTop();
}

/* ----------------------------------------------------------------------------
Handler functions for messages received from the server
--------------------------------------------------------------------------- */

//handle registration response
function handleResponseRegistration(data) {
  console.log('Handle Registration Response');
  //success
  if (data["message-type"] === 'response-ok') {
    // Perform auto login
    setCookie("session_id", data["message"], 7);
    $('#dashboard_menu').fadeIn();
    $('#navbar_form_logout').fadeIn();
    $('#navbar_form_login').hide();
    
    currentUsername = $("#register_username").val();
    $('#navbar_user_name').html("Welcome " + currentUsername + "!<b class=\"caret\">");
    
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
function handleResponseLogin(data) {
  console.log('Handle Login Response');
  //success
  if (data["message-type"] === 'response-ok') {
    setCookie("session_id", data["message"], 7);
    // --
    $('#login_username').val('');
    $('#login_password').val('');
    $('#navbar_user_name').html("Welcome " + currentUsername + "!<b class=\"caret\">");
    // --
    showDashboard("user-list");
  } else {
    currentUsername = null;
    showErrorMessage("<b>Error!</b> " + data["message"]);
  }
}

// ----------------------------------------------------------------------------

function handleResponseSessionCheck(data) {
  console.log('Handle Session Check Response');
  // --
  if (data["message-type"] === 'response-ok') {
    console.log(data);
    currentUsername = data["message"];
    $('#navbar_user_name').html("Welcome " + currentUsername + "!<b class=\"caret\">");
    $('#navbar_form_logout').show();
    $('#navbar_form_login').hide();
    $('#dashboard_menu').fadeIn();
  } else {
    currentUsername = null;
    //remove invalid session id
    eraseCookie("session_id");
    // --
    $('#dashboard_menu').hide();
    $('#navbar_form_logout').hide();
    $('#navbar_form_login').fadeIn();
  }
}

// ----------------------------------------------------------------------------

function handleResponseUserList(data) {
  console.log('Handle User List Response');
  // --
  if (data["message-type"] === 'response-list') {
    userList = new Array();
    var i = 1;
    $('#dashboard_list_users').html("");
    for (var user in data["list"]) {
      var name = data["list"][user]["name"];
      var observable = data["list"][user]["observable"];
      var linkId = "dashboard_list_user_" + i;
      // --
      userList.push({
        "name" : name,
        "observable" : observable,
        "labelid" : linkId,
        "buttonid" : linkId
      });
      // --
      var html = "<a href=\"#\" class=\"list-group-item\" id=\"" + linkId + "\">" + name + "";
      if (observable === true) {
        html += "<i class=\"glyphicon fui-check\" style=\"float: right; margin-top: 6px; margin-right: 10px\"/>";
      }
      html += "</a>";
      // --
      $('#dashboard_list_users').append(html);
      // --
      $('#' + linkId).click({
        id : linkId,
        user : name,
        observable : observable
      }, function (event) {
        listItemHandlerStartObservation(event.data.user, event.data.observable);
      });
      // --
      i++;
    }
  }
}

var observationList;
var userList;
var currentObservation;

// ----------------------------------------------------------------------------

function handleResponseSessionList(data) {
  console.log('Handle Session List Response');
  // --
  if (data["message-type"] === 'response-list') {
    observationList = new Array();
    var i = 1;
    $('#dashboard_list_sessions').html("");
    for (var session in data["list"]) {
      var observationid = data["list"][session]["observation-id"];
      var name = data["list"][session]["observed"];
      var starttime = data["list"][session]["starttime"];
      var endtime = data["list"][session]["endtime"];
      // --
      var entryId = "dashboard_list_session_observed_" + i;
      var buttonId = "dashboard_list_session_observed_cancel_" + i;
      // --
      observationList.push({
        "id" : observationid,
        "user" : name,
        "starttime" : starttime,
        "endtime" : endtime,
        "elementid" : entryId,
        "buttonid" : buttonId
      });
      // --
      html = generateSessionEntryHtml(entryId, buttonId, name, starttime, endtime);
      // --
      $('#dashboard_list_sessions').append(html);
      // --
      $('#' + entryId).click({
        entryId : entryId,
        observationid : observationid
      }, function (event) {
        sendRequestSessionPoints(event.data.observationid);
        currentObservation = event.data.observationid;
      });

      $('#' + buttonId).click({
        buttonId : buttonId,
        observationid : observationid,
        user : name,
        starttime : starttime,
        endtime : endtime
      }, function (event) {
        listItemHandlerStopObservation(event.data.observationid, event.data.user, event.data.starttime, event.data.endtime);
      });
      // --
      i++;
    }
  }
}

function generateSessionEntryHtml(entryId, buttonId, name, starttime, endtime) {
  var html = "<a href=\"#\" id=\"" + entryId + "\" class=\"list-group-item\"><small>"
     + formatDate(starttime) + "</small><br><b>"
     + name;
  if (endtime) {
    html += "<i class=\"glyphicon fui-check\" style=\"float: right; margin-top: 6px; margin-right: 10px\"/>";
    html += "</b><br><small>"
    html += formatTimestamp(starttime);
    html += " - " + formatTimestamp(endtime) + "</small>"
  } else {
    html += "<button id=\"" + buttonId + "\" class=\"btn btn-xs btn-primary btn-circle\" style=\"float: right; margin-top:-2px; margin-right:3px\">";
    html += "<i class=\"glyphicon fui-cross\"/></button>";
    html += "</b><br><small>"
    html += formatTimestamp(starttime);
    html += "</small>"
  }
  html += "</a>";
  // --
  return html;
}

// ----------------------------------------------------------------------------


function handleResponseSessionPoints(data) {
  console.log('Handle Session Points Response');
  // --
  if (data["message-type"] === 'response-list') {
    setPath(data["list"]);
  }
}

// ----------------------------------------------------------------------------

function handleResponseStartObservation(data) {
  console.log('Handle Start Observation Response');
  //success
  if (data["message-type"] === 'response-ok') {
    showSuccessMessage("You are now observing " + data["message"]);
    sendRequestSessionList(true, false);
  } else {
    showErrorMessage("<b>Error!</b> " + data["message"]);
  }
}

// ----------------------------------------------------------------------------

function handleResponseStopObservation(data) {
  console.log('Handle Stop Observation Response');
  //success
  if (data["message-type"] === 'response-ok') {
    showSuccessMessage("You have stopped observing " + data["message"]); // TODO improve message if observation of oneself was stopped
    sendRequestSessionList(true, false);
  } else {
    showErrorMessage("<b>Error!</b> " + data["message"]);
  }
}

// ----------------------------------------------------------------------------

function handleNotification(data) {
  console.log('Handle Notification');
  var message = data["message"];
  showInfoMessage(message);
}

function handleLocationUpdateNotification(data) {
  console.log('Handle Location Update Notification')
  // --
  var username = data["username"];
  var longitude = data["longitude"];
  var latitude = data["latitude"];
  var accuracy = data["accuracy"];
  // --
  // Look for username in current session list
  for (var session in sessionList) {
    var sessionUser = session["username"];
    if (username === sessionUser) {
      var sessionId = session["id"];
      if (sessionId == currentObservation) {
        // TODO extend current path
      }
    }
  }
}

/* ----------------------------------------------------------------------------
Messages sent to the server go here
--------------------------------------------------------------------------- */

function sendRegistrationRequest(username, password, observable) {
  var mcid = ++cid;
  var request = {
    "cid" : mcid,
    "message-type" : "request-registration",
    "username" : username,
    "password" : password,
    "observable" : observable
  };
  conversations[mcid] = "request-registration";
  socket.send(JSON.stringify(request));
}

function sendLoginRequest(username, password) {
  var mcid = ++cid;
  var request = {
    "cid" : mcid,
    "message-type" : "request-login",
    "username" : username,
    "password" : password
  };
  conversations[mcid] = "request-login";
  socket.send(JSON.stringify(request));
  currentUsername = username;
}

function sendSessionCheck() {
  var mcid = ++cid;
  var sessionId = getCookie("session_id");
  if (!sessionId) {
    sessionId = "not_init"; // workaround for quick-json bug that makes it unable to handle empty fields
  }
  var request = {
    "cid" : mcid,
    "message-type" : "request-session-check",
    "session-id" : sessionId
  };
  conversations[mcid] = "request-session-check";
  socket.send(JSON.stringify(request));
}

function sendLogout() {
  var mcid = ++cid;
  var request = {
    "cid" : mcid,
    "message-type" : "request-logout",
    "session-id" : getCookie("session_id")
  };
  conversations[mcid] = "request-logout";
  socket.send(JSON.stringify(request));
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

function sendRequestUserList(observableOnly) {
  var mcid = ++cid;
  var request = {
    "cid" : mcid,
    "message-type" : "request-user-list",
    "session-id" : getCookie("session_id"),
    "observable-only" : observableOnly
  };
  conversations[mcid] = "request-user-list";
  socket.send(JSON.stringify(request));
}

function sendRequestSessionList(listObserved, listObservers) {
  var mcid = ++cid;
  var request = {
    "cid" : mcid,
    "message-type" : "request-session-list",
    "session-id" : getCookie("session_id"),
    "list-observed" : listObserved,
    "list-observers" : listObservers
  };
  conversations[mcid] = "request-session-list";
  socket.send(JSON.stringify(request));
}

function sendRequestSessionPoints(observationid) {
  var mcid = ++cid;
  var request = {
    "cid" : mcid,
    "message-type" : "request-session-points",
    "session-id" : getCookie("session_id"),
    "observation-id" : observationid
  };
  conversations[mcid] = "request-session-points";
  socket.send(JSON.stringify(request));
}

function sendRequestStartObservation(name) {
  var mcid = ++cid;
  var request = {
    "cid" : mcid,
    "message-type" : "request-start-observation",
    "session-id" : getCookie("session_id"),
    "user" : name
  };
  conversations[mcid] = "request-start-observation";
  socket.send(JSON.stringify(request));
}

function sendRequestStopObservation(observationid, name, userIsObserver) {
  var mcid = ++cid;
  var request = {
    "cid" : mcid,
    "message-type" : "request-stop-observation",
    "session-id" : getCookie("session_id"),
    "observation-id" : observationid,
    "user" : name,
    "user-is-observer" : userIsObserver
  };
  conversations[mcid] = "request-stop-observation";
  socket.send(JSON.stringify(request));
}

/* ----------------------------------------------------------------------------
Client Side UI Functions
--------------------------------------------------------------------------- */
function searchObservationList(query) {
  for (var observation in observationList) {
    var elementId = observationList[observation]["elementid"];
    var username = observationList[observation]["user"];
    // --
    if (query && username && username.indexOf(query) == -1) {
      $('#' + elementId).hide();
    } else {
      $('#' + elementId).show();
    }
  }
}

/* ----------------------------------------------------------------------------
Utilities
--------------------------------------------------------------------------- */

function scrollToTop(){
	$('html, body').animate({scrollTop: '0px'}, 800);
}

function showSuccessMessage(message) {
  $("#page_alert").removeClass("alert-danger");
  $("#page_alert").removeClass("alert-info");
  $("#page_alert").addClass("alert-success");
  $("#page_alert").html(message);
  makeAlertDismissable();
  scrollToTop();
  $("#page_alert").fadeIn();
}

function showErrorMessage(message) {
  $("#page_alert").removeClass("alert-success");
  $("#page_alert").removeClass("alert-info");
  $("#page_alert").addClass("alert-danger");
  $("#page_alert").html(message);
  makeAlertDismissable();
  scrollToTop();
  $("#page_alert").fadeIn();
  
}

function showInfoMessage(message) {
  $("#page_alert").removeClass("alert-success");
  $("#page_alert").removeClass("alert-danger");
  $("#page_alert").addClass("alert-info");
  $("#page_alert").html(message);
  makeAlertDismissable();
  scrollToTop();
  $("#page_alert").fadeIn();
}

function hideMessage() {
  $("#page_alert").fadeOut();
}

function makeAlertDismissable()
{
	$("#page_alert").addClass("alert-dismissable");
	$("#page_alert").append( "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>" );
}

function formatTimestamp(milliseconds) {
  var d = new Date(milliseconds);
  // --
  var str = d.getHours() + ":" + (d.getMinutes() < 10 ? "0" : "") + d.getMinutes();
  // --
  return str;
}

function formatDate(milliseconds) {
  var d = new Date(milliseconds);
  // --
  var str = d.getDate() + "." + (d.getMonth() + 1) + ".";
  // --
  return str;
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
    zoom : 12,
    mapTypeId : google.maps.MapTypeId.TERRAIN
  };
  map = new google.maps.Map(document.getElementById("googleMap"), mapProp);

  setMapToCurrentPosition();

  $(window).resize(function () {
    google.maps.event.trigger(map, "resize");
  });

  // Register location tracker
  if (navigator.geolocation) {
    console.log("locationUpdate");
    navigator.geolocation.watchPosition(positionChanged);
  }
}

// ----------------------------------------------------------------------------

function clearMarkers() {
  if (markers) {
    while (markers.length > 0) {
      var marker = markers.pop();
      marker.setMap(null);
    }
  }
}

function addMarker(marker) {
  if (!markers) {
    markers = new Array();
  }
  markers.push(marker);
  marker.setMap(map);
}

// ----------------------------------------------------------------------------

function clearPath() {}

function setPath(coordList) {
  // clear path
  if (path) {
    path.setMap(null);
  }
  // clear points
  if (points) {
    while (points.length > 0) {
      var point = points.pop();
      point.setMap(null);
    }
  } else {
    points = new Array();
  }
  // clear markers
  clearMarkers();
  // --
  if (!coordList || coordList.length == 0) {
    console.log("no points");
    setMapToCurrentPosition();
  } else {
    var pointList = [];
    var bounds = new google.maps.LatLngBounds();

    // build path and add points
    for (var point in coordList) {
      var timestamp = coordList[point]["timestamp"];
      var latitude = coordList[point]["latitude"];
      var longitude = coordList[point]["longitude"];
      var accuracy = coordList[point]["accuracy"];
      accuracy = accuracy == 0 ? 0 : accuracy / 100;
      // --
      var latlng = new google.maps.LatLng(latitude, longitude);
      pointList.push(latlng);
      bounds.extend(latlng);
      // --
      var pathPoint = {
        strokeColor : '#FF0000',
        strokeOpacity : 0.8,
        strokeWeight : 2,
        fillColor : '#FF0000',
        fillOpacity : 0.35,
        map : map,
        center : latlng,
        radius : accuracy
      };
      // --
      points.push(new google.maps.Circle(pathPoint));
    }

    // Set Path
    path = new google.maps.Polyline({
        path : pointList,
        geodesic : true,
        strokeColor : '#FF0000',
        strokeOpacity : 1.0,
        strokeWeight : 2
      });
    path.setMap(map);

    // Add Markers
    if (pointList.length > 0) {
      var start = pointList[0];
      var startMarker = new google.maps.Marker({
          position : start,
          map : map,
          title : 'Start'
        });
      addMarker(startMarker);
      // --
      if (pointList.length > 1) {
        var end = pointList[0];
        var endMarker = new google.maps.Marker({
            position : end,
            map : map,
            title : 'Finish'
          });
        addMarker(endMarker);
      }
    }

    // Set map bounds
    map.fitBounds(bounds);
  }
}
// ----------------------------------------------------------------------------

function positionChanged(position) {
  if (currentUsername) {
    var myLatlng = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
    sendLocationUpdate(position);
  }
}

function setMapToCurrentPosition() {
  if (navigator.geolocation) {
    browserSupportFlag = true;
    navigator.geolocation.getCurrentPosition(function (position) {
      console.log("position update");
      var myLatLng = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
      // --
      clearMarkers();
      var marker = new google.maps.Marker({
          position : myLatLng,
          map : map,
          title : 'You are here'
        });
      addMarker(marker);
      // --
      map.setCenter(myLatLng);
    }, function () {
      handleNoGeolocation(browserSupportFlag);
    });
  }
  // Browser doesn't support Geolocation
  else {
    browserSupportFlag = false;
    handleNoGeolocation(browserSupportFlag);
  }
}

// ----------------------------------------------------------------------------

function handleNoGeolocation(errorFlag) {
  if (errorFlag == true) {
    console.log("Geolocation service failed.");
    map.setCenter(new google.maps.LatLng(51.508742, -0.120850));
  } else {
    console.log("Your browser doesn't support geolocation. We've placed you in Siberia.");
    map.setCenter(new google.maps.LatLng(60, 105));
  }
}
