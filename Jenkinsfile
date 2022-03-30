pipeline {
    agent any

    stages {
        stage('Build & Test') {
            steps {
                sh './gradlew clean build'
            }
        }
        
        stage('Docker login') {
            steps {
                sh "docker login -u 20001017 -p parola@docker"
            }
        }
        
        stage('Tag image') {
            steps {
                script {
                    GIT_TAG = sh([script: 'git fetch --tag && git tag', returnStdout: true]).trim()
                    MAJOR_VERSION = sh([script: 'git tag | cut -d . -f 1', returnStdout: true]).trim()
                    MINOR_VERSION = sh([script: 'git tag | cut -d . -f 2', returnStdout: true]).trim()
                    PATCH_VERSION = sh([script: 'git tag | cut -d . -f 3', returnStdout: true]).trim()
                }
                sh "docker build -t 20001017/art_gallery-img:${MAJOR_VERSION}.\$((${MINOR_VERSION} + 1)).${PATCH_VERSION} ."
                
                sh "docker push 20001017/art_gallery-img:$IMAGE_VERSION"
            }
        }
    }
}
