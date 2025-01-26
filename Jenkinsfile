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
                        credentialsId: 'gitlab_dororo737'  // 자격증명 ID 명시적 지정
                    }
                }

        stage('Build') {
            agent {  // 이 스테이지에서만 Docker 컨테이너 사용
                docker {
                    image 'maven:3.8.6-openjdk-17'
                    args '-v $HOME/.m2:/root/.m2'
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
                def Author_ID = sh(script: "git show -s --pretty=%an", returnStdout: true).trim()
                def Author_Name = sh(script: "git show -s --pretty=%ae", returnStdout: true).trim()
                mattermostSend (color: 'good',
                message: "빌드 성공: ${env.JOB_NAME} #${env.BUILD_NUMBER} by ${Author_ID}(${Author_Name})\n(<${env.BUILD_URL}|Details>)",
                endpoint: '{endpoint입력}',
                channel: '{channel입력}'
                )
            }
        }
        failure {
        	script {
                def Author_ID = sh(script: "git show -s --pretty=%an", returnStdout: true).trim()
                def Author_Name = sh(script: "git show -s --pretty=%ae", returnStdout: true).trim()
                mattermostSend (color: 'danger',
                message: "빌드 실패: ${env.JOB_NAME} #${env.BUILD_NUMBER} by ${Author_ID}(${Author_Name})\n(<${env.BUILD_URL}|Details>)",
                endpoint: '{endpoint입력}',
                channel: '{channel입력}'
                )
            }
        }
    }
}
