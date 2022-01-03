package AlgoPars.Metier;

import AlgoPars.Controleur;

import java.util.List     ;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner  ;

/**
 * Classe principale liée au modèle de l'architecture MVC
 * @author LHEAD
 */
public class Interpreteur
{
	private Controleur  controleur; // controleur associé

	private List<Donnee> lstDonnee ; // liste des données
	private List<String> lstContenu; // contenu du fichier

	private String nomFichier; // nom du fichier à lire

	/**
	 * Constructeur principale
	 */
	public Interpreteur(Controleur controleur, String nomFichier)
	{
		this.controleur = controleur;
		this.lstContenu = new ArrayList<String>();

		this.nomFichier = nomFichier;

		this.lectureFichier();
	}

	public void lectureFichier()
	{
		String line = "";
		try
		{
			Scanner sc = new Scanner (new FileInputStream(nomFichier));
			while(sc.hasNextLine())
			{
				line = sc.nextLine();
				this.lstContenu.add(line);
			}
			sc.close();
		}catch (Exception e) {System.out.println("Erreur 001 : Lecture du fichier .algo"); e.printStackTrace();}
	}

	public Donnee getDonnee(String nom)
	{
		for ( Donnee data : this.lstDonnee )
			if ( data.getNom().equals(nom) )return data;
		
		return null;
		
	}

	public void creerVariable(String ligne, String nom)
	{
		Donnee tmp;
		
		switch(this.getType(ligne))
		{
			case "entier"   : tmp = new Donnee<Integer>  (nom, "entier"             , null, false); break;
			case "réel"     : tmp = new Donnee<Double>   (nom, "réel "              , null, false); break;
			case "caractère": tmp = new Donnee<Character>(nom, "caractère"          , null, false); break;
			case "booléen"  : tmp = new Donnee<Boolean>  (nom, "booléen"            , null, false); break;
			default         : tmp = new Donnee<String>   (nom, "chaine de caractère", null, false); break;
			
		}
		this.lstDonnee.add(tmp);
	}

	public String getType(String ligne)
	{
		String[] decomp = ligne.split(":");
		String type = decomp[1].replaceAll(" ", "");
		return type;
	}
}
