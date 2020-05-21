./gradlew build
./gradlew jar
java -jar build/libs/JSP.jar --solver spt est_spt --instance ft la0 > Resultats/SPT_results.txt
java -jar build/libs/JSP.jar --solver lrpt est_lrpt --instance ft la0 > Resultats/LRPT_results.txt
java -jar build/libs/JSP.jar --solver est_lrpt descent --instance ft la0 > Resultats/DESCENT_results.txt
java -jar build/libs/JSP.jar --solver descent taboo --instance ft la0 > Resultats/TABOO_results.txt
