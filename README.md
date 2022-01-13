# PTS3Interpreteur
# ReadMe Interpreteur pseudo code
# Author : Benjamin Cléon, Alan Grenet, Paul Rayer, Luna Sebastiao
----------------------------------------------------------------------------------------------------

Permet d'interpreter un algorithme écris en pseudo-code et de montrer la trace des variables 
choisies par l'utilisateur, ainsi que la trace d'exécution.

----------------------------------------------------------------------------------------------------

Après avoir décompresser l'archive récupérer sur ce dépot
Placer vous dans PTS3Interpreteur-main\PTS3Interpreteur-main

----------------------------------------------------------------------------------------------------
pour compiler
	installer les paquetages de l'iut comme expliquer sur l'intranet
	sous windows
		installer jansi-2.1.0.jar que vous trouverez dans jar et l'ajouter au CLASSPATH
		
	javac @compile.list -d <chemin d'accès au classpath> -encoding UTF8
	par exemple javac @compile.list -d C:\iut\TP\java\paquetage_class -encoding UTF8
----------------------------------------------------------------------------------------------------

pour exécuter
	prenez note que dans le nom du fichier que vous saisirez dans le terminal, ne doit pas figurer l'extension
	Si vous ne lancez pas depuis le repértoire courant
	java AlgoPars.Controleur <cheminabsolu/nomdufichier> 
	par exemple java AlgoPars.Controleur C:\Users\Benjamin\OneDrive\Bureau\IUT\S3\ProjetTut\Dev\PTS3Interpreteur\FichierAlgo\Test1
	
	Si vous lancez depuis le répertoire courant
	java AlgoPars.Controleur <cheminabsolu/nomdufichier> ou java AlgoPars.Controleur <cheminrelatif/nomdufichier>
	par exemple java AlgoPars.Controleur FichierAlgo/Test1
	
