import java.io.File;
import java.util.Scanner;

public class GestionVariable {
	
	private String[] tabVar;
	private int nbVar;
	
	public GestionVariable (String nomFichier) {
		File fichier = new File (nomFichier.concat(".var"));
		this.tabVar = new String[20];
		this.nbVar = 0;
		
		if (fichier.exists()) {
			try {
				Scanner sc = new Scanner ( fichier );
				
				while (sc.hasNextLine()) {
					String ligne = sc.nextLine();
					this.tabVar[this.nbVar++] = ligne;
				}
				
			} catch (Exception e) {}
		}
		
		System.out.println("" + this.nbVar );
	}
	
	String affichage () {
		String sRet = "";
		
		for(int i=0; i < 11; i++) sRet += "\"";
		sRet += "\n| DONNEES |\n";
		
		for(int i=0; i < 42; i++) sRet += "\"";
		sRet += "\n|     NOM         |         VALEUR       |\n";
		
		for(int i=0; i < this.nbVar; i++) {
			String valVar = this.tabVar[i];
			
			if(valVar.length() > 10) {
				String debut = valVar.substring(0,5);
				String fin = valVar.substring((valVar.length()-3), valVar.length());
				valVar = debut.concat(fin);
			}
			
			sRet += "| " + String.format("%-15s", valVar) + " | " + String.format("%20s", valVar) + " |\n";
		}
		
		for(int i=0; i < 42; i++) sRet += "\"";
		sRet += "\n";
		
		return sRet;
	}
	
	
	void affichageDetaillee (String var) {
		String sRet = "";
		String valTest = "Intellectum est enim mihi quidem in multis, et maxime in me ipso, sed paulo ante in omnibus, cum M. Marcellum senatui reique publicae concessisti, commemoratis praesertim offensionibus, te auctoritatem huius ordinis dignitatemque rei publicae tuis vel doloribus vel suspicionibus anteferre. Ille quidem fructum omnis ante actae vitae hodierno die maximum cepit, cum summo consensu senatus, tum iudicio tuo gravissimo et maximo. Ex quo profecto intellegis quanta in dato beneficio sit laus, cum in accepto sit tanta gloria.";
		
		for(int i=0; i < 11; i++) sRet += "\"";
		sRet += "\n| DONNEES |\n";
		
		for(int i=0; i < 85; i++) sRet += "\"";
		sRet += "\n|     NOM         |                           VALEUR                                |\n";
		
		sRet += "| " + String.format("%-15s", var) + " | ";
		
		/*
		if(valTest.length() > 63) {
			sRet += String.format ("%63s", valTest.substring(0,63)) + " |\n";
			valTest = valTest.substring(63,valTest.length());
			
			while(valTest.length() > 63) {
				sRet += "| " + String.format ("%-15s", " ") + " | " + String.format ("%63s", valTest.substring(0,63)) + " |\n";
				valTest = valTest.substring(63,valTest.length());
			}
		}
		else sRet += String.format ("%63s", valTest.substring(0,63)) + " |\n";
		
		
		for(int i=0; i < 85; i++) sRet += "\"";
		sRet += "\n";
		
		System.out.println( sRet + "\n"); */
		
		//this.affichage();
	}
	
	public static void main(String[] arg) {
		GestionVariable g = new GestionVariable("exemple");
		g.affichageDetaillee("test");
	}
}
