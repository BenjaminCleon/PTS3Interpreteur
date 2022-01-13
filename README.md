# PTS3Interpreteur
# ReadMe Interpreteur pseudo code
# Author : Benjamin Cléon, Alan Grenet, Paul Rayer, Luna Sebastiao
----------------------------------------------------------------------------------------------------

Permet d'interpreter un algorithme écris en pseudo-code et de montrer la trace des variables 
choisies par l'utilisateur, ainsi que la trace d'exécution.

----------------------------------------------------------------------------------------------------
se placer dans TPS3Interpreteur
pour compiler
	installer les paquetages de l'iut comme expliquer sur l'intranet
	sous windows
		installer jansi-2.1.0.jar que vous trouverez dans jar et l'ajouter au CLASSPATH
	javac @compile.list -d <chemin d'accès au classpath> -encoding UTF8

----------------------------------------------------------------------------------------------------

pour exécuter
	java AlgoPars/Controleur FichierAlgo/NOM_DU_FICHIER
où NOM_DU_Fichier correspond au nom de fichier .algo où se trouve le code et au .var ou se trouve 
les variables à tracer.
