package de.pauli.urlTester;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import jdk.incubator.http.HttpResponse;

public class UrlTester {
	static boolean running = true;
	 private static final Logger log = Logger.getLogger( UrlTester.class.getName() );

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {

		Scanner scanner = new Scanner(System.in);
		String greeting = "Wilkommen zum URL Tester", aufforderung = "Geben Sie bitte die zu Testende URL eine";
		System.out.println(greeting);
		System.out.println(aufforderung);
		String eigegebeneURL = scanner.nextLine();
		
		Handler handler =  new FileHandler( "log.txt" );
		handler.setLevel( Level.FINEST );
		log.addHandler( handler );


		SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");

		final URL url = new URL(eigegebeneURL);

		System.out.println(String.format(
				"%s  wird überwacht, drücken Sie eine beliebige Taste um das Programm zu beenden...", eigegebeneURL));

		Runnable r = (new Runnable() {

			@Override
			public void run() {
				
				Date currentdate, olddate = new Date(0);
				while (true) {
					currentdate = new Date();
					int status = 1000;
					if (currentdate.getTime() > olddate.getTime() + 30 * 1000) {
						olddate=currentdate;
						String timestring = ft.format(currentdate);

						
						try {
							HttpURLConnection con = (HttpURLConnection) url.openConnection();
							con.setRequestMethod("GET");
							con.setConnectTimeout(10000);
							status = con.getResponseCode();

						} catch (Exception e) {
							// System.err.println(e.toString());
						}
						String timereport=String.format("%s : %s -> %s", timestring, eigegebeneURL,
								((status > 400) ? "nicht erreichbar" : "erreichbar"));
						System.out.println(timereport);
						log.info(timereport);
					}
				}

			}
		});

		Thread t = new Thread(r);
		t.start();
		InputStream inputStream = System.in;
		try {
			inputStream.read();
			t.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
