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
import org.eclipse.jetty.websocket.api.extensions.ExtensionFactory;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import at.jku.se.tracking.database.DatabaseService;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements a simple web server providing web socket capabilities on secure connections.
 * 
 * @author markus.hofmarcher
 */
public class WebSocketServer {

	public static String WORKING_DIR;

	// ------------------------------------------------------------------------

	private static final int DEFAULT_PORT = 448;

	// ------------------------------------------------------------------------

	private Server server;
	private String host;
	private int port;
	private Resource keyStoreResource;
	private String keyStorePassword;
	private String keyManagerPassword;
	private List<Handler> webSocketHandlerList = new ArrayList<>();

	// ------------------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		int port = DEFAULT_PORT;
		// --
		for (String a : args) {
			String[] arg = parseArgument(a);
			// --
			if (arg.length == 2) {
				switch (arg[0]) {
				case "-dir":
					File workingDir = new File(arg[1]);
					if (workingDir.exists() && workingDir.isDirectory()) {
						WORKING_DIR = workingDir.getCanonicalPath();
					} else {
						System.err.println("working dir not exist or is invalid (" + workingDir.getAbsolutePath() + ")");
						return;
					}
					break;
				case "-port":
					try {
						port = Integer.parseInt(arg[1]);
					} catch (NumberFormatException e) {
						System.err.println("invalid port (" + arg[1] + ")");
						return;
					}
					break;
				case "-h":
				case "-help":
				case "--help":
					System.out.println("java -jar <jar-name> [-dir:<working-directory>] [-port:<portnumber>]");
					break;
				}
			}
		}
		// --
		if (WORKING_DIR == null) {
			File dir = new File(WebSocketServer.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			if (dir.isFile()) {
				WORKING_DIR = dir.getParent();
			} else {
				WORKING_DIR = dir.getPath();
			}
		}
		// --
		System.out.println("Working Directory: " + WORKING_DIR);
		System.out.println("Starting server on port '" + port + "'...");
		// --
		WebSocketServer webSocketServer = new WebSocketServer();
		// Host / Port
		webSocketServer.setHost("0.0.0.0");
		webSocketServer.setPort(port);
		// SSL Keystore
		webSocketServer.setKeyStoreResource(new FileResource(new URL("file://" + WORKING_DIR + File.separator + "resources/keystore.jks")));
		webSocketServer.setKeyStorePassword("password");
		webSocketServer.setKeyManagerPassword("password");
		// Register WebSocket handler
		webSocketServer.addWebSocket(WebSocketSession.class, "/");
		// Initialize and start the server
		webSocketServer.initialize();
		webSocketServer.start();
	}

	// ------------------------------------------------------------------------

	private static String[] parseArgument(String arg) {
		if (arg.contains(":")) {
			int idx = arg.indexOf(":");
			return new String[] { arg.substring(0, idx), arg.substring(idx + 1) };
		} else {
			return new String[] { arg };
		}
	}

	// ------------------------------------------------------------------------

	public void initialize() {
		server = new Server();
		// connector configuration
		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.setKeyStoreResource(keyStoreResource);
		sslContextFactory.setKeyStorePassword(keyStorePassword);
		sslContextFactory.setKeyManagerPassword(keyManagerPassword);
		SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString());
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
				// workaround for bug in jetty > 9.1.2 with compression of large messages
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=431459
				// WARNING: disables message compression -> might be better to revert to
				// Jetty 9.1.2; remove as soon as bug is fixed
				ExtensionFactory extFactory = webSocketServletFactory.getExtensionFactory();
				// --
				extFactory.unregister("deflate-frame");
				extFactory.unregister("permessage-deflate");
				extFactory.unregister("x-webkit-deflate-frame");
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
