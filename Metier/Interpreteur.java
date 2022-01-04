package AlgoPars.Metier;

import AlgoPars.Controleur          ;
import AlgoPars.Metier.Donnee       ;
import AlgoPars.Metier.EntreeSortie ;
import AlgoPars.Metier.GestionDonnee;

import java.util.List         ;
import java.io.FileInputStream;
import java.util.ArrayList    ;
import java.util.Scanner      ;

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

	private GestionDonnee gestionDonnee; // permet de gérer les données souhaitant être traiter

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
		this.gestionDonnee = new GestionDonnee(nomFichier, this);
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

			if ( ligneAInterpreter.equals("DEBUT") )this.lectureConstante = this.lectureVariable = false;
			
			if ( ligneAInterpreter.equals("variable:" ) )
			{
				this.lectureConstante = false;
				this.lectureVariable  = true ;
			}
			if ( ligneAInterpreter.equals("constante:") )this.lectureConstante = true;

			if ( (this.lectureVariable || this.lectureConstante) && 
			    !(ligneAInterpreter.equals("constante:") || ligneAInterpreter.equals("variable:")) )
			{
				// Alan c'est ton moment
				this.creerDonnee(ligneAInterpreter);
			}
			else
			{
				if ( ligneAInterpreter.contains("ecrire") )this.traceDexecution += EntreeSortie.ecrire(ligneAInterpreter, this) + "\n";
				if ( ligneAInterpreter.contains("<--"   ) )this.affecter(ligneAInterpreter);
				if ( ligneAInterpreter.contains("lire"  ) )EntreeSortie.lire(ligneAInterpreter, this);
			}
		}
	}

	/**
	 * Retourne la trace d'execution actuelle
	 * @return
	 */
	public String getTraceDexecution()
	{
		return this.traceDexecution;
	}

	/**
	 * Permet de lire le fichier et de récupérer son contenu
	 */
	public void lectureFichier()
	{
		String line = "";
		try
		{
			Scanner sc = new Scanner (new FileInputStream(nomFichier + ".algo"));
			while(sc.hasNextLine())
			{
				line = sc.nextLine();
				this.lstContenu.add(line.replaceAll("\t", "    "));
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
	 * retourne le nombre de données
	 * @return
	 *    nombre de données
	 */
	public int getNbDonnee(){ return this.lstDonnee.size(); }

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
			res += String.format("%3d %-79s", i, this.lstContenu.get(i)) + "\n";
		
		if ( i < 40+n)
			for (int j=0;j<40+n-i;j++)
				res += String.format("%-83s", "") + "\n";

		return res;
	}
	
	/**
	 * permet de creer une donnee
	 * constante ou variable
	 * @param ligne
	 *   la ligne à traiter
	 */
	public void creerDonnee(String ligne)
	{
		Donnee tmp;
		String nom;
		if(this.lectureVariable) 
		{

			String[] l = ligne.split(":");
			l[0].replaceAll(" ", "");
			String[] lSplit = l[0].split(",");
			
			for(int i=0; i<lSplit.length; i++)
			{
				nom = lSplit[i].replaceAll(" |\t", "");
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
			String[] l = ligne.split("<--");
			nom = l[0].replaceAll(" |\t", "");

			String valeur[] = ligne.split("<--");
			String val = valeur[1];
			
			switch(this.getType(ligne))
			{
				case "entier"    -> tmp = new Donnee<Integer>   (nom, "entier"   , Integer.parseInt(val)    , true);
				case "réel"      -> tmp = new Donnee<Double>    (nom, "réel "    , Double.parseDouble(val)  , true);
				case "caractère" -> tmp = new Donnee<Character> (nom, "caractère", val.charAt(0)            , true);
				case "booléen"   -> tmp = new Donnee<Boolean>   (nom, "booléen"  , Boolean.parseBoolean(val), true);
				default           -> tmp = new Donnee<String>   (nom, "chaine de caractères",           val , true); 
			}
			this.lstDonnee.add(tmp);
		}
	}

	/**
	 * Permet d'affecter une valeur à une ligne
	 * @param ligne
	 */
	private void affecter(String ligne)
	{
		String nomVar = ligne.substring(0, ligne.indexOf("<--")).replaceAll(" |\t", "");
		String value  = ligne.substring(ligne.indexOf("<--")+3);

		value = value.replaceAll(" |\t", "");

		Donnee tmp = null;

		for ( Donnee data: this.lstDonnee )
			if ( data.getNom().equals(nomVar) )tmp = data;

		Util.setValeurBySwitch(tmp, value);
	}

	/**
	 * Retourne le nombre de ligne du fichier
	 * @return
	 * 	nombre de ligne
	 */
	public int getSizeContenu(){ return this.lstContenu.size(); }

	/**
	 * Retourne la ligne des données
	 * @param ligne
	 * @return
	 */
	private String getType(String ligne)
	{
<<<<<<< HEAD
		String type;
		if(this.lectureVariable)
		{
			String[] decomp = ligne.split(":");
			type = decomp[1].replaceAll(" ", "");
		}
		else //this.lectureConstante
		{
			val = this.getValeur(ligne);
			type = "entier";
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
		return null;
=======
		return Util.getType(ligne, this.lectureConstante);
>>>>>>> 0e58b52626381ac2452485f752c51f6b5e485c50
	}

	/**
	 * Retourne l'affichage des données séléctionnés
	 * @return
	 */
	public String getDonnees()
	{
		return this.gestionDonnee.getDonneeString();
	}
}
