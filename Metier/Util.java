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

import AlgoPars.Metier.Type;
import iut.algo.Console;

/**
 * Classe Util avec des méthodes utiles dans diverses fichier
 */
public class Util
{
	private static final String REGEX_OP  = "(\\(|\\)|<=|>=|!=|\\^|<|>|=|div|mod|xou|ou|et|non|\\+|-|×|©|\\/){1}";
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
			type = interpreteur.getDonnee(valeur[0].replaceAll(" |\t", "")).getType();

			if ( type.equals(Type.ENTIER) || type.equals(Type.REEL) || type.equals(Type.BOOLEEN) )
				valeur[1] = String.valueOf(Util.expression(valeur[1].replaceAll(" ", "")));
			
			if ( type.equals(Type.CHAR) || type.equals(Type.BOOLEEN) )valeur[1] = valeur[1].replaceAll(" ", "");

			if ( type.equals(Type.CHAINE) ) valeur[1] = Util.expression(valeur[1]);
		}

		valeur[1] = valeur[1].replaceAll("^ *\"|\" *$|\t|'" ,"");

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

	   
	public static String expression(String ligne)
	{
		int taille   ;
		int dernierOp;

		String tmp     ;
		String ligneTmp     = "";
		String operateur    = "";
		String operateurTmp = "";

		String dernierFile = "";

		Stack<String> pile = new Stack     <String>();
		Queue<String> file = new LinkedList<String>();

		Queue<String> fileRet = new LinkedList<String>();

		dernierOp = 0;
		taille = ligne.length();
		
		while ( (operateur = Util.nextOperateur(ligne)) != null || ligneTmp.equals(operateur) )
		{
			Console.println(ligneTmp);
			if ( operateur.length() <= ligne.length()) operateurTmp = Util.nextOperateur(ligne.substring(operateur.length()));

			if  ( file.isEmpty() && ligne.indexOf(operateur) == 0 || operateurTmp == null )
			{	
				if ( operateurTmp == null )
				{
					ligneTmp = ligne.substring(0);
					ligne    = "";
				}
				else
				{
					dernierOp = ligne.indexOf(Util.nextOperateur(ligne.substring(operateur.length())));
					ligneTmp = ligne.substring(0, dernierOp);
					ligne    = ligne.substring(ligneTmp.length());
				}

				ligneTmp = ligneTmp.replaceAll("^ *\"|\" *$", "");

				file.add(ligneTmp);
				continue;
			}

			ligneTmp = ligne.substring(0, ligne.indexOf(operateur));
			ligneTmp = ligneTmp.replaceAll("^ *\"|\" *$", "");

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
		file.add(ligne.replaceAll("^ *\"|\" *$", ""));

		while ( !pile.isEmpty() )file.add(pile.pop());

		for ( String val: file)if ( !val.equals(""))fileRet.add(val);

		System.out.println(fileRet);
		return Util.evaluerEPO(fileRet);
	}

	private static String evaluerEPO(Queue<String> file)
	{
		Stack<String> pile    = new Stack<String>();
		String val1, val2;

		for ( String val : file )
		{
			if ( val.matches(Util.REGEX_OP) )
			{
				val1 = pile.pop();
				val2 = pile.pop();

				switch(val)
				{
					case "-"   -> pile.add(String.valueOf(Double.parseDouble(val2) - Double.parseDouble(val1)));
					case "+"   -> pile.add(String.valueOf(Double.parseDouble(val2) + Double.parseDouble(val1)));
					case "×"   -> pile.add(String.valueOf(Double.parseDouble(val2) * Double.parseDouble(val1)));
					case "/"   -> pile.add(String.valueOf(Double.parseDouble(val2) / Double.parseDouble(val1)));
					case "div" -> pile.add(String.valueOf(Double.parseDouble(val2) / Double.parseDouble(val1)));
					case "mod" -> pile.add(String.valueOf(Double.valueOf(val2)%Double.valueOf(val1)));
					case "^"   -> pile.add(String.valueOf(Math.pow(Double.valueOf(val2),Double.valueOf(val1))));
					// case ">"   -> pile.add(String.valueOf(Util.convert(Double.parseDouble(val2)> Double.parseDouble(val1))));
					// case "<"   -> pile.add(String.valueOf(Util.convert(Double.parseDouble(val2)< Double.parseDouble(val1))));
					// case "<="  -> pile.add(String.valueOf(Util.convert(Double.parseDouble(val2)<=Double.parseDouble(val1))));
					// case ">="  -> pile.add(String.valueOf(Util.convert(Double.parseDouble(val2)>=Double.parseDouble(val1))));
					// case "et"  -> pile.add(String.valueOf(Boolean.parseBoolean(val2)&&Boolean.parseBoolean(val1)));
					// case "ou"  -> pile.add(String.valueOf(Boolean.parseBoolean(val2)||Boolean.parseBoolean(val1)));
					case "©"   -> pile.add(String.valueOf(val2 + val1));
				}
			}
			else
				pile.add(val);
		}

		Console.println(pile);
		return pile.pop();
	}

	public static String convert(boolean bOk)
	{
		if ( bOk )return Util.TRUE ;
		else      return Util.FALSE;
	}

	private static boolean prioSupEgal(String st1, String st2)
	{
		int prioSt1 = 0, prioSt2 = 0;

		final String[][] prio =
							  {  { "("                             },
								 { "<", ">", "<=", ">=", "!=", "=" },
								 { "ou", "+", "-"                  },
								 { "et", "×", "/"                  },
								 { "non", "^"                      },
								 { "©"                             }
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
		{
			System.out.println(matcher.start() + " " + matcher.end() );
			return ligne.substring(matcher.start(), matcher.end());
		}
		return nextOperateur;
	}

	public Character car( int  value ){ return (char)value; }
	public Integer   ord( char value ){ return (int )value; }

	public String enChaine ( double value ){ return String.valueOf(value); }
	public String enChaine ( int    value ){ return String.valueOf(value); }

	public Integer enEntier( String chaine ){ return Integer.parseInt   (chaine); }
	public Double  enReel  ( String chaine ){ return Double .parseDouble(chaine); }

	public Integer plancher ( double value ){ return (int)Math.floor(value); }
	public Integer arrondi  ( double value ){ return (int)Math.round(value); }

	public String aujourdhui(){ return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDateTime.now()); }
	public String jour      (){ return aujourdhui().substring(0, 2); }
	public String mois      (){ return aujourdhui().substring(3, 5); }
	public String annee     (){ return aujourdhui().substring(6)   ; }

	public Boolean estReel  ( String chaine ){ return chaine.matches("^\\d+.\\d+$"); }
	public Boolean estEntier( String chaine ){ return chaine.matches("^\\d$"      ); }

	public Integer hasard( int valeur ){ return (int)(Math.random()*valeur); }
}
