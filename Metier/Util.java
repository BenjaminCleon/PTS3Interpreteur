package AlgoPars.Metier;

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
	private static final String REGEX_OP    = "(\\(|\\)|<=|>=|!=|\\^|<|>|=|div|mod|xou|ou|et|non|\\+|-|×|\\d+\\+|\\d+-|©|\\/\\^|\\/|\\\\\\/¯|\\|-?\\d*\\|){1}";
	private static final String REGEX_PRIMI = "ord\\(|car\\(|enChaine\\(|enEntier\\(|enReel\\(|plancher\\(|arrondi\\(";
	
	private static final String TRUE  = "true" ;
	private static final String FALSE = "false";
	
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

		if ( bAffectParConst && ! (valeur[1].indexOf("\"") != -1) )
			valeur[1] = valeur[1].replaceAll(" ", "");

		if ( !bAffectParConst )
		{
			valeur[0]=valeur[0].trim();
			
			type = interpreteur.getDonnee(valeur[0].replaceAll(" |\t", "")).getType();

			if ( !type.equals(Type.CHAINE) )
				valeur[1] = String.valueOf(Util.expression(valeur[1].replaceAll(" ", ""), interpreteur));
			
			if ( type.equals(Type.CHAINE) ) valeur[1] = Util.expression(valeur[1], interpreteur);
		}

		valeur[1] = valeur[1].replaceAll("^ *\"|\" *$|\t|'" ,"");

		return valeur[1];
	}

	public static void setValeurBySwitch(Donnee data, String value, Integer... args)
	{
		System.out.println(data);
		
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

		String tmp     ;
		String ligneTmp  = "";
		String operateur = "";

		Stack<String> pile = new Stack     <String>();
		Queue<String> file = new LinkedList<String>();

        Queue<String> fileRet = new LinkedList<String>();

		dernierOp = 0;

		if ( bPrimi ) ligne = Util.traiterPrimi(ligne, interpret);

		taille = ligne.length();
		
		System.out.println(ligne);
		
		while ( (operateur = nextOperateur(ligne)) != null || ligneTmp.equals(operateur) )
		{
			System.out.println(ligneTmp);
			
			ligneTmp = ligne.substring(0, ligne.indexOf(operateur)).replaceAll("^ *\"|\" *$", "");
			dataTmp = interpret.getDonnee(ligneTmp.replaceAll(" *", ""));
			System.out.println("|"+ligneTmp+"|"+((dataTmp!=null)?dataTmp.getValeur():null)+"|");
			if ( dataTmp != null )ligneTmp = String.valueOf(dataTmp.getValeur());
			
			file.add(ligneTmp);
			
			switch(operateur)
			{
				case "(" -> pile.add(operateur);
				case ")" ->
				{
					while (  !pile.isEmpty() && !(pile.peek()).equals("(") )file.add(pile.pop());
					pile.pop();
				}
				default  ->
				{
					while ( !pile.isEmpty() && Util.prioSupEgal(pile.peek(), operateur) )
						file.add(pile.pop());

					pile.add(operateur);
				}
			}

			ligne = ligne.substring(ligne.indexOf(operateur)+operateur.length());
		}

		dataTmp = interpret.getDonnee(ligne.replaceAll(" *", ""));
		if ( dataTmp != null )ligne = String.valueOf(dataTmp.getValeur());
		else                  ligne = ligne.replaceAll("^ *\"|\" *$", "");
		
		file.add(ligne);

        while ( !pile.isEmpty() )file.add(pile.pop());

        for ( String val: file)if ( !val.equals(""))fileRet.add(val);
		
		return Util.evaluerEPO(fileRet);
	}

    private static String evaluerEPO(Queue<String> file)
    {
        Stack<String> pile = new Stack<String>();
        ArrayList<Boolean> pileLogique = new ArrayList<Boolean>();
        Stack<String> lstOpeLogique = new Stack<String>();
        String val1, val2;

        for ( String val : file )
        {
            if ( val.matches(Util.REGEX_OP) )
            {
               
			    if(val.matches("xou|ou|et|non"))
			    {
			    	lstOpeLogique.add(val);
			    }
			    else if(val.matches("(\\\\\\/¯|\\|-?\\d*\\|){1}"))
			    {
			    	switch(val) //
		        	{
		        	   	case "\\/¯" -> pile.add( String.valueOf( (Math.sqrt (Double.parseDouble(file.remove())) )) );
		        	   	default ->
		        	   	{
		        	   		val=val.replace('|', ' ');
		        	   		pile.add(String.valueOf( Math.abs(Double.parseDouble(val.trim())) ));
		        	   	}
					}
			    }
			    else
			    {				
					if(val.contains("-"))
					{
						String valPrc = val.substring(0, val.indexOf('-'));
						pile.add( String.valueOf(Double.parseDouble(valPrc) - Double.parseDouble(pile.pop())) );
					}
					else if(val.contains("+"))
					{
						String valPrc = val.substring(0, val.indexOf('+'));
						pile.add( String.valueOf(Double.parseDouble(valPrc) + Double.parseDouble(pile.pop())) );
					}
					else
					{
					
						val1 = pile.pop();
						val2 = pile.pop();
						
						switch(val) // Traitement opérateur arithmétiques binaires + puissance
				        {
							case "×" -> pile.add(String.valueOf(Double.parseDouble(val2) * Double.parseDouble(val1)));
							case "/" -> pile.add(String.valueOf(Double.parseDouble(val2) / Double.parseDouble(val1)));
							case "^" -> System.out.println("Test:" + pile.add(String.valueOf(Math.pow(Double.parseDouble(val2),Double.parseDouble(val1)))) );
							case "©" -> pile.add(String.valueOf(val2 + val1));
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
            else
            {
				pile.add(val);
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
		else return String.valueOf (pile.pop());
    }

	private static boolean prioSupEgal(String st1, String st2)
	{
		int prioSt1 = 0, prioSt2 = 0;

		final String[][] prio =
		                      {  { "("                             },
		                         { "<", ">", "<=", ">=", "/=", "=" },
		                         { "+", "-"                        },
		                         { "×", "/"                        },
		                         { "^", "non"                      }
						      };

		for (int numPrio=0;numPrio<prio.length;numPrio++)
			for (int i=0;i<prio[numPrio].length;i++)
			{
				if ( st1.equals(prio[numPrio][i]) )prioSt1 = numPrio;
				if ( st2.equals(prio[numPrio][i]) )prioSt2 = numPrio;
			}

		return prioSt1>=prioSt2;
	}

	public static String nextOperateur(String ligne)
	{
		String nextOperateur = null;

		CharSequence entreeLigne = ligne;
		String patternStr = Util.REGEX_OP;
		Pattern pattern   = Pattern.compile(patternStr);
		Matcher matcher   = pattern.matcher(entreeLigne);

		if ( matcher.find() )
			return ligne.substring(matcher.start(), matcher.end());

		return nextOperateur;
	}

	private static String traiterPrimi(String ligne, Interpreteur interpret)
	{
		System.out.println(ligne);
		String res = "";
		String tmp = "";

		String ligneNext = "";
		String postLigne = "";

		int posLastBracket   = 0;
		int nbBracketWaiting = 1;

		CharSequence entreeLigne = ligne;
		String patternStr = REGEX_PRIMI;
		Pattern pattern = Pattern.compile(patternStr) ;
		Matcher matcher1 = pattern.matcher(entreeLigne);
		Matcher matcher2;

		if ( !matcher1.find() )return Util.expression(ligne, interpret, false);

		for (int i=matcher1.start();i<ligne.length()&&nbBracketWaiting!=0;i++)
		{
			if ( ligne.charAt(i) == ')' ){ posLastBracket = i; nbBracketWaiting--; }
			if ( ligne.charAt(i) == '(' )nbBracketWaiting++;
		}

		ligneNext = ligne.substring(matcher1.end(), posLastBracket);
		/* postLigne = ligne.substring(posLastBracket+1);
		System.out.print("[" + postLigne + "]");
		System.out.println( "{"  + ((postLigne.equals(""))?"":Util.expression(postLigne, interpret, true)) + "}"); */

		matcher2 = pattern.matcher(ligneNext);
		if ( matcher2.find() ) tmp = Util.expression(ligneNext, interpret, true );
		else                   tmp = Util.expression(ligneNext, interpret, false);

		System.out.println(ligne.substring(matcher1.start(), matcher1.end()-1 ) + "|" +  tmp);

		switch( ligne.substring(matcher1.start(), matcher1.end()-1 ) )
		{
			case "car" -> res = String.valueOf(Util.car(Integer.parseInt(tmp)));
			case "ord" -> res = String.valueOf(Util.ord(tmp.charAt(1)));
		}

		System.out.println(res);
		return res;
	}

	private static Character car( int  value ){ return (char)value; }
	private static Integer   ord( char value ){ return (int )value; }

	private static String enChaine ( double value ){ return String.valueOf(value); }
	private static String enChaine ( int    value ){ return String.valueOf(value); }

	private static Integer enEntier( String chaine ){ return Integer.parseInt   (chaine); }
	private static Double  enReel  ( String chaine ){ return Double .parseDouble(chaine); }

	private static Integer plancher ( double value ){ return (int)Math.floor(value); }
	private static Integer arrondi  ( double value ){ return (int)Math.round(value); }

	private static String aujourdhui(){ return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDateTime.now()); }
	private static String jour      (){ return aujourdhui().substring(0, 2); }
	private static String mois      (){ return aujourdhui().substring(3, 5); }
	private static String annee     (){ return aujourdhui().substring(6)   ; }

	private static Boolean estReel  ( String chaine ){ return chaine.matches("^\\d+.\\d+$"); }
	private static Boolean estEntier( String chaine ){ return chaine.matches("^\\d$"      ); }

	private static Integer hasard( int valeur ){ return (int)(Math.random()*valeur); }
}
