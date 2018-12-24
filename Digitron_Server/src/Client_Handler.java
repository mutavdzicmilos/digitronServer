import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client_Handler extends Thread {

	BufferedReader clientInput = null;
	PrintStream clientOutput = null;
	Socket soketZaKomunikaciju = null;
	String username;

	public Client_Handler(Socket soket) {
		soketZaKomunikaciju = soket;
	}
	
	private boolean proveriFajl(String username, String password) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("logovi.txt"));
			while (reader.ready()) {
				if (reader.readLine().equals(username)) {
					if (reader.readLine().equals(password))
						return true;
				}

			}
			reader.close();
		} catch (Exception e) {
			System.out.println("Greska bas");
		}

		return false;
	}

	private boolean proveriUserFajl(String username) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("logovi.txt"));
			while (reader.ready()) {
				if (reader.readLine().equals(username)) {
					return true;

				}
				reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			System.out.println("Greska bas");
		}

		return false;
	}
	private String bistriPoUseru(String username) {
		String s = "Kalkulacije korisnika ";
		try {
			BufferedReader br = new BufferedReader(new FileReader("racunice.txt"));
			while (br.ready()) {
				String a = br.readLine();
				if (a.startsWith(username))
					s = s + a + ";";
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return s;
	}

	private boolean proveriPassword(String password) {
		char ch;
		boolean capitalFlag = false;
		boolean numberFlag = false;
		if (password.length() < 8)
			return false;
		for (int i = 0; i < password.length(); i++) {
			ch = password.charAt(i);
			if (Character.isDigit(ch)) {
				numberFlag = true;
			} else if (Character.isUpperCase(ch)) {
				capitalFlag = true;
			}
			if (numberFlag && capitalFlag)
				return true;
		}
		return false;

	}

	public String radi(String s) {
		String[] deli = s.split("\\+");
		if (deli.length == 2) {

			try {
				return Integer.toString(Integer.parseInt(deli[0]) + Integer.parseInt(deli[1]));
				// sve protiv slucajeve;
			} catch (Exception e) {
				return "Greska, niste uneli brojeve";
			}
		}
		deli = s.split("\\-");
		if (deli.length == 2) {

			try {
				return Integer.toString(Integer.parseInt(deli[0]) - Integer.parseInt(deli[1]));
				// sve protiv slucajeve;
			} catch (Exception e) {
				return "Greska, niste uneli brojeve";
			}

		}
		deli = s.split("\\*");
		if (deli.length == 2) {

			try {
				return Integer.toString(Integer.parseInt(deli[0]) * Integer.parseInt(deli[1]));
				// sve protiv slucajeve;
			} catch (Exception e) {
				return "Greska, niste uneli brojeve";
			}

		}
		deli = s.split("\\/");
		if (deli.length == 2) {

			try {
				return Double.toString(Integer.parseInt(deli[0]) / Double.parseDouble(deli[1]));
				// sve protiv slucajeve;
			} catch (Exception e) {
				return "Greska, niste uneli brojeve";
			}

		}

		return "Greska,uneli ste nedozvoljene operacije";

	}

	private String registracija(PrintStream clientOutput, BufferedReader clientInput) {
		String user;
		String pw;
		boolean isValid=false;
		try {
		do {
			clientOutput.println("Unesite username");
			user = clientInput.readLine();
			if(user.equals("quit"))return null;
			clientOutput.println("Unesite sifru");
			pw = clientInput.readLine();
			if(pw.equals("quit"))return null;
			if (proveriUserFajl(user) || !proveriPassword(pw)) {
				if (proveriUserFajl(user))
					clientOutput.println("Username vec postoji, pokusajte ponovo");
				else {
					clientOutput.println(
							"Password nije adekvatan. Mora imati 8 karaktera,minimum 1 broj i minimum 1 veliko slovo.Pokusajte ponovo!");
				}
			}else {isValid=true;}			
		}while(!isValid);
		PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter("logovi.txt", true)));
		pw1.println(user);
		pw1.println(pw);
		pw1.close();
		return user;
		}catch(Exception e) {
			return null;
		}
		

	}



	
	
	
	
	
	
	
	
	
	private String login(PrintStream clientOutput, BufferedReader clientInput) {
		try {
		int pok = 0;
         clientOutput.println("Login");
         while (pok < 2) {
           clientOutput.println("Ukucajte username");
           
           String user = clientInput.readLine();
           if(user.equals("quit"))return null;
           clientOutput.println("Ukucajte password");
           String pw = clientInput.readLine();
           if(pw.equals("quit"))return null;
           if (proveriFajl(user, pw)) {
             clientOutput.println("Uspesno logovanje\n--------------------------------------------------\n");
           return user;
           }
         }
		}catch(Exception e) {
			return null;
		}
		
		return null;
	}

	private void dodajuFajl(String username, String racunica, String resenje) {
		try {
			PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter("racunice.txt", true)));
			pw1.println(username + " " + racunica + " = " + resenje);

			pw1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	private void loginovan(String user, PrintStream clientOutput2, BufferedReader clientInput2) {
		try {
		while (true) {
			
			
			clientOutput.println(
					"Ukucajte izraz. ukoliko zelite kraj rada,ukucajte samo quit, a ukoliko zelite logove l");
			String izraz = clientInput.readLine();

			

				if (izraz.equals("quit")) {
				clientOutput.println("Dovidjenja, hvala na vasem radu.");
				return;
			}	
				if (izraz.equals("l")) {
				clientOutput.println("Vasi logovi: ");

				clientOutput.println(bistriPoUseru(user));
			}else {
			String a = radi(izraz);
			if (!a.startsWith("Greska")) {
				clientOutput.println(a);
				dodajuFajl(user, izraz, a);
			} else {
				clientOutput.println(a);
			}
			}
		}
		}catch(Exception e) {
			
		}
		
	}

	public void run() {

		try {

			clientInput = new BufferedReader(new InputStreamReader(soketZaKomunikaciju.getInputStream()));
			clientOutput = new PrintStream(soketZaKomunikaciju.getOutputStream());
			clientOutput.println("\t\t\tDigitron Onlajn\n\n-------------------------------\n");
			
			runn();

			
			



			soketZaKomunikaciju.close();

		} catch (IOException e) {
			System.out.println("Nasilno zatvaranje !!!!!!U nitima u run metodi pogledaj.");
		}

	}

	private void runn() {
try {
		boolean isValid = false;

		do {
			clientOutput.println("IZABERITE OPCIJU:" + "\n1)REGISTRACIJA" + "\n2)PRIJAVA" + "\n3)NASTAVI KAO GOST");

			String izbor = clientInput.readLine();

			switch (izbor) {
			case "1": {
				isValid = true;
					String user=registracija(clientOutput,clientInput);
					if(user!=null) {
					loginovan(user,clientOutput,clientInput);}else {
						clientOutput.println("Dovidjenja");
					}
					break;
			}
			case "2": {
				isValid = true;
				String user=login(clientOutput,clientInput);
				if(user!=null) {
				loginovan(user,clientOutput,clientInput);			
				}else {
					clientOutput.print("Dovidjenja.");
				}
				break;
			}
			case "3": {
				isValid = true;
				for (int i = 0; i < 3; i++) {

					clientOutput.println("Unesi izraz");
					String izraz = clientInput.readLine();
					if(izraz.equals("quit")) {
						clientOutput.println("Dovidjenja.");
					}
					String rezultat = radi(izraz);

					if(rezultat == null) {
						clientOutput.println("Dovidjenja");
						clientInput.readLine();
						soketZaKomunikaciju.close();
						return;
					}
					if (rezultat.startsWith("GREŠKA"))
						i--;
					clientOutput.println(rezultat);

					clientOutput.println(
							"Kao gost imate pravo na jos :  " + (2 - i) + " izraza\n");
				
				}
				clientOutput.println("Dovidjenja, vas rad kao gost je gotov. Za izlazak iz aplikacije ukucajte quit");
				soketZaKomunikaciju.close();
				
				break;
			}
			case "quit": {
				isValid = true;
				clientOutput.println("Dovidjenja");
				soketZaKomunikaciju.close();
				return;
			}
			default: {
				clientOutput.println("Vracen si na pocetak.Razmisli kad biras opciju.");
			}
			}
		} while (!isValid);		}catch(Exception e) {
			clientOutput.println("puca sve");
		}
	}
	
	

}