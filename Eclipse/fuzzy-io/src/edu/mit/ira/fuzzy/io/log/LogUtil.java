package edu.mit.ira.fuzzy.io.log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {

	// Save string of data to text file
	public static void saveData(String path, String data) {
		byte bytes[] = data.getBytes();
		Path p = Paths.get(path);
		try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(p))) {
			out.write(bytes, 0, bytes.length);
		} catch (IOException x) {
			System.err.println(x);
		}
	}

	// append a string to a text file
	public static void appendData(String path, String data) {
		// Write Log to File
		byte bytes[] = data.getBytes();
		Path p = Paths.get(path);
		try (OutputStream out = new BufferedOutputStream(
				Files.newOutputStream(p, StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
			out.write(bytes, 0, bytes.length);
		} catch (IOException x) {
			System.err.println(x);
		}
	}

	public static String makeTimeStamp() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		Date date = new Date(System.currentTimeMillis());
		return formatter.format(date);
	}
}