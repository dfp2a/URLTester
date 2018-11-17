package de.pauli.urlTester;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class URLTesterRunnable implements Runnable {
	private boolean running = true;
	private FileWriter fw;
	private BufferedWriter br;
	private SimpleDateFormat sdfLog, sdfFile;
	private URL testURL;
	private String urlName;
	private String filename;
	private static final int INTERVALTIME = 30 * 1000;

	public boolean initialise(String urlName) {
		boolean succes = false;
		this.urlName = urlName;
		sdfLog = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
		sdfFile = new SimpleDateFormat("dd-MMM-yyyy HH-mm-ss");
		try {
			testURL = new URL(urlName);
			filename= sdfFile.format(new Date()) + ".log";
			File f = new File(filename);
			fw = new FileWriter(f, true);
			br = new BufferedWriter(fw);
			succes = true;
		} catch (Exception e) {
			succes = false;
		}

		return succes;
	}

	public void stopURLTester() {
		running = false;
		try {
			if (br != null) {
				br.close();
			}
			if (fw != null) {
				fw.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		Date currentdate = new Date(), olddate = new Date(0);

		while (running) {
			currentdate = new Date();
			int status = 1000;
			if (currentdate.getTime() > olddate.getTime() + INTERVALTIME) {
				olddate = currentdate;
				String timestring = sdfLog.format(currentdate);

				try {
					HttpURLConnection con = (HttpURLConnection) testURL.openConnection();
					con.setRequestMethod("GET");
					con.setConnectTimeout(10000);
					status = con.getResponseCode();

				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
				String timereport = String.format("%s : %s -> %s", timestring, urlName,
						((status > 400) ? "nicht erreichbar" : "erreichbar"));
				System.out.println(timereport);
				try {
					br.write(timereport);
					br.newLine();
					br.close();
					File f = new File(filename);
					fw = new FileWriter(f, true);
					br = new BufferedWriter(fw);

				} catch (IOException iox) {
					iox.printStackTrace();
				}

			}
		}

	}
}