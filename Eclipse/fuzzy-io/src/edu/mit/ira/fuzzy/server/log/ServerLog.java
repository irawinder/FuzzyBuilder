package edu.mit.ira.fuzzy.server.log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sun.net.httpserver.HttpExchange;

public class ServerLog {
	
	/**
	 * Prints a log to console. Also returns the log as a string
	 * @param clientIP
	 * @param message
	 * @return
	 */
	public static void add(HttpExchange t, String message) {

		// Create Log
		String clientIP = t.getRemoteAddress().toString();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		Date date = new Date(System.currentTimeMillis());
		String timeStamp = formatter.format(date);
		String log = timeStamp + " " + clientIP + " " + message + "\n";

		// Write Log to Console
		System.out.print(log);

		// Write Log to File
		byte data[] = log.getBytes();
		Path p = Paths.get("./logs.txt");
		try (OutputStream out = new BufferedOutputStream(
				Files.newOutputStream(p, StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
			out.write(data, 0, data.length);
		} catch (IOException x) {
			System.err.println(x);
		}
	}
}
