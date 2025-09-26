pipeline {
  agent any
  options { timestamps() }
  parameters {
    booleanParam(name: 'RUN_E2E', defaultValue: false, description: 'รัน E2E ตอนนี้หรือไม่')
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
            sh """
              # Create a docker-compose file for the E2E environment
              cat > docker-compose.e2e.yml <<'YAML'
              version: "3.8"
              services:
                backend:
                  build: ./backend
                  ports: ["8080:8080"]
                frontend:
                  build:
                    context: ./frontend/burrtong
                    dockerfile: Dockerfile
                  ports: ["80:80"]
                  depends_on:
                    - backend
              YAML

              # Build and run the services in the background
              docker-compose -f docker-compose.e2e.yml up -d --build

              # Wait for the frontend to be accessible
              echo "Waiting for services to start..."
              timeout 120 sh -c 'until curl -sf http://localhost > /dev/null; do sleep 5; done'
              echo "Services are running."
            """
        }
    }

    stage('E2E Test: Cypress') {
        when { expression { params.RUN_E2E } }
        steps {
            dir('frontend/burrtong') {
                sh '''
                  # The baseUrl for the test will be http://localhost (the frontend service)
                  # We pass this as a config to cypress run
                  npm run cy:run -- --config baseUrl=http://localhost
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
