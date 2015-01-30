node('linux') {
    // COMPILE AND JUNIT
    echo "INFO - Starting build phase"
    def src = 'https://github.com/harniman/spring-petclinic.git'
    // def src = '/Users/nharniman/git/harniman/spring-petclinic'
    git url: src

    ensureMaven()
    sh 'mvn -o clean package'
    sh 'tar -c -f src.tar src/ pom.xml'
    archive 'src.tar, target/petclinic.war'
    step $class: 'hudson.tasks.junit.JUnitResultArchiver', testResults: 'target/surefire-reports/*.xml'
    echo "INFO - Ending build phase"
}

// FUNCTIONS


/**
 * Deploy Maven on the slave if needed and add it to the path
 */
def ensureMaven() {
    env.PATH = "${tool 'Maven 3.x'}/bin:${env.PATH}"
}

