./gradlew duga-core:install build
sudo service tomcat8 stop
sudo cp build/libs/Duga-0.2-SNAPSHOT.war /var/lib/tomcat8/webapps/GithubHookSEChatService.war
sudo rm -rf /var/lib/tomcat8/webapps/GithubHookSEChatService*
sudo service tomcat8 start