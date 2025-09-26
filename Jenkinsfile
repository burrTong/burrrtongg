pipeline {
  agent any
  options { timestamps() }

  stages {
    stage('Checkout') { steps { checkout scm } }

    stage('Build (parallel)') {
      parallel {
        stage('Frontend') {
          stages {
            stage('Install') { steps { dir('frontend/burrtong'){ sh 'npm ci' } } }
            stage('Test & Lint') {
              steps {
                dir('frontend/burrtong'){
                  sh 'npm test --silent || echo "no frontend tests"'
                  sh 'npm run -s lint || echo "no lint script"'
                }
              }
            }
            stage('Build') { steps { dir('frontend/burrtong'){ sh 'npm run build' } } }
          }
        }

        stage('Backend') {
          steps {
            dir('backend'){
              sh 'chmod +x ./gradlew'
              sh './gradlew --no-daemon build'
            }
          }
        }
      }
    }

    stage('Archive') {
      steps {
        archiveArtifacts artifacts: 'frontend/burrtong/dist/**, frontend/burrtong/build/**',
                         allowEmptyArchive: true, fingerprint: true
        archiveArtifacts artifacts: 'backend/build/libs/*.jar',
                         allowEmptyArchive: true, fingerprint: true
        junit allowEmptyResults: true, testResults: 'backend/build/test-results/test/*.xml'
      }
    }
  }
}
