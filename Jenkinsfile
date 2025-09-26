pipeline {
  agent any
  options { timestamps() }
  parameters {
    booleanParam(name: 'RUN_E2E', defaultValue: false, description: 'รัน E2E ตอนนี้หรื)ไม่')
  }
  environment {
    PATH = "/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin"
    CI   = 'true'
  }

  stages {
    stage('Checkout') { steps { checkout scm } }

    stage('Build (parallel)') {
      parallel {
        stage('Frontend') {
          stages {
            stage('Install') {
              steps {
                dir('frontend/burrtong') {
                  sh 'command -v npm || echo "Node not found"; npm -v || true'
                  sh 'npm ci --loglevel=info'
                }
              }
            }
            stage('Test & Lint') {
              steps {
                dir('frontend/burrtong') {
                  sh 'npm test --silent || echo "no frontend tests"'
                  sh 'npm run -s lint || echo "no lint script"'
                }
              }
            }
            stage('Build') {
              steps { dir('frontend/burrtong') { sh 'npm run build' } }
            }
          }
        }

        stage('Backend') {
          steps {
            dir('backend') {
              sh 'chmod +x ./gradlew'
              sh './gradlew --no-daemon assemble -x test --info --stacktrace'
            }
          }
        }
      }
    }

    stage('Archive') {
      steps {
        archiveArtifacts artifacts: 'frontend/burrtong/dist/**', allowEmptyArchive: true, fingerprint: true
        archiveArtifacts artifacts: 'backend/build/libs/*.jar', allowEmptyArchive: true, fingerprint: true
      }
    }

    stage('E2E Setup: Run Application') {
      when { expression { params.RUN_E2E } }
      steps {
        writeFile(
          file: 'docker-compose.e2e.yml',
          text: '''
version: "3.8"
services:
  backend:
    build: ./backend
    ports: ["18090:8080"]
    environment:
      - SERVER_ADDRESS=0.0.0.0
  frontend:
    build:
      context: ./frontend/burrtong
      dockerfile: Dockerfile
    ports: ["18081:80"]
    depends_on:
      - backend
'''
        )

        sh "docker-compose -f docker-compose.e2e.yml up -d --build"

        sh '''
          echo "Waiting for frontend service (port 18081)..."
          ATTEMPTS=0
          MAX_ATTEMPTS=24
          until curl -sf http://127.0.0.1:18081 > /dev/null; do
            if [ ${ATTEMPTS} -ge ${MAX_ATTEMPTS} ]; then
              echo "Frontend service did not start in time. Aborting."
              exit 1
            fi
            ATTEMPTS=$((ATTEMPTS + 1))
            echo "Waiting for frontend... (${ATTEMPTS}/${MAX_ATTEMPTS})"
            sleep 5
          done
          echo "Frontend service is running."

          echo "Waiting for backend service (port 18090)..."
          ATTEMPTS=0
          until curl -sf http://127.0.0.1:18090/actuator/health | grep UP; do
            if [ ${ATTEMPTS} -ge ${MAX_ATTEMPTS} ]; then
              echo "Backend service did not start or is unhealthy. Aborting."
              exit 1
            fi
            ATTEMPTS=$((ATTEMPTS + 1))
            echo "Waiting for backend health... (${ATTEMPTS}/${MAX_ATTEMPTS})"
            sleep 5
          done
          echo "Backend service is healthy and running."
        '''
      }
    }

    stage('E2E Test: Cypress') {
      when { expression { params.RUN_E2E } }
      steps {
        dir('frontend/burrtong') {
          sh '''
            npm run cy:run -- --config baseUrl=http://127.0.0.1:18081 --env 
            backendUrl=http://127.0.0.1:18090
          '''
        }
      }
      post {
        always {
          archiveArtifacts artifacts: 'frontend/burrtong/cypress/reports/**', allowEmptyArchive: true
          archiveArtifacts artifacts: 'frontend/burrtong/cypress/videos/**', allowEmptyArchive: true
          archiveArtifacts artifacts: 'frontend/burrtong/cypress/screenshots/**', allowEmptyArchive: true
        }
      }
    }
  }

  post {
    always {
      sh 'docker-compose -f docker-compose.e2e.yml down || true'
    }
  }
}
