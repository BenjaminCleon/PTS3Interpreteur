import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class TraceurAlgo
{
	private String lien = "Essais.algo";
	private ArrayList<String> alLigne = new ArrayList<String>();

	public void lectureFichier(String lien)
	{
		String line = "";
		try
		{
			Scanner sc = new Scanner (new FileInputStream(lien));
			while(sc.hasNextLine())
			{
				line = sc.nextLine();
				this.alLigne.add(line);
			}
			sc.close();
		}catch (Exception e) {System.out.println("Erreur 001 : Lecture du fichier .algo"); e.printStackTrace();}
	}
	public static void main(String[] args)
	{
		lectureFichier("Essais.algo");
	
		try
		{
			Scanner input = new Scanner(System.in);
			int nLine = 0;
			while(nLine<this.alLigne.size())
			{
				for(int nbLigne=0; nbLigne<this.alLigne.size(); nbLigne++)
				{
					if( nLine == nbLigne)
						System.out.println("X " + (nbLigne+1) + " " + this.alLigne.get(nbLigne));
					else
						System.out.println((nbLigne+1) + " " + this.alLigne.get(nbLigne));
				}


				line = input.nextLine();
				switch(line.toUpperCase())
				{
					case ""  -> {nLine++;}
					case "B" -> {nLine--;}
					default ->
					{
						switch((line.charAt(0) + "").toUpperCase())
						{
							case "L" -> {nLine = Integer.parseInt(line.substring(1))-1;}
							case "I" -> {/*S'arreter dans une itération */}
						}
					}
				}


			}
			input.close();
		} catch (Exception e) {System.out.println("Erreur 002 : Avancement ligne par ligne"); e.printStackTrace();}
		

		
	}
}