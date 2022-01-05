package AlgoPars.Metier;

import AlgoPars.Controleur          ;
import AlgoPars.Metier.Donnee       ;
import AlgoPars.Metier.EntreeSortie ;
import AlgoPars.Metier.GestionDonnee;

import java.util.List         ;
import java.io.FileInputStream;
import java.util.ArrayList    ;
import java.util.Scanner      ;

import iut.algo.Console;
import iut.algo.CouleurConsole;


/**
 * Classe principale liée au modèle de l'architecture MVC
 * @author LHEAD
 */
public class Interpreteur
{
	private static final String BG_ROUGE = "\u001b[41m";
	private static final String BG_RESET = "\u001b[40m";

	private Controleur  controleur; // controleur associé

	private List<Donnee> lstDonnee ; // liste des données
	private List<String> lstContenu; // contenu du fichier
	
	private String traceDexecution ; // trace d'éxécution du code

	private String nomFichier; // nom du fichier à lire
	private int    numeroLigne; // Numéro de la ligne en cours
	private int    move;

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
		this.numeroLigne = 0;
		this.move = 0;

		this.lstContenu      = new ArrayList<String>();
		this.lstDonnee       = new ArrayList<Donnee>();
		this.traceDexecution = "";

		this.lectureVariable  = false;
		this.lectureConstante = false;

		this.lectureFichier();
		this.gestionDonnee = new GestionDonnee(nomFichier, this);
	}

	/**
	 * Définit le numéro de la ligne en cours
	 */
	public void setNumeroLigne(int numeroLigne){this.numeroLigne = numeroLigne;}


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
	 *    contenu du fichier sous forme de chaine
	 */
	public String getFichier(int n)
	{
		String res ;
		int    size;
		
		int i;
		int l;
		res = "";
		size = this.lstContenu.size();
		this.setNumeroLigne(n);

		if( size <= 40)
		{
			for(l=0; l<size; l++)
			{
				//System.out.println(CouleurConsole.ROUGE.getFont());
				if (this.numeroLigne == l)
					res += CouleurConsole.MAUVE.getFond();
				res+= String.format("%2d %-80s",l, this.lstContenu.get(l))+ CouleurConsole.NOIR.getFond() + "\n";
				
			}

			for(int a = l; a<40; a++)
				res += String.format("%-80s", " ") + "\n";
		}
		else//A debug
		{
			
			int move = 0;
			if( this.numeroLigne < 20)
			{
					//Affichage des 40 premieres lignes
				for(i = 0+move; i<40+move; i++)
				{
					if (this.numeroLigne == i)
						res += CouleurConsole.MAUVE.getFond();
					res+= String.format("%2d %-80s",i, this.lstContenu.get(i))+ CouleurConsole.NOIR.getFond() + "\n";
				}
			}
			else
			{
				move = this.numeroLigne-20;
				for(int j = 0+move; j<size; j++)
				{
					if (this.numeroLigne == j)
					{
						res += String.format("%2d %-80s", j, CouleurConsole.MAUVE.getFond() + this.lstContenu.get(j)) + CouleurConsole.NOIR.getFond() + "\n";
					}
					else
						res += String.format("%2d %-80s", j, this.lstContenu.get(j)) + "\n";
				}
			}

			//Ajout des lignes vides en fin de programme
			for(i=n;i<40+n && size>i ;i++)
				if ( i < 40+n)
					for (int j=0;j<40+n-i;j++)
						res += String.format("%-80s", "") + "\n";
		
		}
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
			l[0].replaceAll(" |\t", "");
			String[] lSplit = l[0].split(",");
			
			for(int i=0; i<lSplit.length; i++)
			{
				nom = lSplit[i].replaceAll(" |\t", "");
				switch(this.getType(ligne))
				{
					case Type.ENTIER  -> tmp = new Donnee<Integer>  (nom, Type.ENTIER , null, false);
					case Type.REEL    -> tmp = new Donnee<Double>   (nom, Type.REEL   , null, false);
					case Type.CHAR    -> tmp = new Donnee<Character>(nom, Type.CHAR   , null, false);
					case Type.BOOLEEN -> tmp = new Donnee<Boolean>  (nom, Type.BOOLEEN, null, false);
					default           -> tmp = new Donnee<String>   (nom, Type.CHAINE , null, false);
				}
				this.lstDonnee.add(tmp);
			}
		}
		if(this.lectureConstante)
		{
			String[] l = ligne.split("<--");
			nom = l[0].replaceAll(" |\t", "");
			String val = Util.getValeur(ligne);			
			switch(this.getType(ligne))
			{
				case Type.ENTIER  -> tmp = new Donnee<Integer>  (nom, Type.ENTIER , Integer.parseInt(val)    , true);
				case Type.REEL    -> tmp = new Donnee<Double>   (nom, Type.REEL   , Double.parseDouble(val)  , true);
				case Type.CHAR    -> tmp = new Donnee<Character>(nom, Type.CHAR   , val.charAt(0)            , true);
				case Type.BOOLEEN -> tmp = new Donnee<Boolean>  (nom, Type.BOOLEEN, Boolean.parseBoolean(val), true);
				default           -> tmp = new Donnee<String>   (nom, Type.CHAINE ,                       val, true); 
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
		String value  = Util.getValeur(ligne);

		System.out.println(nomVar + " |" + value + "|");

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
		return Util.getType(ligne, this.lectureConstante);
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