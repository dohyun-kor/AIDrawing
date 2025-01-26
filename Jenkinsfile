pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "d-108-fork/backend"
        DOCKER_TAG = "latest"
        REGISTRY_CREDENTIAL = "DockerHub"
        SSH_CREDENTIALS = "SSH_CREDENTIALS"
        EC2_USER = "ubuntu"
        EC2_HOST = "i12d108.p.ssafy.io"
        DOCKER_COMPOSE_PATH = "~/d-108-fork"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://lab.ssafy.com/dororo737/d-108-fork.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', REGISTRY_CREDENTIAL) {
                        def app = docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                        app.push()
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                sshagent([SSH_CREDENTIALS]) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} << EOF
                            cd ${DOCKER_COMPOSE_PATH}
                            docker-compose pull backend
                            docker-compose up -d backend
                        EOF
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Deployment successful!'
        }
        failure {
            echo 'Deployment failed.'
        }
    }
}
