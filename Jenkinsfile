pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "dororo737/d-108"
        DOCKER_TAG = "latest"
        REGISTRY_CREDENTIAL = "REGISTRY_CREDENTIAL"
        SSH_CREDENTIALS = "SSH_CREDENTIALS"
        EC2_USER = "ubuntu"
        EC2_HOST = "i12d108.p.ssafy.io"
        DOCKER_COMPOSE_PATH = '/home/ubuntu/d-108'
    }

    stages {
        stage('Checkout') {
            steps {
                // 호스트 에이전트에서 Git 저장소 체크아웃
                checkout scm
            }
        }

        stage('Build') {
            // 빌드를 위한 Docker 컨테이너 (Maven 최신 버전)
            agent {
                docker {
                    image 'maven:3.9.9-eclipse-temurin-17'
                    // ~/.m2 볼륨 마운트, 워크스페이스 마운트 등
                    args '-v $HOME/.m2:/root/.m2 -v $WORKSPACE:/workspace --group-add 999'
                }
            }
            environment {
                // Maven 설정 폴더
                MAVEN_CONFIG = "/root/.m2"
            }
            steps {
                // D108 디렉토리로 이동하여 Maven 빌드 실행
                dir('D108') {
                    // 패키징
                    sh 'mvn clean package -DskipTests'
                }
                // 빌드 결과물 확인
                sh 'ls -la D108/target'

                // JAR 파일 스태시
                stash includes: 'D108/target/*.jar', name: 'jarFiles'
            }
        }

        stage('Docker Build & Push') {
            // DOOD 방식 컨테이너 사용 (docker CLI 이미지를 사용)
            // :latest 대신 stable 태그 등 적절한 이미지를 사용할 수 있습니다.
            agent {
                docker {
                    image 'docker:24.0.2-cli'
                    // 호스트의 Docker 소켓을 마운트하여 호스트 데몬 사용
                    args '-v /var/run/docker.sock:/var/run/docker.sock -u root'
                }
            }
            steps {
                // 스태시된 JAR 파일 언스태시
                unstash 'jarFiles'

                dir('D108') {
                    // 디렉토리 상태 확인
                    sh 'ls -la'
                    sh 'ls -la target'

                    script {
                        // Docker Registry 인증 설정
                        docker.withRegistry('https://index.docker.io/v1/', REGISTRY_CREDENTIAL) {
                            // docker build (DOOD)
                            // 컨테이너 내부에서 실행되지만, 실제로는 호스트 도커 데몬에서 빌드
                            def app = docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}", ".")

                            // 빌드된 이미지는 호스트 데몬에 등록됨
                            // docker info를 통해 확인
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

                // Docker Compose 파일을 EC2 서버로 전송 후, docker compose를 통해 배포
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
docker system prune -f

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
                // 마지막 커밋 작성자 정보
                def Author_ID = sh(script: "git log -1 --pretty=%an", returnStdout: true).trim()
                def Author_Email = sh(script: "git log -1 --pretty=%ae", returnStdout: true).trim()
                // Mattermost 등 알림 전송
                mattermostSend(
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
                mattermostSend(
                    color: 'danger',
                    message: "❌ 빌드 실패: ${env.JOB_NAME} #${env.BUILD_NUMBER}\n작성자: ${Author_ID} (${Author_Email})\n(<${env.BUILD_URL}|상세보기>)",
                    endpoint: 'https://meeting.ssafy.com/hooks/sqycn54qc7nh5eho11em34w36w',
                    channel: 'D108jenkins'
                )
            }
        }
    }
}
