pipeline {
    agent any
    options { timestamps() }
    parameters {
        booleanParam(name: 'RUN_E2E', defaultValue: false, description: 'รัน E2E ตอนนี้หรื)ไม่')
    }
    environment {
        PATH = "/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin"
        CI = 'true'
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
  db:
    image: postgres:13
    environment:
      - POSTGRES_DB=mydatabase
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=password
    ports:
      - "5432"
  backend:
    build: ./backend
    ports: ["18090:8080"]
    environment:
      - SERVER_ADDRESS=0.0.0.0
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/mydatabase
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=password
    depends_on:
      - db
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
echo "Waiting for services to start..."
ATTEMPTS=0
MAX_ATTEMPTS=24

echo "Waiting for database service..."
until docker-compose -f docker-compose.e2e.yml exec -T db pg_isready -U postgres -d mydatabase; do
  if [ ${ATTEMPTS} -ge ${MAX_ATTEMPTS} ]; then
    echo "Database service did not start in time. Aborting."
    exit 1
  fi
  ATTEMPTS=$((ATTEMPTS + 1))
  echo "Waiting for database... (${ATTEMPTS}/${MAX_ATTEMPTS})"
  sleep 5
done
echo "Database service is running."

echo "Waiting for backend service to be healthy..."
ATTEMPTS=0
until curl -sf http://127.0.0.1:18090/actuator/health | grep UP; do
  if [ ${ATTEMPTS} -ge ${MAX_ATTEMPTS} ]; then
    echo "Backend service did not start or is unhealthy. Aborting."
    docker-compose -f docker-compose.e2e.yml logs backend
    exit 1
  fi
  ATTEMPTS=$((ATTEMPTS + 1))
  echo "Waiting for backend health... (${ATTEMPTS}/${MAX_ATTEMPTS})"
  sleep 5
done
echo "Backend service is healthy and running."

echo "Seeding database..."
sleep 5 # Add a small extra delay just in case schema creation is slow
docker-compose -f docker-compose.e2e.yml exec -T db psql -U postgres -d mydatabase -c "INSERT INTO users (username, password, role) VALUES ('customer@customer.com', '\$2a\$10\$R.gJ3r2a5iT.N.d.a.s.e.O.p.q.r.s.t.u.v.w.x.y.z.A.B.C.D.E.F', 'CUSTOMER');"
docker-compose -f docker-compose.e2e.yml exec -T db psql -U postgres -d mydatabase -c "INSERT INTO users (username, password, role) VALUES ('admin@admin.com', '\$2a\$10\$R.gJ3r2a5iT.N.d.a.s.e.O.p.q.r.s.t.u.v.w.x.y.z.A.B.C.D.E.F', 'ADMIN');"
echo "Database seeded."
'''
            }
        }

        stage('E2E Test: Cypress') {
            when { expression { params.RUN_E2E } }
            steps {
                dir('frontend/burrtong') {
                    sh '''
npm run cy:run -- --config baseUrl=http://127.0.0.1:18081 --env backendUrl=http://127.0.0.1:18090
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