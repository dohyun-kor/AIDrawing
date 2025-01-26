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
                git branch: 'master',
                url: 'https://lab.ssafy.com/dororo737/d-108-fork.git',
                credentialsId: 'gitlab_dororo737'
            }
        }

        stage('Build') {
          agent {
            docker {
              image 'maven:3.9.9-eclipse-temurin-17'
              args '-v $HOME/.m2:/root/.m2 --group-add 999'
            }
          }
          steps {
            sh 'mvn clean package -DskipTests'
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
            script {
                def Author_ID = sh(script: "git log -1 --pretty=%an", returnStdout: true).trim()
                def Author_Email = sh(script: "git log -1 --pretty=%ae", returnStdout: true).trim()
                mattermostSend (
                    color: 'good',
                    message: "✅ 빌드 성공: ${env.JOB_NAME} #${env.BUILD_NUMBER}\n작성자: ${Author_ID} (${Author_Email})\n(<${env.BUILD_URL}|상세보기>)",
                    endpoint: 'https://meeting.ssafy.com/hooks/sqycn54qc7nh5eho11em34w36w',
                    channel: 'D108jenkins'
                )
            }
        }
        failure {
            script {
                def Author_ID = sh(script: "git log -1 --pretty=%an", returnStdout: true).trim()
                def Author_Email = sh(script: "git log -1 --pretty=%ae", returnStdout: true).trim()
                mattermostSend (
                    color: 'danger',
                    message: "❌ 빌드 실패: ${env.JOB_NAME} #${env.BUILD_NUMBER}\n작성자: ${Author_ID} (${Author_Email})\n(<${env.BUILD_URL}|상세보기>)",
                    endpoint: 'https://meeting.ssafy.com/hooks/sqycn54qc7nh5eho11em34w36w',
                    channel: 'D108jenkins'
                )
            }
        }
    }
}