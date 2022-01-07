import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.plaf.synth.SynthScrollPaneUI;

public class Test
{
    public static void main(String[] args) {
        System.out.println("Resultat final : " + Test.expression("non(4/=4)") );

    }
       
    public static String expression(String ligne)
	{
		int taille   ;
		int dernierOp;

		String tmp     ;
		String ligneTmp  = "";
		String operateur = "";

		Stack<String> pile = new Stack     <String>();
		Queue<String> file = new LinkedList<String>();

        Queue<String> fileRet = new LinkedList<String>();

		dernierOp = 0;
		taille = ligne.length();
		
		while ( (operateur = Test.nextOperateur(ligne)) != null || ligneTmp.equals(operateur) )
		{
			ligneTmp = ligne.substring(0, ligne.indexOf(operateur));

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
                    while ( !pile.isEmpty() && Test.prioSupEgal(pile.peek(), operateur) )
                        file.add(pile.pop());


                    pile.add(operateur);
                }
			}

			ligne = ligne.substring(ligne.indexOf(operateur)+operateur.length());
		}
		file.add(ligne);

        while ( !pile.isEmpty() )file.add(pile.pop());

        for ( String val: file)if ( !val.equals(""))fileRet.add(val);
		
		System.out.println(fileRet);
		return Test.evaluerEPO(fileRet);
	}

    private static String evaluerEPO(Queue<String> file)
    {
        Stack<Double> pile = new Stack<Double>();
        ArrayList<Boolean> pileLogique = new ArrayList<Boolean>();
        Stack<String> lstOpeLogique = new Stack<String>();
        double val1, val2;

        for ( String val : file )
        {
            if ( val.matches("(\\(|\\)|<=|>=|/=|<|>|=|xou|ou|et|non|\\+|-|×|\\^|\\/){1}") )
            {
               
			    if(val.matches("xou|ou|et|non"))
			    {
			    	lstOpeLogique.add(val);
			    }
			    else if(val.matches("(\\\\\\/ ̄ |\\|-?\\d*\\|){1}"))
			    {
			    	switch(val) //
		        	{
		        	   	case "\\/ ̄ " -> pile.add( (double) (Math.sqrt (Double.parseDouble(file.remove())) ) );
		        	   	default ->
		        	   	{
		        	   		val=val.replace('|', ' ');
		        	   		pile.add( Math.abs(Double.parseDouble(val.trim())) );
		        	   	}
					}
			    }
			    else
			    {
					val1 = pile.pop();
					val2 = pile.pop();
					
					switch(val) // Traitement opérateur arithmétiques binaires + puissance
		            {
		                case "+" -> pile.add(val2 + val1);
		                case "-" -> pile.add(val2 - val1);
		                case "×" -> pile.add(val2 * val1);
		                case "/" -> pile.add(val2 / val1);
		                case "^" -> pile.add(Math.pow(val2,val1));
		                case ">"  -> pileLogique.add(new Boolean (val2 >  val1));
						case "<"  -> pileLogique.add(new Boolean (val2 <  val1));
						case ">=" -> pileLogique.add(new Boolean (val2 >= val1));
						case "<=" -> pileLogique.add(new Boolean (val2 <= val1));
						case "="  -> pileLogique.add(new Boolean (val2 == val1));
						case "/=" -> pileLogique.add(new Boolean (val2 != val1));
					}
                }
            }
            else
                pile.add(Double.parseDouble(val));
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
		    			pileLogique.add(new Boolean(!(pileLogique.remove(0))));
		    		else
					{
						boolean valBool1 = pileLogique.remove(0);
						boolean valBool2 = pileLogique.remove(0);
																
						switch(val)
						{
							case "et"  -> pileLogique.add(new Boolean(valBool1 && valBool2));
							case "ou"  -> pileLogique.add(new Boolean(valBool1 || valBool2));
							default    -> pileLogique.add(new Boolean( (valBool1 && !valBool2)||(!valBool1 && valBool2) ));
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
		                         { "+", "-"                  },
		                         { "×", "/", "^"             },
		                         { "non"                           }
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
		String patternStr = "(\\(|\\)|<=|>=|/=|<|>|=|xou|ou|et|non|\\+|-|×|\\^|\\/|\\\\\\/ ̄ |\\|-?\\d*\\|){1}";
		Pattern pattern   = Pattern.compile(patternStr);
		Matcher matcher   = pattern.matcher(entreeLigne);

		if ( matcher.find() )
            return ligne.substring(matcher.start(), matcher.end());

		return nextOperateur;
	}
}
