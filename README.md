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

Nos fichiers de tests sont dans le répertoire FichierAlgo

----------------------------------------------------------------------------------------------------
pour compiler
	installer les paquetages de l'iut comme expliquer sur l'intranet
	sous windows
		installer jansi-2.1.0.jar que vous trouverez dans jar et l'ajouter au CLASSPATH
		
	javac @compile.list -d ./ -encoding UTF8
----------------------------------------------------------------------------------------------------

pour exécuter
	prenez note que dans le nom du fichier que vous saisirez dans le terminal, ne doit pas figurer l'extension
	
	Placeez vous à la racine du répertoire PTS3Interpreteur
	java AlgoPars.Controleur <chemindacces>
	exemple
	java AlgoPars.Controleur FichierAlgo/Test1
	
3
