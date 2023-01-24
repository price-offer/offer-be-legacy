def maindir = "."

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
        stage('Docker image build & push') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerHubRegistryCredential', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        sh '''
                        NEW_TAG=$(git log -1 --pretty=%h)
                        dockerRepo="goharrm/offer-dev"

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
                sshagent(credentials : ["offer-jenkins"]) {
                    sh '''
                        MAIN_PATH=$(pwd)

                        if [ ! -e ~/offer-rollout/application-manifests ];
                        then
                          mkdir -p ~/offer-rollout
                          cd ~/offer-rollout
                          git clone git@github.com:price-offer/application-manifests.git
                        else
                          cd ~/offer-rollout/application-manifests
                          git pull
                        fi

                        NEW_TAG=$(git log -1 --pretty=%h)

                        cd ~/offer-rollout/application-manifests
                        sed -i 's/offer-dev:.*\$/offer-dev:${NEW_TAG}/g' ./services/offer-be-rollout/rollout.yaml

                        git add ./services/offer-be-rollout/rollout.yaml
                        git commit -m "[FROM Jenkins] Container Image Tag was changed to ${NEW_TAG}"
                        git push
                        cd $MAIN_PATH
                    '''
                }
            }
        }
    }
}
