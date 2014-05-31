package at.jku.geotracker.application;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.support.v4.widget.DrawerLayout;
import android.widget.LinearLayout;
import at.jku.geotracker.activity.menu.MainMenuItem;
import at.jku.geotracker.ws.client.WSClient;
import at.jku.geotracker.ws.ssl.MySSLSocketFactory;

public class Globals extends Application {

	public static String restUrl = "https://192.168.1.149:8443/rest";
	public static String wssUrl = "wss://192.168.1.149:8443";
	// public static String wssUrl = "wss://schnelleflitzer.at";

	public static String username = null;
	public static String password = null;

	private LinearLayout menuLayout = null;
	private DrawerLayout drawerLayout = null;

	private WSClient c = null;

	private static String sessionId = null;

	public String getRestUrl() {
		return Globals.restUrl;
	}

	public static String getSessionId() {
		return sessionId;
	}

	public static void setSessionId(String sessionId) {
		Globals.sessionId = sessionId;
	}

	public void setMenuLayout(LinearLayout menuLayout, DrawerLayout drawerLayout) {
		this.menuLayout = menuLayout;
		this.drawerLayout = drawerLayout;
	}

	public void closeMenu() {
		if (this.menuLayout != null && this.drawerLayout != null) {
			this.drawerLayout.closeDrawer(menuLayout);
		}
	}

	public void openMenu() {
		if (this.menuLayout != null && this.drawerLayout != null) {
			this.drawerLayout.openDrawer(menuLayout);
		}
	}

	public String getWSSUrl() {
		return Globals.restUrl;
	}

	public ArrayList<MainMenuItem> getMenuList() {
		ArrayList<MainMenuItem> menuList = new ArrayList<MainMenuItem>();
		MainMenuItem m = new MainMenuItem();
		m.setTitle("Userliste");
		menuList.add(m);

		MainMenuItem m1 = new MainMenuItem();
		m1.setTitle("Einstellungen");
		menuList.add(m1);

		MainMenuItem m2 = new MainMenuItem();
		m2.setTitle("Beobachtungen");
		menuList.add(m2);

		return menuList;
	}

	public WSClient getWsClient() {
		return this.c;
	}

	public void initWSS() {

		try {
			c = new WSClient(new URI(Globals.wssUrl));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		KeyStore trustStore = null;
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		try {
			trustStore.load(null, null);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		MySSLSocketFactory sf = null;
		try {
			sf = new MySSLSocketFactory(trustStore);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		try {
			c.setSocket(sf.createSocket());
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			c.connectBlocking();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		JSONObject jo = new JSONObject();
		try {
			jo.put("message-type", "request-login");
			jo.put("cid", "1001");
			jo.put("username", Globals.username);
			jo.put("password", Globals.password);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		c.send(jo.toString());

	}
}
