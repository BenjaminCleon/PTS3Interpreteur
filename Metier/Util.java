package AlgoPars.Metier;

import java.lang.Character.Subset;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import AlgoPars.Metier.Interpreteur;
import AlgoPars.Metier.Type;
import iut.algo.Console;

/**
 * Classe Util avec des méthodes utiles dans diverses fichier
 */
public class Util
{
	private static String ligneAInterprete = "";
	private static String lastOperateur    = "";
	private static boolean bInterpreter  = true;

	private static List<String> lstTypePourEnChaine = new ArrayList<String>();

	private static Queue<String> file;
	private static Stack<String> pile;

	private static final String REGEX_OP ="(\\(|\\)|<=|>=|!=|\\^|<|>|=|div|mod|\\bxou\\b|\\bou\\b|\\bet\\b|\\bnon\\b|\\+|-|×|©|\\/\\^|\\/|\\\\\\/¯|\\|-?\\w*\\||\\bord\\b|\\bcar\\b|\\benChaine\\b|\\benEntier\\b|\\benReel\\b|\\bplancher\\b|\\barrondi\\b|\\baujourdhui\\b|\\bjour\\b|\\bmois\\b|\\bannee\\b|\\bestReel\\b|\\bestEntier\\b|\\bhasard\\b){1}";
	private static final String REGEX_PRIMI = "(\\bord\\b|\\bcar\\b|\\benChaine\\b|\\benEntier\\b|\\benReel\\b|\\bplancher\\b|\\barrondi\\b|\\baujourdhui\\b|\\bjour\\b|\\bmois\\b|\\bannee\\b|\\bestReel\\b|\\bestEntier\\b|\\bhasard\\b){1}";
	private static final String REGEX_DATE  = "(\\baujourdhui\\b|\\bjour\\b|\\bmois\\b|\\bannee\\b){1}";
	private static final String TRUE  = "true" ;
	private static final String FALSE = "false";
	private static Pattern  pattern = Pattern.compile(Util.REGEX_OP);

	/**
	 * 
	 * @param ligne
	 * @param estConstant
	 * @return
	 */
	public static String getType(String ligne, boolean estConstant)
	{
		String type;

		if (!estConstant)
		{
			String[] decomp = ligne.split(":");
			type = decomp[1].replaceAll(" ", "");
		}
		else
		{
			String val  = Util.getValeur(ligne, true, null);
			type = getTypeAfterValue(val);
		}

		return type;
	}

	public static String getTypeAfterValue(String val)
	{
		String type = "";

		if(val.equals("true") || val.equals("false")) type = Type.BOOLEEN;
		if(val.matches("^\\d+$")                    ) type = Type.ENTIER ;
		if(val.matches("(\\d*)\\.(\\d*)")           ) type = Type.REEL   ;
		if(val.matches("\'.\'")                     ) type = Type.CHAR   ;
		if(type.equals("")                          ) type = Type.CHAINE ;

		return type;
	}

	/**
	 * retourne la valeur contenu dans la ligne
	 * @param ligne
	 * @return la valeur
	 */
	public static String getValeur(String ligne, boolean bAffectParConst, Interpreteur interpreteur)
	{
		String type;
		String valeur[] = ligne.split("<--");
		boolean bOk = false;
		try
		{
			if ( bAffectParConst && ! (valeur[1].indexOf("\"") != -1) )
			valeur[1] = valeur[1].replaceAll(" ", "");

		if ( !bAffectParConst )
		{
			valeur[0]=valeur[0].trim();
			
			type = interpreteur.getDonnee(valeur[0].replaceAll(" |\t", "")).getType();

			if ( !type.equals(Type.CHAINE) && !type.equals(Type.BOOLEEN))
				valeur[1] = String.valueOf(Util.expression(valeur[1].replaceAll(" ", ""), interpreteur));
			
			if ( type.equals(Type.CHAINE) || type.equals(Type.BOOLEEN) ) valeur[1] = Util.expression(valeur[1], interpreteur);
		}

			valeur[1] = valeur[1].replaceAll("^ *\"|\" *$|\t|'" ,"");
		}catch(Exception e){System.out.println("Erreur 003 : Variable inexistante \n");e.printStackTrace();}
		

		return valeur[1];
	}

	public static void setValeurBySwitch(Donnee data, String value, Integer... args)
	{
		switch(data.getType())
		{
			case Type.ENTIER  -> data.setValeur((int)(Double.parseDouble  (value)), args);
			case Type.REEL    -> data.setValeur(Double.parseDouble  (value)       , args);
			case Type.CHAR    -> data.setValeur(value.charAt(0)                   , args);
			case Type.BOOLEEN -> data.setValeur(Boolean.parseBoolean(value)       , args);
			default           -> data.setValeur(                     value        , args);
		}
	}

	   
	public static String expression(String ligne, Interpreteur interpret)
	{
		return Util.expression(ligne, interpret, true);
	}

	public static String expression(String ligne, Interpreteur interpret, boolean bPrimi)
	{
		int taille   ;
		int dernierOp;

		Donnee dataTmp;

		String tmp           ;
		String typeData      ;
		String ligneTmp  = "";
		String operateur = "";

		Util.pile = new Stack     <String>();
		Util.file = new LinkedList<String>();

        Queue<String> fileRet = new LinkedList<String>();

		dernierOp = 0;

		Util.ligneAInterprete = ligne;
		taille = Util.ligneAInterprete.length();
		
		while ( ((operateur = nextOperateur(interpret)) != null || ligneTmp.equals(operateur)) && Util.bInterpreter )
		{
			ligneTmp = Util.ligneAInterprete.substring(0, Util.ligneAInterprete.indexOf(operateur));
			ArrayList<Integer> alInd = new ArrayList<Integer>();
			dataTmp = interpret.getDonnee(ligneTmp.replaceAll(" *", ""));
			if(dataTmp.contains("\\["))
			{
				
			}
			if ( dataTmp != null )ligneTmp = String.valueOf(dataTmp.getValeur());

			Util.file.add(ligneTmp);
			
			System.out.println(operateur);
			Util.ajouterOperateurAPile(operateur);

			Util.ligneAInterprete = Util.ligneAInterprete.substring(Util.ligneAInterprete.indexOf(operateur)+operateur.length());
		}

		dataTmp = interpret.getDonnee(Util.ligneAInterprete.replaceAll(" *", ""));
		if ( dataTmp != null )ligneTmp = String.valueOf(dataTmp.getValeur());
		else ligneTmp                  = Util.ligneAInterprete.replaceAll("^ *\"|\" *$", "");
		
		Util.file.add(ligneTmp);

        while ( !Util.pile.isEmpty() )Util.file.add(Util.pile.pop());

        for ( String val: Util.file)if ( !val.matches("^ *$"))fileRet.add(val);

		Util.lstTypePourEnChaine.clear();
		Util.ligneAInterprete = lastOperateur = "";
		Util.bInterpreter     = true;

		System.out.println(fileRet);

		return Util.evaluerEPO(fileRet, interpret);
	}

	private static void ajouterOperateurAPile(String operateur)
	{
		Util.lastOperateur = operateur;
		switch(operateur)
		{
			case "(" -> Util.pile.add(operateur);
			case ")" ->
			{
				while (  !Util.pile.isEmpty() && !(Util.pile.peek()).equals("(") )Util.file.add(Util.pile.pop());
				Util.pile.pop();
			}
			default  ->
			{
				while ( !Util.pile.isEmpty() && Util.prioSupEgal(Util.pile.peek(), operateur) )
					Util.file.add(Util.pile.pop());

					Util.pile.add(operateur);
			}
		}
	}

    private static String evaluerEPO(Queue<String> fileRet, Interpreteur interpret)
    {
        Stack<String> pileArith = new Stack<String>();
        ArrayList<Boolean> pileLogique = new ArrayList<Boolean>();
        Stack<String> lstOpeLogique = new Stack<String>();
        String val1, val2;
		Donnee dataTmp;

        for ( String val : fileRet )
        {
            if ( val.matches(Util.REGEX_OP) )
            {
			    if(val.matches("xou|ou|et|non"))
			    {
			    	lstOpeLogique.add(val);
			    }
			    else
			    {
					if ( val.matches(REGEX_PRIMI) ) // pour les primitives
					{
						if ( val.matches(REGEX_DATE) )
						{
							switch(val)
							{
								case "aujourdhui" -> pileArith.add(Util.aujourdhui                                      ());
								case "annee"      -> pileArith.add(Util.annee                                           ());
								case "mois"       -> pileArith.add(Util.mois                                            ());
								case "jour"       -> pileArith.add(Util.jour                                            ());
							}
						}
						else
						{
							val1 = pileArith.pop();
							switch (val)
							{
								case "car" -> pileArith.add(String.valueOf(Util.car(Integer.parseInt(Util.retirerPoint(val1)))));
								case "ord" -> 
								{
									if ( val1.contains("'") )val1 = val1.substring(val1.indexOf("'")+1, val1.lastIndexOf("'"));
									pileArith.add(String.valueOf(Util.ord(val1.charAt(0))));
								}
								case "enChaine"   -> pileArith.add(Util.enChaine(Double.parseDouble(val1)));
								case "enEntier"   -> pileArith.add(String.valueOf(Util.enEntier    (Util.retirerPoint(val1))));
								case "enReel"     -> pileArith.add(String.valueOf(Util.enReel      (val1)));
								case "plancher"   -> pileArith.add(String.valueOf(Util.plancher(Double.parseDouble(val1))));
								case "plafond"    -> pileArith.add(String.valueOf(Util.plafond(Double.parseDouble(val1))));
								case "arrondi"    -> pileArith.add(String.valueOf(Util.arrondi (Double.parseDouble(val1))));
								case "estReel"    -> pileLogique.add(Util.estReel  (val1));
								case "estEntier"  -> pileLogique.add(Util.estEntier(val1));
							}
						}
					}
					else
					{
						if(val.matches("(|\\|-?\\w*\\|){1}"))
						{
							val=val.replace('|', ' ');
							pileArith.add(String.valueOf( Math.abs(Double.parseDouble(Util.expression(val.trim(), interpret)))));
						}
						else
						{
							if ( val.equals("\\/¯"))pileArith.add( String.valueOf( (Math.sqrt (Double.parseDouble(pileArith.pop())) )) );
							else
							{
								val1 = pileArith.pop();
								val2 = pileArith.pop();
								
								switch(val) // Traitement opérateur arithmétiques binaires + puissance
								{
									case "+"   -> pileArith.add(String.valueOf(Double.parseDouble(val2) + Double.parseDouble(val1)));
									case "-"   -> pileArith.add(String.valueOf(Double.parseDouble(val2) - Double.parseDouble(val1)));
									case "×"   -> pileArith.add(String.valueOf(Double.parseDouble(val2) * Double.parseDouble(val1)));
									case "/"   -> pileArith.add(String.valueOf(Double.parseDouble(val2) / Double.parseDouble(val1)));
									case "div" -> pileArith.add(String.valueOf((int)Double.parseDouble(val2) / (int)Double.parseDouble(val1)));
									case "mod" -> pileArith.add(String.valueOf(Double.parseDouble(val2) % Double.parseDouble(val1)));
									case "^"   -> pileArith.add(String.valueOf(Math.pow(Double.parseDouble(val2),Double.parseDouble(val1))));
									case "©"   -> pileArith.add(String.valueOf(val2 + val1));
									case ">"  -> pileLogique.add(Double.parseDouble(val2) >  Double.parseDouble(val1));
									case "<"  -> pileLogique.add(Double.parseDouble(val2) <  Double.parseDouble(val1));
									case ">=" -> pileLogique.add(Double.parseDouble(val2) >= Double.parseDouble(val1));
									case "<=" -> pileLogique.add(Double.parseDouble(val2) <= Double.parseDouble(val1));
									case "="  -> pileLogique.add(Double.parseDouble(val2) == Double.parseDouble(val1));
									case "/=" -> pileLogique.add(Double.parseDouble(val2) != Double.parseDouble(val1));
								}
							}
						}
					}
                }
            }
            else
            {
				pileArith.add(val.replaceAll("^ *\"|\" *$", ""));
            }
        }
        
        if (! pileLogique.isEmpty())
        {
        	if(lstOpeLogique.isEmpty())
        	{
        		boolean allTrue = true;
        	
		    	for(Boolean b : pileLogique)
		    		if(b==false)allTrue=false;
        	
        		return String.valueOf(allTrue);
        	}
        	else
        	{        				
        		for(String val : lstOpeLogique)
        		{		    				    		
		    		if(val.matches("non"))
		    			pileLogique.add(!(pileLogique.remove(0)));
		    		else
					{
						boolean valBool1 = pileLogique.remove(0);
						boolean valBool2 = pileLogique.remove(0);
						
						switch(val)
						{
							case "et"  -> pileLogique.add(valBool1 && valBool2);
							case "ou"  -> pileLogique.add(valBool1 || valBool2);
							default    -> pileLogique.add( (valBool1 && !valBool2)||(!valBool1 && valBool2) );
						}
					}
		    	}
		    	
		    	return String.valueOf(pileLogique.remove(0));
        	}
		}
		else return String.valueOf (pileArith.pop());
    }

	private static String retirerPoint(String valeur)
	{
		return valeur.substring(0, (valeur.contains(".")?valeur.indexOf("."):valeur.length()));
	}

	private static boolean prioSupEgal(String st1, String st2)
	{
		int prioSt1 = 0, prioSt2 = 0;

		final String[][] prio =
		                      {  { "("                             },
		                         { "<", ">", "<=", ">=", "/=", "=" },
		                         { "+", "-"                        },
		                         { "×", "/", "mod", "div"          },
		                         { "^", "non", "\\/¯"              },
								 { "ord", "car", "enChaine", "plancher",
								   "enEntier", "enReel", "arrondi", "plafond",
								   "aujourdhui", "jour", "mois", "annee"     ,
								   "estReel", "estEntier", "hasard"          },								   
						      };

		for (int numPrio=0;numPrio<prio.length;numPrio++)
			for (int i=0;i<prio[numPrio].length;i++)
			{
				if ( st1.equals(prio[numPrio][i]) )prioSt1 = numPrio;
				if ( st2.equals(prio[numPrio][i]) )prioSt2 = numPrio;
			}

		return prioSt1>=prioSt2;
	}

	public static String nextOperateur(Interpreteur interpret)
	{
		Donnee dataTmp = null;
		String typeData;
		String nextOperateur = null;
		String tmp = "";
		String cmp = "";
		int cpt = 0;

		CharSequence entreeLigne = Util.ligneAInterprete;
		String patternStr = Util.REGEX_OP;
		Matcher matcher1  = Util.pattern.matcher(entreeLigne);
		Matcher matcher2;

		if ( matcher1.find() )
		{
			nextOperateur = Util.ligneAInterprete.substring(matcher1.start(), matcher1.end());
			// si operateur unaire

			if ( !Util.lastOperateur.equals(")") && (nextOperateur.equals("+") || nextOperateur.equals("-")) && Util.ligneAInterprete.charAt(0) == nextOperateur.charAt(0) )
			{
				tmp = Util.ligneAInterprete.substring(matcher1.end());
				matcher2 = Util.pattern.matcher(tmp);
				if ( matcher2.find() )
				{
					while ( cpt < matcher2.start()+matcher1.end() )
					{
						cmp += Util.ligneAInterprete.charAt(cpt);
						cpt++;
					}
					
					dataTmp = interpret.getDonnee(cmp.substring(1).replaceAll(" *", ""));
					if ( dataTmp != null )cmp = cmp.charAt(0) + String.valueOf(dataTmp.getValeur());

					Util.file.add(cmp);

					Util.ligneAInterprete = Util.ligneAInterprete.substring(matcher2.start()+matcher1.end()+1);
					Util.ajouterOperateurAPile(tmp.substring(matcher2.start(), matcher2.end()));

					return Util.nextOperateur(interpret);
				}
				Util.bInterpreter = false;
			}
			dataTmp = interpret.getDonnee(Util.ligneAInterprete.substring(1).replaceAll(" *", ""));
			if ( dataTmp != null )Util.ligneAInterprete = Util.ligneAInterprete.charAt(0) + String.valueOf(dataTmp.getValeur());

			if ( Util.ligneAInterprete.length() >= 2 )
			{
				if ( Util.ligneAInterprete.substring(0, 2).equals("--") || Util.ligneAInterprete.substring(0, 2).equals("++") )
					Util.ligneAInterprete = Util.ligneAInterprete.substring(2);

				if ( Util.ligneAInterprete.length() >= 2 && Util.ligneAInterprete.substring(0, 2).equals("+-") || Util.ligneAInterprete.substring(0, 2).equals("-+") )
					Util.ligneAInterprete = "-" +Util.ligneAInterprete.substring(2);
			}

			return Util.ligneAInterprete.substring(matcher1.start(), matcher1.end());
		}

		return nextOperateur;
	}

	private static Character car( int  value ){ return (char)value; }
	private static Integer   ord( char value ){ return (int )value; }

	private static String enChaine ( double value ){ return String.valueOf(value); }
	private static String enChaine ( int value    ){ return String.valueOf(value); }

	private static Integer enEntier( String chaine ){ return Integer.parseInt   (chaine); }
	private static Double  enReel  ( String chaine ){ return Double .parseDouble(chaine); }

	private static Integer plancher ( double value ){ return (int)Math.floor(value); }
	private static Integer plafond  ( double value ){ return (int)Math.ceil (value); }
	private static Integer arrondi  ( double value ){ return (int)Math.round(value); }
	
	private static String aujourdhui(){ return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDateTime.now()); }
	private static String jour      (){ return aujourdhui().substring(0, 2); }
	private static String mois      (){ return aujourdhui().substring(3, 5); }
	private static String annee     (){ return aujourdhui().substring(6)   ; }

	private static Boolean estReel  ( String chaine ){ return chaine.matches("^\\d+.\\d+$"); }
	private static Boolean estEntier( String chaine ){ return chaine.matches("^\\d+$"      ); }

	private static Integer hasard( int valeur ){ return (int)(Math.random()*valeur); }
}
