/**
 * @author 
 */

package AlgoPars.Metier;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List     ;
import java.util.Locale;
import java.util.Scanner  ;

import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;

/**
 * Classe permettant de gérer les données devant être suivies
 */
public class GestionDonnee
{
	private ArrayList<String> lstVar ; // Liste des variables à suivre
	private Interpreteur interpreteur; // L'interpreteur qui possède l'instance en cours
	
	/**
	 * Lit le fichier .var au même nom que le fichier algo, s'il existe
	 * @param nomFichier
	 * @param interpreteur
	 */
	public GestionDonnee (String nomFichier, Interpreteur interpreteur)
	{
		this.lstVar       = new ArrayList<String>();
		this.interpreteur = interpreteur;

		File fichier = new File (nomFichier + ".var" );
		
		if (fichier.exists()) // si le fichier existe
		{
			try
			{
				Scanner sc = new Scanner ( fichier );
				
				while (sc.hasNextLine())
				{
					String ligne = sc.nextLine();
					this.lstVar.add(ligne);
				}
				
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Retourne la liste des variables devant être traitées
	 * @return
	 * 		variables devant être traitées
	 */
	public ArrayList<String> getListeVariable()
	{
		return this.lstVar;
	}

	/**
	 * Retourne les données étant à tracer
	 * @return
	 *    les données devant être tracer
	 */
	public String getDonneeString()
	{
		String sRet   = "";
		
		sRet += getDonneesTab();

		for(int i=this.lstVar.size();i<41;i++)sRet += String.format("%-41s", "") + "\n";
		
		return sRet;
	}
	
	/**
	 * Pour récupèrer les détails de la trace des variables
	 * @return
	 *    la trace des variables
	 */
	public String getDonneesTab () 
	{
		String sRet = "";
		
		String valVar;
		
		Donnee tmp;

		for(String var: this.lstVar)
		{
			tmp = this.interpreteur.getDonnee(var);

			if ( tmp == null )valVar = "non declaré";
			else
			{
				if ( tmp.getValeur() == null )valVar = "non initialisé";
				else
				{
					valVar = String.valueOf(tmp.getValeur());
					if ( tmp.getType().equals(Type.BOOLEEN) )
					{
						if ( valVar.contains("true") )valVar = "vrai";
						else                          valVar = "faux";
					}
					if(valVar.length() > 10)
					{
						if ( tmp.getType().equals(Type.CHAINE))
						{
							String debut = valVar.substring(0,5);
							String fin   = valVar.substring((valVar.length()-3), valVar.length());
							valVar = debut + ".." + fin;
						}
					}
				}
			}
			// à fixer quand c'est un nombre
			sRet += String.format("  %-15s", var) + "|" + String.format("%22s", valVar) + " \n";
		}
		
		return sRet;
	}
	
	/**
	 * Pour obtenir une copie de la trace dans le presse  papier
	 */
	public void traceCopie ()
	{
		String sRet = "";
		
		for(int i=0; i < 11; i++) sRet += "¨";
		sRet += "\n| DONNEES |\n";
		
		for(int i=0; i < 43; i++) sRet += "¨";
		sRet += "\n|     NOM         |         VALEUR        |\n";
		
		sRet += getDonneesTab();
		sRet = sRet.replaceAll(" \n ", " |\n| ");
		sRet = sRet.replaceAll(" \n" , " |\n"  );
		sRet = sRet.replaceAll("\n  ", "\n|  " );
		
		
		for(int i=0; i < 42; i++) sRet += "¨";
		sRet += "\n";
		
		copiePressePapier(sRet);
	}
	
	/**
	 * Retourne la trace des variables
	 * @return 
	 * 			la trace des variables
	 */
	public String traceVar(String var)
	{
		String sRet = "";
		String type = "";

		int dimension;
		
		for(int i=0; i < 11; i++) sRet += "¨";
		sRet +=  "\n| DONNEES |\n";
			
		for(int i=0; i < 56; i++) sRet += "¨";
		sRet += "\n|     NOM         |                 TYPE               |\n";
		
		Donnee tmp = this.interpreteur.getDonnee(var);
		dimension = tmp.getDimension();

		if ( dimension >= 1 ) // si c'est un tableau
		{
			type = "Tableau d";
			switch ( tmp.getType() )
			{
				case Type.ENTIER -> type += "'entiers";
				default          -> type += "e " + tmp.getType();
			}
		}
		else
			type = tmp.getType();

		sRet += "| " + String.format("%-15s", var) + " | " + String.format("%35s", type) + "|\n";
		for(int i=0; i < 56; i++) sRet += "¨";
		sRet += "\n¨¨¨¨¨¨¨¨\n|Valeur|\n¨¨¨¨¨¨¨¨\n";
		for(int i=0; i < 56; i++) sRet += "¨";
		sRet += "\n";

		if ( dimension == 0 )
		{
			String val = String.valueOf(tmp.getValeur());

			// actualise le type pour les booléens
			if ( tmp.getType().equals(Type.BOOLEEN)) 
			{
				if ( val.contains("true"))val = "vrai";
				else                      val = "faux";
			}

			sRet += String.format ("|  %-50s  |\n", val);
			for(int i=0; i < 56; i++) sRet += "¨";	
		}
		else
		{ // cas ou c'est un tableau 
			String ligneTab   = "";
			String ligneIndice= "";
			int dim1, dim2, dim3;
			dim1 = dim2 = dim3 = 0;
			
			List<Object> tab = (List<Object>)(tmp.getValeur());

			dim1 = tab.size();
			if ( dimension >= 2 )dim2 = ((List)((List)tab).get(0)).size();
			if ( dimension == 3 )dim3 = ((List)((List)((List)tab).get(0)).get(0)).size();
		
			for(int i=0;i<112;i++)sRet += "¨";
			sRet += "\n";

			for(int i=0; i < dim1; i++)
			{
				if ( dimension >= 2 )
				{
					for (int j=0;j<dim2;j++)
					{
						if ( dimension == 3 )
						{
							for (int k=0;k<dim3;k++)
							{
								sRet += String.format("| %-25s | %-80s |",
							                      tmp.getNom()+"["+i+"]"+"["+j+"]["+k+"]",
												  ((List)((List)tab.get(i)).get(j)).get(k) + "");
								sRet += "\n";
							}

							if ( j != dim2 -1 )
							{
								for(int l=0; l < 112; l++) sRet += "¨";
								sRet += "\n";
							}
						}
						else
						{
							sRet += String.format("| %-25s | %-80s |",
							                      tmp.getNom()+"["+i+"]"+"["+j+"]", ((List)tab.get(i)).get(j) + "");
							sRet += "\n";
						}
					}
					if ( i != dim1 -1 )
					{
						for(int l=0; l < 112; l++) sRet += "¨";
						sRet += "\n";
					}
				}
				else
				{
					sRet += String.format("| %-25s | %-80s |",
					                      tmp.getNom()+"["+i+"]", tab.get(i)+ "");
					sRet += "\n";
				}
			}
			for(int i=0; i < 112; i++) sRet += "¨";
			sRet += "\n";

		}
		return sRet + "\n";
	}
	
	/**
	 * Recupère le détail d'une variable en particulier
	 * @param var
	 *    détail de var
	 */
	public void traceVariableCopie(String var)
	{
		copiePressePapier(traceVar(var));
	}
	
	/**
	 * Ajour d'une ligne pour le terminal
	 * @param ligneTab
	 * @param ligneIndice
	 * @return
	 *    la nouvelle ligne avec l'affichage à jour
	 */
	public String ajoutLigne (String ligneTab, String ligneIndice)
	{
		String sRet = "";
		String debut = "| " + String.format("%-15s", "") + " | ";
		
		if (! ligneTab   .isEmpty()) sRet += debut + String.format("%-63s", ligneTab   ) + " |\n";
		if (! ligneIndice.isEmpty()) sRet += debut + String.format("%-63s", ligneIndice) + " |\n";
		sRet += debut + String.format("%-63s", "") + " |\n";
		
		return sRet;
	}
	
	/**
	 * Passe la chaine en paramètre dans le presse papier
	 * @param s
	 *    chaine à passer dans le presse papier
	 */
	public void copiePressePapier (String s)
	{
		try 
		{
			StringSelection sCopie = new StringSelection(s);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sCopie, null);
		}
		catch( Exception e) { e.printStackTrace(); }
		
		System.out.println("La visualisation a été copié dans le presse papier avec succès");
	}
}
