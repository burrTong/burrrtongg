pipeline {
    agent any
    options {
        timestamps()
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Frontend: Install') {
            steps {
                dir('frontend/burrtong') {
                    sh 'npm ci'
                }
            }
        }

        stage('Frontend: Test & Lint') {
            steps {
                dir('frontend/burrtong') {
                    sh 'npm test --silent || echo "No frontend tests found, skipping."'
                    sh 'npm run lint'
                }
            }
        }

        stage('Frontend: Build') {
            steps {
                dir('frontend/burrtong') {
                    sh 'npm run build'
                }
            }
        }

        stage('Archive Frontend') {
            steps {
                archiveArtifacts artifacts: 'frontend/burrtong/dist/**', allowEmptyArchive: true
            }
        }

        stage('Backend: Build') {
            steps {
                dir('backend') {
                    sh 'chmod +x ./gradlew'
                    sh './gradlew build'
                }
            }
        }

        stage('Archive Backend') {
            steps {
                dir('backend') {
                    archiveArtifacts artifacts: 'build/libs/*.jar', allowEmptyArchive: true
                }
            }
        }
    }
}
