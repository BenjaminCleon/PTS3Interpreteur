package AlgoPars.Metier;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List     ;
import java.util.Locale;
import java.util.Scanner  ;

import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;

public class GestionDonnee
{
	private List<String> lstVar      ;
	private Interpreteur interpreteur;
	
	public GestionDonnee (String nomFichier, Interpreteur interpreteur)
	{
		this.lstVar       = new ArrayList<String>();
		this.interpreteur = interpreteur;

		File fichier = new File (nomFichier + ".var" );
		
		if (fichier.exists())
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
						if ( valVar.equals("true") )valVar = "vrai";
						else                        valVar = "faux";
					}
					if(valVar.length() > 10)
					{
						String debut = valVar.substring(0,5);
						String fin   = valVar.substring((valVar.length()-3), valVar.length());
						valVar = debut + fin;
					}
				}
			}
			// à fixer quand c'est un nombre
			sRet += String.format("  %-15s", var) + "|" + String.format("%22s", valVar) + " \n";
		}
		
		return sRet;
	}
	
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
	
	public String traceVar(String var)
	{
		System.out.println();
		
		String sRet = "";
		
		for(int i=0; i < 11; i++) sRet += "¨";
		sRet += "\n| DONNEES |\n";
			
		for(int i=0; i < 85; i++) sRet += "¨";
		sRet += "\n|     NOM         |                           VALEUR                                |\n";
		
		Donnee tmp = this.interpreteur.getDonnee(var);
		sRet += "| " + String.format("%-15s", var) + " | " /*+ String.format("%63s", "") + " |\n" */;
		
		System.out.println(tmp.getType());
		
		switch(tmp.getType())
		{
			case "chaine de caractères" ->
			{
				String val = String.valueOf(tmp.getValeur());
				sRet += String.format ("%63s", val) + " |\n";	
			}
			default ->
			{
				if(tmp.getValeur() instanceof List)
				{
					String ligneTab= "";
					String ligneIndice= "";
					
					List<Object> tab = (List<Object>)(tmp.getValeur());
					for(int n=0; n < tab.size(); n++)
					{
						String sCase = "";
						
						if(tmp.getType().equals("caractère")) sCase = "['" + tmp.getValeurTableau(n) + "']";
						else sCase = "[" + tmp.getValeurTableau(n) + "]";
						
						if((ligneTab.length() + sCase.length()) < 61) 
						{
							ligneTab += sCase;
							int milieu = (int)(sCase.length() / 2);
							
							for(int i=0; i < milieu; i++) ligneIndice += " ";
							ligneIndice += tab.indexOf(tmp.getValeurTableau(n));
							
							if(tab.indexOf(tmp.getValeurTableau(n)) > 9 || milieu % 2 !=0 && milieu > 1) milieu--;
							for(int i=0; i < milieu; i++) ligneIndice += " ";
						}
						else
						{
							sRet += ajoutLigne(ligneTab, ligneIndice);
							ligneTab = ligneIndice = "";
						}
					}
					
					if(! ligneTab.isEmpty())
					{
						sRet += ajoutLigne(ligneTab, ligneIndice);
						ligneTab = ligneIndice = "";
					}
					
					String s = " | " + String.format("%-15s", "") + " | ";
					sRet = sRet.replace(s, " ");
				}
			}
		}

		for(int i=0; i < 85; i++) sRet += "¨";
		sRet += "\n";
		
		return sRet;
	}
	
	public void traceVariableCopie(String var)
	{
		copiePressePapier(traceVar(var));
	}
	
	public String ajoutLigne (String ligneTab, String ligneIndice)
	{
		String sRet = "";
		String debut = "| " + String.format("%-15s", "") + " | ";
		
		if (! ligneTab   .isEmpty()) sRet += debut + String.format("%-63s", ligneTab   ) + " |\n";
		if (! ligneIndice.isEmpty()) sRet += debut + String.format("%-63s", ligneIndice) + " |\n";
		sRet += debut + String.format("%-63s", "") + " |\n";
		
		return sRet;
	}
		
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
