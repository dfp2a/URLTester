package de.pauli.urlTester;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class UrlTester {

	private URL testURL;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		UrlTester urlTester = new UrlTester();
		Scanner scanner = new Scanner(System.in);
		String greeting = "Wilkommen zum URL Tester", aufforderung = "Geben Sie bitte die zu Testende URL eine";
		System.out.println(greeting);
		System.out.println(aufforderung);
		String eigegebeneURL = scanner.nextLine();

		if (urlTester.CheckAndInitializeURL(eigegebeneURL)) {

			System.out.println(
					String.format("%s  wird überwacht, drücken Sie eine beliebige Taste um das Programm zu beenden...",
							eigegebeneURL));

			URLTesterRunnable r = new URLTesterRunnable();
			if (r.initialise(eigegebeneURL)) {
				Thread t = new Thread(r);
				t.start();

				InputStream inputStream = System.in;
				try {
					inputStream.read();

					r.stopURLTester();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				System.out.println("Fehler innerhalb der Intialisierung des Testes");
			}
		}else {
		System.out.println(String.format("%s  ist keine gültige URL.", eigegebeneURL));
		}

	}

	public boolean CheckAndInitializeURL(String potentialURL) {

		boolean succes = true;
		try {
			setTestURL(new URL(potentialURL));
		} catch (MalformedURLException e) {
			succes = false;
		}

		return succes;
	}

	public URL getTestURL() {
		return testURL;
	}

	public void setTestURL(URL testURL) {
		this.testURL = testURL;
	}

}
