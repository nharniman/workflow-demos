node('linux') { // COMPILE AND JUNIT
    def src = 'https://github.com/harniman/spring-petclinic.git'
    // def src = '/Users/nharniman/git/harniman/spring-petclinic'
    git url: src

    ensureMaven()
    sh 'mvn -o clean package'
}


/**
 * Deploy Maven on the slave if needed and add it to the path
 */
def ensureMaven() {
    env.PATH = "${tool 'Maven 3.x'}/bin:${env.PATH}"
}
