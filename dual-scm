node ("linux") {
   checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '88b98c97-7324-4c92-be11-b9fab79e7d46', url: 'https://github.com/harniman/spring-petclinic']]])
   sh "ls -l"
   stage 'Stage 1'
   echo 'Hello World 1'
   stage 'Stage 2'
   echo 'Hello World 2'
}
