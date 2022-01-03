public class LectureDonnee
{
	private ArrayList<Donnee> lstDonnee;
	public LectureDonnee()
	{
		this.lstDonnee() = new ArrayList<Donnee>();
	}
	
	
	public void creerVariable(String ligne, String nom)
	{
		Donnee tmp;
		
		switch(this.getType(ligne))
		{
			case "entier"   : tmp = new Donnee<Integer>   (nom, "entier"             , null, false); break;
			case "réel"     : tmp = new Donnee<Double>    (nom, "réel "              , null, false); break;
			case "caractère": tmp = new Donnee<Charactère>(nom, "caractère"          , null, false); break;
			case "booléen"  : tmp = new Donnee<Integer>   (nom, "booléen"            , null, false); break;
			default         : tmp = new Donnee<Integer>   (nom, "chaine de caractère", null, false); break;
			
		}
		this.lstDonnee.add(tmp);
	}
	/*
	public void creerConstante(String ligne)
	{
		Donnee tmp;
		
		switch(this.getType(ligne))
		{
			case "entier"   : tmp = new Donnee<Integer>   (nom, "entier"             , , true); break;
			case "réel"     : tmp = new Donnee<Double>    (nom, "réel "              , null, true); break;
			case "caractère": tmp = new Donnee<Charactère>(nom, "caractère"          , null, true); break;
			case "booléen"  : tmp = new Donnee<Integer>   (nom, "booléen"            , null, true); break;
			default         : tmp = new Donnee<Integer>   (nom, "chaine de caractère", null, true); break;
			
		}
		this.lstDonnee.add(tmp);
	}*/
	
	public String getType(String ligne)
	{
		String[] decomp = ligne.split(":");
		String type = decomp[1].replaceAll(" ", "");
		return type;
	}
}
