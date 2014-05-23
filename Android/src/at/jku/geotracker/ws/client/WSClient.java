package at.jku.geotracker.ws.client;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

/**
 * This example demonstrates how to create a websocket connection to a server.
 * Only the most important callbacks are overloaded.
 */
public class WSClient extends WebSocketClient {

	public WSClient(URI serverUri) {
		super(serverUri);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("opened connection");
	}

	@Override
	public void onMessage(String message) {
		System.out.println("received: " + message);
	}

	@Override
	public void onFragment(Framedata fragment) {
		System.out.println("received fragment: "
				+ new String(fragment.getPayloadData().array()));
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// The codecodes are documented in class
		// org.java_websocket.framing.CloseFrame
		System.out.println("Connection closed code: " + code);
	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();
	}
}