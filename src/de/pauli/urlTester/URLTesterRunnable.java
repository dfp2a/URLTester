package de.pauli.urlTester;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class URLTesterRunnable implements Runnable {
	private boolean running = true;
	private FileWriter fw;
	private BufferedWriter br;
	private SimpleDateFormat sdfLog, sdfFile;
	private URL testURL;
	private String urlName;
	private String filename;
	private static final int INTERVALTIME = 30 * 1000;

	//Einrichtung aller Komponenten
	public boolean initialise(String urlName) {
		boolean succes = false;
		this.urlName = urlName;
		sdfLog = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",Locale.US);
		sdfFile = new SimpleDateFormat("dd-MMM-yyyy HH-mm-ss",Locale.US);
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

	//Abschluss des Vorgangen von außen und ordentliches Beenden des Programmes
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
	
	//Globales Prüfen ob die URL keine Probeleme macht
	public static boolean CheckURL(String potentialURL) {

		boolean succes = true;
		try {
			URL url = new URL(potentialURL);
		} catch (MalformedURLException e) {
			succes = false;
		}
		return succes;
	}

//Aufgabenerfüllen des Testen der URL in 30 Sekunden Intervallen
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
				}
				// Nach den HTTP Status Codes ist alles über 400 nicht positiv demnach die Prüfung
				String timereport = String.format("%s : %s -> %s", timestring, urlName,
						((status >= 400) ? "nicht erreichbar" : "erreichbar"));
				System.out.println(timereport);
				try {
					br.write(timereport);
					br.newLine();
					br.close();
					// Damit das Fortlaufend garantiert wird wird der BufferedWriter geschlossen und neue geöffnet
					File f = new File(filename);
					fw = new FileWriter(f, true);
					br = new BufferedWriter(fw);

				} catch (Exception e) {
					
				}

			}
		}

	}
}