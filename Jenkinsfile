pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "dororo737/d-108-fork"
        DOCKER_TAG = "latest"
        REGISTRY_CREDENTIAL = "REGISTRY_CREDENTIAL"
        SSH_CREDENTIALS = "gitlab_jenkins_ssh_key"
        EC2_USER = "ubuntu"
        EC2_HOST = "i12d108.p.ssafy.io"
        DOCKER_COMPOSE_PATH = '/opt/d-108-fork'
        REPO_URL = 'git@gitlab.com:dororo737/d-108-fork.git' // 실제 레포지토리 URL로 변경
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
              args '-v $HOME/.m2:/root/.m2 -v $WORKSPACE:/workspace --group-add 999'
            }
          }
          environment {
            MAVEN_CONFIG = "/root/.m2"
          }
          steps {
            dir('D108') {
              sh 'mvn clean package -DskipTests'
            }
            // Optionally, verify that target/ exists
            sh 'ls -la D108/target'
            // 빌드 아티팩트 스태시
            stash includes: 'D108/target/*.jar', name: 'jarFiles'
          }
        }


        stage('Docker Build & Push') {
            steps {
                // 스태시에서 빌드 아티팩트 언스태시
                unstash 'jarFiles'

                dir('D108'){
                    // 디렉토리 내용 확인 (디버깅 용도)
                    sh 'ls -la'
                    sh 'ls -la target'

                    script {
                        docker.withRegistry('https://index.docker.io/v1/', REGISTRY_CREDENTIAL) {
                            def app = docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}", ".")
                            // Docker 로그인 상태 확인 (디버깅 용도)
                            sh 'docker info'

                            // Docker 이미지 푸시
                            app.push()
                            app.push('latest')
                        }
                    }
                }
            }
        }

        stages {
            stage('Deploy') {
                steps {
                    echo "Deploying to ${EC2_HOST} as ${EC2_USER}"
                    sshagent([SSH_CREDENTIALS]) {
                        sh """
                            ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} <<-EOF
                                set -e
                                echo "현재 디렉토리: \$(pwd)"
                                if [ ! -d "${DOCKER_COMPOSE_PATH}" ]; then
                                    echo "디렉토리가 존재하지 않습니다. 레포지토리를 클론합니다."
                                    git clone https://lab.ssafy.com/dororo737/d-108-fork.git ${DOCKER_COMPOSE_PATH}
                                else
                                    echo "디렉토리가 이미 존재합니다. 최신으로 업데이트합니다."
                                    cd ${DOCKER_COMPOSE_PATH}
                                    git pull
                                fi
                                cd ${DOCKER_COMPOSE_PATH}
                                echo "Docker Compose 파일 존재 여부 확인: \$(ls -la | grep docker-compose.yml)"
                                docker-compose pull backend
                                docker-compose up -d backend
                                echo "Docker 컨테이너 상태 확인:"
                                docker ps | grep backend
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