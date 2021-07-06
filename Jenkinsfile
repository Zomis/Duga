#!/usr/bin/env groovy

@Library('ZomisJenkins')
import net.zomis.jenkins.Duga

pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                dir('duga-ktor') {
                    sh './gradlew shadowJar --stacktrace'
                }
            }
        }

        stage('Docker Image') {
            when {
                branch 'main'
            }
            steps {
                script {
                    // Stop running containers
                    sh 'docker ps -q --filter name="duga_bot" | xargs -r docker stop'

                    // Deploy
                    dir('duga-ktor') {
                        sh 'docker build . -t duga_bot'
                    }
                    withCredentials([usernamePassword(
                          credentialsId: 'AWS_CREDENTIALS',
                          passwordVariable: 'AWS_SECRET_ACCESS_KEY',
                          usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
                        withEnv(["ENV_AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}", "ENV_AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}"]) {
                            def result = sh(script: """docker run --network host -d --rm --name duga_bot \
                              -e TZ=Europe/Amsterdam \
                              -e AWS_SECRET_ACCESS_KEY=$ENV_AWS_SECRET_ACCESS_KEY \
                              -e AWS_ACCESS_KEY_ID=$ENV_AWS_ACCESS_KEY_ID \
                              -v /home/zomis/jenkins/duga_bot:/data/logs \
                              -v /etc/localtime:/etc/localtime:ro \
                              -w /data/logs duga_bot""",
                                returnStdout: true)
                            println(result)
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/build/test-results/junit-platform/TEST-*.xml'
        }
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
