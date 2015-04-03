package core;

import java.awt.Desktop;
import java.io.File;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.sun.net.httpserver.HttpServer;

public class Main {

	private static InetSocketAddress addr;
	private static HttpServer srv;

	public static boolean showFrame = true;
	public static String appName = "Start.jar";
	public static String documentRoot;
	public static String bindHost;
	public static String defaultFile;
	public static int port_min;
	public static int port_max;
	public static int shutdownAfter;
	public static long lastRequestTime;
	private static Timeout timeOutThread;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Config.initDefaults();

		File defaultConfig = new File(System.getProperty("user.dir") + File.separatorChar + "start.config");

		if (defaultConfig.exists()) {
			System.out.printf("Loading config from %s\n", defaultConfig);
			Config.loadConfig(defaultConfig.getAbsolutePath());
		} else {
			System.err.printf("Config file not found [%s]\n", defaultConfig);
			parseArgs(args);
		}

		populateConfig();

		if ((documentRoot.length() == 1) && (documentRoot.equals("."))) {
			documentRoot = System.getProperty("user.dir");
		} else {
			if ((documentRoot.substring(0, 2).equals("./")) || (documentRoot.substring(0, 2).equals(".\\"))) {
				documentRoot = System.getProperty("user.dir") + File.separator + documentRoot.substring(2);
			}
		}

		if ((documentRoot.charAt(documentRoot.length() - 1) == '/') || ((documentRoot.charAt(documentRoot.length() - 1) == '\\'))) {
			documentRoot = documentRoot.substring(0, documentRoot.length() - 1);
		}

		if (new File(documentRoot).exists()) {
			System.out.printf("Document root: %s\n", documentRoot);
		} else {
			System.err.printf("ERROR: Document root [%s] is not valid directory path\n", documentRoot);
			System.exit(1);
		}

		startServer(bindHost, port_min);
	}

	public static void startServer(String host, int port) {
		addr = new InetSocketAddress(host, port);
		String serverUrl = "http://" + host + ":" + String.valueOf(port);

		try {
			if (showFrame) {
				JFrame frame = new JFrame(appName);
				frame.setSize(200, 100);

				JLabel label = new JLabel("<html><p>Running " + appName + " at <a href=\"" + serverUrl + "\">" + serverUrl + "</a></p>" + "<p>Document root is <b>" + documentRoot + "</b></p>"
						+ "<p>Just close this window to stop the server</p></html>");

				frame.add(label);
				frame.pack();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}

			srv = HttpServer.create(addr, 10);
			srv.createContext("/", new FileHandler());
			System.out.printf("Server started at %s\n", serverUrl);
			openWebpage(new URL(serverUrl));
			srv.start();
			lastRequestTime = new Date().getTime();

			timeOutThread = new Timeout();
			timeOutThread.start();
		} catch (BindException be) {
			if (port + 1 < port_max) {
				System.out.printf("WARNING: Couldn't bind an address %s: %s\n", serverUrl, be.getMessage());
				startServer(host, port + 1);
			} else {
				System.err.printf("ERROR: All ports in range from %d to %d is already in use! Shutting down", port_min, port_max);
			}
		} catch (Exception e) {
			System.err.printf("ERROR: %s\n", e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * quit
	 * 
	 */
	public static void quit() {
		if (srv != null) {
			srv.stop(0);
		}
		System.exit(0);
	}

	/**
	 * Populate configuration
	 * 
	 */
	public static void populateConfig() {
		bindHost = Config.get("Host");
		port_min = Integer.valueOf(Config.get("PortMin"));
		port_max = Integer.valueOf(Config.get("PortMax"));
		showFrame = Config.get("ShowFrame").equals("1");
		documentRoot = Config.get("DocumentRoot");
		defaultFile = Config.get("DefaultFile");
		shutdownAfter = Integer.valueOf(Config.geti("ShutdownAfter"));
	}

	private static void help() {
		System.out.printf("Usage:\tjava -jar start.jar [options]\n\n");
		System.out.println("Options:");
		System.out.println("  -h, --host \t\tHost to bind to (default: 127.0.0.1)");
		System.out.println("  -pm, --port-min \tMinimal port to bind to (default: 8000)");
		System.out.println("  -px, --port-max \tMaximum port to bind to (default: 9000)");
		System.out.println("  -w, --frame \t\tShow the control window");
		System.out.println("  -r, --root \t\tDocument root for the server (default is the current working directory)");
		System.out.println("  -c, --config \t\tLoad the configuration from the file");
		System.out.println("  -i, --index \t\tDefault file name (default: index.html)");
		System.out.println("  -S, --shutdown-after \tTime (in seconds) to shutdown the server after the last request");
	}

	/**
	 * Parse arguments
	 * 
	 * @param args
	 */
	private static void parseArgs(String[] args) {
		if (args.length > 0) {
			if (args[0].equals("--help")) {
				help();
				quit();
			}

			int currentArg = 0;
			do {
				String arg = args[currentArg];

				if (arg.equals("--host") || arg.equals("-h")) {
					Config.set("Host", args[currentArg + 1]);
					currentArg++;
				}

				if (arg.equals("--port-min") || arg.equals("-pm")) {
					Config.set("PortMin", args[currentArg + 1]);
					currentArg++;
				}

				if (arg.equals("--port-max") || arg.equals("-px")) {
					Config.set("PortMax", args[currentArg + 1]);
					currentArg++;
				}

				if (arg.equals("--frame") || arg.equals("-w")) {
					Config.set("ShowFrame", args[currentArg + 1]);
					currentArg++;
				}

				if (arg.equals("--root") || arg.equals("-r")) {
					Config.set("DocumentRoot", args[currentArg + 1]);
					currentArg++;
				}

				if (arg.equals("--config") || arg.equals("-c")) {
					Config.loadConfig(args[currentArg + 1]);
					populateConfig();
					currentArg++;
				}

				if (arg.equals("--index") || arg.equals("-i")) {
					Config.set("DefaultFile", args[currentArg + 1]);
				}

				if (arg.equals("--shutdown-after") || arg.equals("-S")) {
					Config.set("ShutdownAfter", args[currentArg + 1]);
				}

				currentArg++;
			} while (currentArg < args.length);
		}
	}

	public static void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void openWebpage(URL url) {
		try {
			openWebpage(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

}
