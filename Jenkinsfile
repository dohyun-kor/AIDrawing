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

        stage('Deploy') { // 중첩된 stages 블록 제거 및 동일 수준으로 이동
            steps {
                echo "Deploying to ${EC2_HOST} as ${EC2_USER}"

                // Jenkins의 Git 자격 증명을 사용하여 EC2 서버에서 git clone 수행
                withCredentials([usernamePassword(credentialsId: 'gitlab_dororo737', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                    sshagent([SSH_CREDENTIALS]) {
                        sh """
                            ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} <<-EOF
                                set -e
                                echo "현재 디렉토리: \$(pwd)"

                                # 디렉토리가 Git 리포지토리인지 확인
                                if [ -d "${DOCKER_COMPOSE_PATH}" ]; then
                                    if [ -d "${DOCKER_COMPOSE_PATH}/.git" ]; then
                                        echo "디렉토리가 이미 Git 리포지토리입니다. 최신으로 업데이트합니다."
                                        cd ${DOCKER_COMPOSE_PATH}
                                        git pull
                                    else
                                        echo "디렉토리가 존재하지만 Git 리포지토리가 아닙니다. 디렉토리를 삭제하고 클론합니다."
                                        rm -rf ${DOCKER_COMPOSE_PATH}
                                        git clone https://${GIT_USERNAME}:${GIT_PASSWORD}@lab.ssafy.com/dororo737/d-108-fork.git ${DOCKER_COMPOSE_PATH}
                                    fi
                                else
                                    echo "디렉토리가 존재하지 않습니다. 레포지토리를 클론합니다."
                                    git clone https://${GIT_USERNAME}:${GIT_PASSWORD}@lab.ssafy.com/dororo737/d-108-fork.git ${DOCKER_COMPOSE_PATH}
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
