pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "dororo737/d-108-fork"
        DOCKER_TAG = "latest"
        REGISTRY_CREDENTIAL = "REGISTRY_CREDENTIAL"
        SSH_CREDENTIALS = "SSH_CREDENTIALS"
        EC2_USER = "ubuntu"
        EC2_HOST = "i12d108.p.ssafy.io"
        DOCKER_COMPOSE_PATH = '/home/ubuntu/d-108-fork'
        // Generic Webhook Trigger에서 설정한 환경 변수
        PUSHER_NAME = "${PUSHER_NAME}"
    }

    stages {
        stage('Checkout') {
            steps {
                // Git 레포지토리를 Jenkins 워크스페이스에 체크아웃
                git branch: 'master',
                    url: 'https://lab.ssafy.com/dororo737/d-108-fork.git',
                    credentialsId: 'gitlab_dororo737'
            }
        }

        stage('Build') {
            agent {
                // 빌드를 위한 Docker 컨테이너 사용 (Maven 최신 버전)
                docker {
                    image 'maven:3.9.9-eclipse-temurin-17'
                    args '-v $HOME/.m2:/root/.m2 -v $WORKSPACE:/workspace --group-add 999'
                }
            }
            environment {
                // Maven 설정 폴더 지정
                MAVEN_CONFIG = "/root/.m2"
            }
            steps {
                // 빌드 작업 실행
                dir('D108') {
                    // 테스트 스킵하고 패키징
                    sh 'mvn clean package'
                }
                // 빌드 아티팩트 확인
                sh 'ls -la D108/target'
                // 빌드 결과물 스태시
                stash includes: 'D108/target/*.jar', name: 'jarFiles'
            }
        }

        stage('Docker Build & Push') {
            steps {
                // 스태시된 빌드 아티팩트 언스태시
                unstash 'jarFiles'

                dir('D108') {
                    // 디렉토리 상태 확인
                    sh 'ls -la'
                    sh 'ls -la target'

                    script {
                        // Docker Registry 인증 설정
                        docker.withRegistry('https://index.docker.io/v1/', REGISTRY_CREDENTIAL) {
                            // 이미지 빌드
                            def app = docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}", ".")
                            // Docker 정보 확인
                            sh 'docker info'
                            // 이미지 푸시
                            app.push()
                        }
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                echo "Deploying to ${EC2_HOST} as ${EC2_USER}"

                // Docker Compose 파일을 Jenkins 워크스페이스에서 EC2 서버로 전송
                withCredentials([
                    usernamePassword(
                        credentialsId: 'gitlab_dororo737',
                        usernameVariable: 'GIT_USERNAME',
                        passwordVariable: 'GIT_PASSWORD'
                    )
                ]) {
                    sshagent([SSH_CREDENTIALS]) {
                        // SCP를 사용하여 docker-compose.yml 전송
                        sh """
                            scp -o StrictHostKeyChecking=no docker-compose.yml ${EC2_USER}@${EC2_HOST}:${DOCKER_COMPOSE_PATH}/docker-compose.yml
                        """

                        // EC2 서버에서 Docker Compose 실행
                        sh """
                            ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} /bin/bash <<EOS
cd "${DOCKER_COMPOSE_PATH}"
docker compose pull
docker compose up -d --force-recreate

# 실행 확인
sleep 5
docker ps || echo "Container check failed"
EOS
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            script {
                // Mattermost 등 알림 전송
                mattermostSend (
                    color: 'good',
                    message: "✅ 빌드 성공: ${env.JOB_NAME} #${env.BUILD_NUMBER}\n작성자: ${env.PUSHER_NAME} \n(<${env.BUILD_URL}|상세보기>)",
                    endpoint: 'https://meeting.ssafy.com/hooks/sqycn54qc7nh5eho11em34w36w',
                    channel: 'D108jenkins'
                )
            }
        }
        failure {
            script {
                mattermostSend (
                    color: 'danger',
                    message: "❌ 빌드 실패: ${env.JOB_NAME} #${env.BUILD_NUMBER}\n작성자: ${env.PUSHER_NAME} (${env.PUSHER_EMAIL})\n(<${env.BUILD_URL}|상세보기>)",
                    endpoint: 'https://meeting.ssafy.com/hooks/sqycn54qc7nh5eho11em34w36w',
                    channel: 'D108jenkins'
                )
            }
        }
    }
}