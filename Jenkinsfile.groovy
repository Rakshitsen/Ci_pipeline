pipeline {
    agent any

    stages {
        stage('Cloning the Repo') {
            steps {
                git branch: 'main', url: 'https://github.com/Rakshitsen/Ci_pipeline.git'
            }
        }

        stage('Docker Image') {
            steps {
                sh "docker image prune -a -f"
                sh "docker build -t rakshitsen/central-image:${BUILD_NUMBER} ."
                sh "docker images"
            }
        }

        // Removed the commented-out stage with incorrect brackets
        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-cred', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                    sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin'
                    sh "docker push rakshitsen/central-image:${BUILD_NUMBER}"
                    sh "docker logout"
                }
            }
        }

        stage('Docker Run') {
            steps {
                sh '''
                CONTAINERS=$(docker ps -aq)
                if [ ! -z "$CONTAINERS" ]; then
                    docker rm -f $CONTAINERS
                fi
                '''
                sh "docker run -d -p 85:80 --name central-container-${BUILD_NUMBER} rakshitsen/central-image:${BUILD_NUMBER}"
            }
        }
    }
}
