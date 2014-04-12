package at.jku.se.tracking;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TrackingWebSocketClient {

	public static void main(String[] args) throws IOException, URISyntaxException {
		new TrackingWebSocketClient().run(new URI("wss://localhost:8443/"));
	}

	public void run(URI destinationUri) throws IOException {

		SslContextFactory sslContextFactory = new SslContextFactory();
		Resource keyStoreResource = Resource.newResource(this.getClass().getClassLoader()
				.getResource("resources/keystore.jks"));

		sslContextFactory.setKeyStoreResource(keyStoreResource);
		sslContextFactory.setKeyStorePassword("password");
		sslContextFactory.setKeyManagerPassword("password");
		WebSocketClient client = new WebSocketClient(sslContextFactory);
		MyWebSocket socket = new MyWebSocket();
		try {
			client.start();
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			System.out.println("Connecting to : " + destinationUri);
			client.connect(socket, destinationUri, request);
			socket.awaitClose(5, TimeUnit.SECONDS);
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			try {
				client.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@WebSocket
	public class MyWebSocket {
		private final CountDownLatch closeLatch = new CountDownLatch(1);

		@OnWebSocketConnect
		public void onConnect(Session session) {
			System.out.println("WebSocket Opened in client side");
			try {
				System.out.println("Sending message: Hi server");
				session.getRemote().sendString("Hi Server");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@OnWebSocketMessage
		public void onMessage(String message) {
			System.out.println("Message from Server: " + message);
		}

		@OnWebSocketClose
		public void onClose(int statusCode, String reason) {
			System.out.println("WebSocket Closed. Code:" + statusCode);
		}

		public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
			return this.closeLatch.await(duration, unit);
		}
	}

}