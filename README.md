###############################

L'intégralité de la compilation est automatisée à l'aide du fichier Results.bat
En utilisant la commande ./Results.bat, les fichiers du dossier Resultats sont mis à jour
La compilation automatique se fait sur les instance ft0* et la*

Pour une compilation manuelle :

Compilation du projet :     ./gradlew build  # Compiles the project
Création du jar :           ./gradlew jar
Run :     java -jar build/libs/JSP.jar --solver basic --instance ft0 la

Les solvers disponibles sont spt / lrpt / est_spt / est_lrpt / descent / taboo
