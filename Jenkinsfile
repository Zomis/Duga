
node {
    checkout scm
    sh 'chmod +x gradlew'
    sh './gradlew duga-core:install build'
    sh 'sudo service tomcat8 stop'
    sh 'sudo cp build/libs/Duga-0.2-SNAPSHOT.war /var/lib/tomcat8/webapps/GithubHookSEChatService.war'
    sh 'sudo rm -rf /var/lib/tomcat8/webapps/GithubHookSEChatService*'
    sh 'sudo service tomcat8 start'
}
