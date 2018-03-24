
node {
    checkout scm
    sh 'chmod +x gradlew'
    sh 'cp /home/zomis/duga.groovy src/main/resources/'
    sh './gradlew duga-core:install build'
    sh 'sudo service tomcat8 stop'
    sh 'sudo rm -rf /var/lib/tomcat8/webapps/GithubHookSEChatService*'
    sh 'sudo cp build/libs/*.war /var/lib/tomcat8/webapps/GithubHookSEChatService.war'
    sh 'sudo service tomcat8 start'
}
