def maindir = "."
def dockerRepo = "goharrm/offer-dev"
def NEW_TAG = "latest"
def LATEST = "latest"

pipeline {
    agent any

    stages {
        stage('Pull Source Code') {
            steps {
                checkout scm
            }
        }
        stage('Build Source Code') {
            steps {
                sh '''
                id
                java -version
                ./gradlew clean build -x test -x jacocoTestReport -x createDocument -x displaceDocument -x sonarqube
                '''
            }
        }
        stage('Get new image tag from commit hash') {
            steps {
                script{
                    NEW_TAG = sh(script: 'git log -1 --pretty=%h', returnStdout: true).trim()
                }
            }
        }
        stage('Docker image build & push') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerHubRegistryCredential', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        sh '''
                        ./gradlew jib \
                            -Djib.to.auth.username=${USERNAME} \
                            -Djib.to.auth.password=${PASSWORD} \
                            -Djib.to.image=${dockerRepo}:${NEW_TAG} \
                            -Djib.console='plain'

                        sleep 10
                        '''
                    }
                }
            }
        }
        stage('Argo Rollout Manifest Update') {
            steps {
                sh '''
                    if [ ! -e ~/offer-rollout ]; 
                    then
                      mkdir ~/offer-rollout 
                      cd ~/offer-rollout
                      git clone git@github.com:price-offer/application-manifests.git
                    else
                      cd ~/offer-rollout/application-manifests
                      git pull
                    fi

                    cd ~/offer-rollout/application-manifests
                    sed -i 's/offer-dev:.*\$/offer-dev:${NEW_TAG}/g' ./services/offer-be-rollout/rollout.yaml
                    git add ./services/offer-be-rollout/rollout.yaml
                    git commit -m "[FROM Jenkins] Container Image Tag was changed to ${NEW_TAG}"
                    git push
                    cd ..
                    cd ..
                '''
            }
        }
    }
}
