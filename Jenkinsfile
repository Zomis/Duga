#!/usr/bin/env groovy

@Library('ZomisJenkins')
import net.zomis.jenkins.Duga

pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'cp /home/zomis/duga.groovy src/main/resources/'
                sh './gradlew duga-core:install build'
            }
        }
        stage('Results') {
            steps {
                junit allowEmptyResults: true, testResults: '**/build/test-results/test/TEST-*.xml'
/*
                withSonarQubeEnv('My SonarQube Server') {
                    // requires SonarQube Scanner for Maven 3.2+
                    sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar'
                }
*/
            }
        }
        stage('Upload Lambdas') {
            when {
                branch 'aws'
            }
            steps {
                dir('Duga') {
                    sh './gradlew shadowJar'
                    sh 'set +x aws lambda update-function-code --function-name duga-hooks --zip-file fileb://duga-aws/build/libs/duga-aws-1.0-SNAPSHOT-all.jar'
                    sh 'set +x aws lambda update-function-code --function-name duga-tasks --zip-file fileb://duga-aws/build/libs/duga-aws-1.0-SNAPSHOT-all.jar'
                    sh 'set +x aws lambda update-function-code --function-name duga-post-from-sqs --zip-file fileb://duga-aws/build/libs/duga-aws-1.0-SNAPSHOT-all.jar'
                }
            }
        }
        stage('Deploy') {
            when {
                branch 'master'
            }
            steps {
                sh 'sudo service tomcat8 stop'
                sh 'sudo rm -rf /var/lib/tomcat8/webapps/GithubHookSEChatService*'
                sh 'sudo cp build/libs/*.war /var/lib/tomcat8/webapps/GithubHookSEChatService.war'
                sh 'sudo service tomcat8 start'
            }
        }
    }

    post {
        success {
            zpost(0)
        }
        unstable {
            zpost(1)
        }
        failure {
            zpost(2)
        }
    }
}
