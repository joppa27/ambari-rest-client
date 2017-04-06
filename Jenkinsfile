pipeline {
  agent none
  stages {
    stage('build') {
      steps {
        parallel(
          "build": {
            node(label: 'dockerNode') {
              sh '''echo hello
'''
            }
            
            
          },
          "": {
            node(label: 'dockerNode')
            
          }
        )
      }
    }
  }
}