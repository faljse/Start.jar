package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Config {
	private static HashMap<String, String> params = new HashMap<>();

	/**
	 * Initialize defaults
	 */
	public static void initDefaults() {
		params.put("DocumentRoot", ".");
		params.put("Host", "127.0.0.1");
		params.put("Port", "8888");
		params.put("ShowFrame", "true");
		params.put("ShutdownAfter", "300");
		params.put("DefaultFile", "index.html");
	}

	public static void loadConfig(String fileName) {
		File configFile = new File(fileName);

		if (configFile.exists()) {
			FileInputStream fis;
			try {
				fis = new FileInputStream(configFile.getAbsolutePath());
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				String line;
				while ((line = br.readLine()) != null) {
					String confItem[] = line.split("=", 2);
					if (confItem.length == 2) {
						params.put(confItem[0], confItem[1]);
					}
				}
			} catch (Exception e) {
				System.err.printf("ERROR: %s\n", e.getMessage());
			}
		} else {
			System.err.printf("ERROR: Couldn't load config from file <%s>. File does not exists.\n", fileName);
		}
	}

	/**
	 * Get string param value
	 * 
	 * @param param
	 * @return
	 */
	public static String get(String param) {
		if (params.containsKey(param)) {
			return params.get(param);
		}

		return null;
	}

	/**
	 * Get integer param value
	 * 
	 * @param param
	 * @return
	 */
	public static int geti(String param) {
		String value = get(param);
		if (value != null) {
			return Integer.valueOf(value);
		}

		return 0;
	}

	/**
	 * Get boolean param value
	 * 
	 * @param param
	 * @return
	 */
	public static boolean getb(String param) {
		String value = get(param);
		if (value != null) {
			return value.toLowerCase().equals("true");
		}

		return false;
	}
}
