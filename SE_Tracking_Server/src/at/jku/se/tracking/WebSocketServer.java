package at.jku.se.tracking;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import at.jku.se.tracking.database.DatabaseService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements a simple web server providing web socket capabilities on secure connections.
 * 
 * @author markus.hofmarcher
 */
public class WebSocketServer {
	private Server server;
	private String host;
	private int port;
	private Resource keyStoreResource;
	private String keyStorePassword;
	private String keyManagerPassword;
	private List<Handler> webSocketHandlerList = new ArrayList<>();

	// ------------------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		WebSocketServer webSocketServer = new WebSocketServer();
		// Host / Port
		webSocketServer.setHost("0.0.0.0");
		webSocketServer.setPort(8443);
		// SSL Keystore
		webSocketServer.setKeyStoreResource(new FileResource(WebSocketServer.class.getClassLoader().getResource(
				"resources/keystore.jks")));
		webSocketServer.setKeyStorePassword("password");
		webSocketServer.setKeyManagerPassword("password");
		// Register WebSocket handler
		webSocketServer.addWebSocket(WebSocketSession.class, "/");
		// Initialize and start the server
		webSocketServer.initialize();
		webSocketServer.start();
	}

	// ------------------------------------------------------------------------

	public void initialize() {
		server = new Server();
		// connector configuration
		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.setKeyStoreResource(keyStoreResource);
		sslContextFactory.setKeyStorePassword(keyStorePassword);
		sslContextFactory.setKeyManagerPassword(keyManagerPassword);
		SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory,
				HttpVersion.HTTP_1_1.asString());
		// --
		HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(new HttpConfiguration());
		// --
		ServerConnector sslConnector = new ServerConnector(server, sslConnectionFactory, httpConnectionFactory);
		sslConnector.setHost(host);
		sslConnector.setPort(port);
		// --
		server.addConnector(sslConnector);
		// handler configuration
		HandlerCollection handlerCollection = new HandlerCollection();
		handlerCollection.setHandlers(webSocketHandlerList.toArray(new Handler[0]));
		// static files handler
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setResourceBase("./webapp/");
		handlerCollection.addHandler(resourceHandler);
		// --
		server.setHandler(handlerCollection);
	}

	// ------------------------------------------------------------------------

	public void addWebSocket(final Class<?> webSocket, String pathSpec) {
		WebSocketHandler wsHandler = new WebSocketHandler() {
			@Override
			public void configure(WebSocketServletFactory webSocketServletFactory) {
				webSocketServletFactory.register(webSocket);
			}
		};
		ContextHandler wsContextHandler = new ContextHandler();
		wsContextHandler.setHandler(wsHandler);
		wsContextHandler.setContextPath(pathSpec);
		webSocketHandlerList.add(wsHandler);
	}

	// ------------------------------------------------------------------------

	public void start() throws Exception {
		server.start();
		server.join();
		// --
		server.addLifeCycleListener(new LifeCycle.Listener() {

			@Override
			public void lifeCycleStopping(LifeCycle arg0) {
				// release resources
				try {
					DatabaseService.releaseResources();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void lifeCycleStopped(LifeCycle arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void lifeCycleStarting(LifeCycle arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void lifeCycleStarted(LifeCycle arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void lifeCycleFailure(LifeCycle arg0, Throwable arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	// ------------------------------------------------------------------------

	public void stop() throws Exception {
		server.stop();
		server.join();
	}

	// ------------------------------------------------------------------------
	// Getter / Setter
	// ------------------------------------------------------------------------

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setKeyStoreResource(Resource keyStoreResource) {
		this.keyStoreResource = keyStoreResource;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	public void setKeyManagerPassword(String keyManagerPassword) {
		this.keyManagerPassword = keyManagerPassword;
	}
}
