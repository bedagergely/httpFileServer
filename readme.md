## build
javac *.java

## run class files
java App

## create runnable jar
jar cfe httpFileServer.jar App *.class

## run jar
java -jar httpFileServer.jar

## Stop
Open browser and go to localhost:8080/stop