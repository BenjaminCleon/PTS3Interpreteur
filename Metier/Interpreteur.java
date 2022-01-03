package AlgoPars.Metier;

import AlgoPars.Controleur;
import AlgoPars.Metier.EntreeSortie;

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
	
	private String traceDexecution ; // trace d'éxécution du code

	private String nomFichier; // nom du fichier à lire
	
	private boolean lectureVariable ; // permet de connaitre si nous sommes dans la déclaration des variables
	private boolean lectureConstante; // permet de connaitre si nous sommes dans la déclaration des constantes

	/**
	 * Constructeur principale
	 */
	public Interpreteur(Controleur controleur, String nomFichier)
	{
		this.controleur = controleur;
		this.nomFichier = nomFichier;

		this.lstContenu      = new ArrayList<String>();
		this.lstDonnee       = new ArrayList<Donnee>();
		this.traceDexecution = "";

		this.lectureVariable  = false;
		this.lectureConstante = false;

		this.lectureFichier();
	}

	/**
	 * Interprete la ligne n en partant du principe que ce qui est au-dessus est interpréter
	 * @param n
	 */
	public void interpreter(int n)
	{
		if ( n < this.lstContenu.size() && n >= 0 )
		{
			String ligneAInterpreter = this.lstContenu.get(n);

			if ( this.lectureVariable || this.lectureConstante )
			{
				// Alan c'est ton moment
				this.creerDonnee(ligneAInterpreter);
			}
			else
			{
				if ( ligneAInterpreter.equals("variable:" ) )
				{
					this.lectureConstante = false;
					this.lectureVariable  = true ;
				}
				if ( ligneAInterpreter.equals("constante:") )this.lectureConstante = true;

				if ( ligneAInterpreter.contains("ecrire") )this.traceDexecution += EntreeSortie.ecrire(ligneAInterpreter, this);
			}

			if ( ligneAInterpreter.equals("DEBUT") )this.lectureConstante = this.lectureVariable = false;	
		}
	}

	/**
	 * Permet de lire le fichier et de récupérer son contenu
	 */
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

	/**
	 * Renvoie la donnée avec le nom en paramètre
	 * @param nom de la donnée
	 * @return
	 *    la donnée associée au nom
	 */
	public Donnee getDonnee(String nom)
	{
		for ( Donnee data : this.lstDonnee )
			if ( data.getNom().equals(nom) )return data;
		
		return null;
	}

	/**
	 * Retourne le contenu avec + ou - 40 lignes
	 * @param n
	 *     la ligne ou se fait l'interprétation
	 * @return
	 *    contenu du fichier
	 */
	public String getFichier(int n)
	{
		String res ;
		int    size;
		
		int i;

		size = this.lstContenu.size();
		
		if ( size <= 40 )n=0;

		res = "";
		for(i=n;i<40+n && size>i ;i++)
			res += String.format("%2d %-80s", i, this.lstContenu.get(i)) + "\n";
		
		if ( i < 40+n)
			for (int j=0;j<40+n-i;j++)
				res += String.format("%-80s", "") + "\n";

		return res;
	}
	
	public void creerDonnee(String ligne)
	{
		Donnee tmp;
		String nom;
		if(this.lectureVariable)
		{
			
			
			String[] l = ligne.split(":");
			l[0].replacceAll(" ", "");
			String[] lSplit = l[0].split(",");
			
			for(int i=0; i<lSplit.length; i++)
			{
				nom = lSplit[i];
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
		}
		if(this.lectureConstante)
		{
			String[] l = ligne.split("=");
			nom = l[0].replaceAll(" ", "");
			switch(this.getType(ligne))
			{
				case "entier" : 
				{
					Integer val = Integer.parseInt(this.getValeur(ligne));
					tmp = new Donnee<Integer>   (nom, "entier"             , val, true);
					break;
				}
				case "réel" :
				{
					Double val = Double.ParseDouble(this.getValeur(ligne));
					tmp = new Donnee<Double>    (nom, "réel "              , val, true);
					break;
				}
				case "caractère" :
				{
					Character val = this.getValeur(ligne);
					tmp = new Donnee<Character>(nom, "caractère"          , val, true);
					break;
				}
				case "booléen" :
				{
					Boolean val = this.getValeur(ligne).equals("true");
					tmp = new Donnee<Boolean>   (nom, "booléen"            , val, true);
					break;
				}
				default :
				{
					String valeur[] = ligne.split("=");
					String val = valeur[1];
					tmp = new Donnee<String>   (nom, "chaine de caractère", val, true); 
					break;
				}
			}
			this.lstDonnee.add(tmp);
		}
	}
	/**
	 * Retourne le nombre de ligne du fichier
	 * @return
	 * 	nombre de ligne
	 */
	public int getSizeContenu(){ return this.lstContenu.size(); }

	/**
	 * Créer une variable
	 * @param ligne
	 * @param nom
	 */
	public void creerVariable(String ligne, String nom)
	{
		Donnee tmp;
		
		switch(this.getType(ligne))
		{
			case "entier"   : tmp = new Donnee<Integer>  (nom, "entier"             , null, !this.lectureVariable); break;
			case "réel"     : tmp = new Donnee<Double>   (nom, "réel "              , null, false); break;
			case "caractère": tmp = new Donnee<Character>(nom, "caractère"          , null, false); break;
			case "booléen"  : tmp = new Donnee<Boolean>  (nom, "booléen"            , null, false); break;
			default         : tmp = new Donnee<String>   (nom, "chaine de caractère", null, false); break;
		}
		this.lstDonnee.add(tmp);
	}

	/**
	 * Retourne la ligne des données
	 * @param ligne
	 * @return
	 */
	private String getType(String ligne)
	{
		if(this.lectureVariable)
		{
			String[] decomp = ligne.split(":");
			String type = decomp[1].replaceAll(" ", "");
		}
		else //this.lectureConstante
		{
			val = this.getValeur(ligne);
			String type = "entier";
			if(val.matches("\"(.*)\"")) type = "chaine de caractères";
			if(val.matches("\'.\'")   ) type = "caractère";
			if(val.equals("true") || val.equals("false")) type = "booléen";
			if(val.matches("(\\d*),(\\d*)") type = "réel";
		}
		return type;
	}
	
	public String getValeur(String ligne)
	{
		if(this.lectureConstante)
		{
			String valeur[] = ligne.split("=");
			val = valeur[1].replaceAll(" ", "");
			return val;
		}
		return "null";
	}

	/**
	 * Retourne l'affichage des données
	 * @return
	 */
	public String getDonnees(){ return ""; }
}
