package AlgoPars.Metier;

import AlgoPars.Controleur          ;
import AlgoPars.Metier.Donnee       ;
import AlgoPars.Metier.EntreeSortie ;
import AlgoPars.Metier.GestionDonnee;
//import AlgoPars.Metier.Tableau      ;

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
	private Controleur  controleur; // controleur associé

	private List<Donnee>  lstDonnee ; // liste des données
	//private List<Tableau> lstTableau; // liste des tableaux
	private List<String>  lstContenu; // contenu du fichier
	
	private List<String>  traceDexecution; // trace d'éxécution du code
	private List<Integer> traceLire      ; // retient les numéros de ligne où il y a eu un lire

	private String nomFichier; // nom du fichier à lire
	private int    numeroLigne; // Numéro de la ligne en cours
	private int    move;

	private boolean lectureVariable ; // permet de connaitre si nous sommes dans la déclaration des variables
	private boolean lectureConstante; // permet de connaitre si nous sommes dans la déclaration des constantes
	
	//booléens qui permettent de savoir si il y a des commentaires /* */
	private boolean enComm;
	private boolean commOk;

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
		this.traceDexecution = new ArrayList<String> ();
		this.traceLire       = new ArrayList<Integer>();

		this.lectureVariable  = false;
		this.lectureConstante = false;
		
		this.enComm = false;
		this.commOk = true ;

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
		String ligneAInterpreter;
		int indexComment;

		if ( n < this.lstContenu.size() && n >= 0 )
		{
			this.commOk = true;
			ligneAInterpreter = commenter(this.lstContenu.get(n));
			indexComment = ligneAInterpreter.indexOf("//");

			if ( indexComment != -1 )ligneAInterpreter = ligneAInterpreter.substring(0, indexComment);

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
				if ( ligneAInterpreter.contains("ecrire") )this.traceDexecution.add(EntreeSortie.ecrire(ligneAInterpreter, this));
				if ( ligneAInterpreter.contains("<--"   ) )this.affecter(ligneAInterpreter);
				if ( ligneAInterpreter.contains("lire"  ) ){ this.traceDexecution.add(EntreeSortie.lire(ligneAInterpreter, this));this.traceLire.add(this.traceLire.size()+1); }
			}
		}
	}

	/**
	 * Retourne la trace d'execution actuelle
	 * @return
	 */
	public List<String> getTraceDexecution()
	{
		return this.traceDexecution;
	}

	/**
	 * Retourne la trace des numéros de ligne lu
	 * @return
	 */
	public List<Integer> getTraceLire()
	{
		return this.traceLire;
	}

	/**
	 * Permet de lire le fichier et de récupérer son contenu
	 */
	public void lectureFichier()
	{
		String line = "";
		try
		{
			Scanner sc = new Scanner (new FileInputStream(nomFichier + ".algo"), "UTF8");
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
		String tmp ;
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
		
		tmp = null;
		if(this.lectureVariable) 
		{

			String[] l = ligne.split(":");
			l[0].replaceAll(" |\t", "");
			String[] lSplit = l[0].split(",");
			
			for(int i=0; i<lSplit.length; i++)
			{
				nom = lSplit[i].replaceAll(" |\t", "");
				if(l[1].matches("(.*)tableau(.*)"))
				{
					String type = "";
					String[] lig = l[1].split("de|d'",2);
					type = lig[1].replaceFirst(" ", "");
					type = type  .replaceFirst("s", "");
					
					
					String indices = ligne;
					String[] t;
					indices = indices.substring(indices.indexOf("[")+1, indices.lastIndexOf("]"));
					indices = indices.replaceAll("\\[|\\]$", "");
					t = indices.split("\\]");
					Integer[] taille = new Integer[t.length];
					for(int cpt=0; cpt<t.length; cpt++)taille[cpt] = Integer.parseInt(t[cpt]);
					switch(this.getType(ligne))
					{
						case Type.ENTIER  -> tmp = new Donnee(nom, type, new ArrayList<Integer>  (), this.lectureConstante, taille);
						case Type.REEL    -> tmp = new Donnee(nom, type, new ArrayList<Double>   (), this.lectureConstante, taille);
						case Type.BOOLEEN -> tmp = new Donnee(nom, type, new ArrayList<Boolean>  (), this.lectureConstante, taille);
						case Type.CHAR    -> tmp = new Donnee(nom, type, new ArrayList<Character>(), this.lectureConstante, taille);
						default           -> tmp = new Donnee(nom, type, new ArrayList<String>   (), this.lectureConstante, taille);
					}
					this.lstDonnee.add(tmp);
					
				}
				else
				{
					switch(this.getType(ligne))
					{
						case Type.ENTIER  -> tmp = new Donnee(nom, Type.ENTIER , null, false);
						case Type.REEL    -> tmp = new Donnee(nom, Type.REEL   , null, false);
						case Type.CHAR    -> tmp = new Donnee(nom, Type.CHAR   , null, false);
						case Type.BOOLEEN -> tmp = new Donnee(nom, Type.BOOLEEN, null, false);
						default           -> tmp = new Donnee(nom, Type.CHAINE , null, false);
					}
				}
				this.lstDonnee.add(tmp);
			}
		}
		if(this.lectureConstante)
		{
			String[] l = ligne.split("<--");
			nom = l[0].replaceAll(" |\t", "");
			String val = Util.getValeur(ligne, true, null);			
			switch(this.getType(ligne))
			{
				case Type.ENTIER  -> tmp = new Donnee(nom, Type.ENTIER , Integer.parseInt(val)    , true);
				case Type.REEL    -> tmp = new Donnee(nom, Type.REEL   , Double.parseDouble(val)  , true);
				case Type.CHAR    -> tmp = new Donnee(nom, Type.CHAR   , val.charAt(0)            , true);
				case Type.BOOLEEN -> tmp = new Donnee(nom, Type.BOOLEEN, Boolean.parseBoolean(val), true);
				default           -> tmp = new Donnee(nom, Type.CHAINE ,                       val, true); 
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
		String value  = Util.getValeur(ligne, false, this);
		int ind =-1;
		
		//System.out.println(nomVar + " |" + value + "|");

		Donnee tmp = null;
		
		if(nomVar.matches("(.*)[(.*)](.*)"))
		{
			String[] decomp = nomVar.split("\\[|\\]");
			nomVar = decomp[0];
			ind    = Integer.parseInt(decomp[1]);
		}
		
		for ( Donnee data: this.lstDonnee )
			if ( data.getNom().equals(nomVar) )tmp = data;

		Util.setValeurBySwitch(tmp, value);
	}

	public String commenter( String ligne)
	{
		
		if(ligne.contains("/*") && ligne.contains("*/"))
		{
			this.commOk = false;
			int indDebCom, indFinCom;
			String l = "";
			indDebCom = ligne.lastIndexOf("/*");
			indFinCom = ligne.indexOf("*/");
			if(indDebCom < indFinCom){l = ligne.substring(0, indDebCom) + " " + ligne.substring( indFinCom+ 2);this.enComm = false;}
			else 
			{
				String finLigne = ligne.substring( ligne.lastIndexOf("*/") + 2 );
				l = ligne.substring(indFinCom + 2, indDebCom);
				this.enComm = true;
				if(ligne.lastIndexOf("*/") > indDebCom){l = l + " " + finLigne;this.enComm = false;}
			} 
			return commenter(l);
		}
		if(ligne.contains("/*")){this.enComm = true ;this.commOk = false;return commenter(ligne.substring(0, ligne.lastIndexOf("/*")));}
		if(ligne.contains("*/")){this.enComm = false;this.commOk = false;return commenter(ligne.substring(ligne.indexOf("*/")+2, ligne.length()));}
		if(this.enComm && this.commOk)return "";
		if(ligne.charAt(0) == ' ') ligne = ligne.substring(1);
		
		return ligne;
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

	public void actualiser()
	{
		this.controleur.actualiser();
	}
}
