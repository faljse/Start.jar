package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Date;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class FileHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange h) throws IOException {
		Main.lastRequestTime = new Date().getTime();

		System.out.println(h.getRequestMethod() + " " + h.getRequestURI());
		OutputStream os = h.getResponseBody();
		String filePath = h.getRequestURI().toString();

		if (filePath.equals("/"))
			filePath = "/" + Main.defaultFile;

		filePath = Main.documentRoot + filePath;

		filePath = filePath.replace('/', File.separatorChar);

		File f = new File(filePath);

		if (f.isFile()) {
			FileInputStream fs = new FileInputStream(f);

			Headers hdr = h.getResponseHeaders();

			String mime;

			String[] fileNameParts = f.getName().split("\\.");

			if (fileNameParts.length > 0) {
				String extension = fileNameParts[fileNameParts.length - 1].toLowerCase();

				switch (extension) {
				case "html":
				case "htm":
				case "xhtml":
					mime = "text/html";
					break;
				case "jpg":
				case "jpeg":
					mime = "image/jpeg";
					break;
				case "png":
					mime = "image/png";
					break;
				case "gif":
					mime = "image/gif";
					break;
				case "css":
					mime = "text/css";
					break;
				case "js":
					mime = "application/javascript";
					break;
				case "json":
					mime = "application/json";
					break;
				default:
					System.out.printf("Cannot find a MIME-type for the extension: %s", extension);
					mime = "text/html";

				}
			} else {
				mime = Files.probeContentType(f.toPath());
				if (mime == null) {
					mime = "application/octet-stream";
				}
			}

			hdr.add("Content-Type", mime);
			hdr.add("Content-Length", String.valueOf(fs.available()));
			h.sendResponseHeaders(200, 0);

			final byte[] buffer = new byte[0x10000];
			int count = 0;
			while ((count = fs.read(buffer)) >= 0) {
				os.write(buffer, 0, count);
			}
			fs.close();
			os.close();

			h.close();
		} else {
			System.out.printf("File %s not found!\n", filePath);
			String error404 = "<h1>404 File not found</h1><p>Couldn't locate a file: <code>" + filePath + "</code></p>";

			h.sendResponseHeaders(404, 0);
			h.getResponseBody().write(error404.getBytes());
			h.getResponseBody().close();
		}

		os.close();
	}
}
