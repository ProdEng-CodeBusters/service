pipeline {
    agent any
    environment {
        DOCKER_PASSWORD = credentials("docker_password")
        GITHUB_TOKEN = credentials("github_token")
    }
    
    stages {
        stage('Build & Test') {
            steps {
                sh './gradlew clean build'
            }
        }
        
        stage('Tag image') {
            steps {
                script {
                    sh([script: 'git fetch --tag', returnStdout: true]).trim()
                    env.MAJOR_VERSION = sh([script: 'git tag | sort --version-sort | tail -1 | cut -d . -f 1', returnStdout: true]).trim()
                    env.MINOR_VERSION = sh([script: 'git tag | sort --version-sort | tail -1 | cut -d . -f 2', returnStdout: true]).trim()
                    env.PATCH_VERSION = sh([script: 'git tag | sort --version-sort | tail -1 | cut -d . -f 3', returnStdout: true]).trim()
                    env.IMAGE_TAG = "${env.MAJOR_VERSION}.\$((${env.MINOR_VERSION} + 1)).${env.PATCH_VERSION}"
                }
            }
        }
        
        stage('Docker login, build & publish') {
            steps {
                sh "docker build -t 20001017/art_gallery-img:${env.IMAGE_TAG} ."
                sh "docker login docker.io -u 20001017 -p ${DOCKER_PASSWORD}"
                sh "docker push 20001017/art_gallery-img:${env.IMAGE_TAG}"
                sh "git tag ${env.IMAGE_TAG}"
                sh "git push https://$GITHUB_TOKEN@github.com/ProdEng-CodeBusters/service.git ${env.IMAGE_TAG}"
            }
        }
        
        stage ('Deploy and test') {
          steps {
            sh "IMAGE_TAG=${env.IMAGE_TAG} docker-compose up -d hello"
            sh "docker pull 20001017/art_gallery-img:${env.IMAGE_TAG}"
          }
        }
        
        stage('Deploy to Kubernetes') {
	      steps {
        	sh "kubectl apply -f kubernetes/hello.yaml"
		sh "kubectl apply -f kubernetes/mongo.yaml"	
		sh "kubectl expose deployment hello --name art_gallery --port 8080 --target-port=8080 --type=LoadBalancer"
	      }
	}
    }
}
