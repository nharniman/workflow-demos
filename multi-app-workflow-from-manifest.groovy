// This script reads the manifest from an upstream job
// it compares with the last successful manifest from this build
// based on the differences, it triggers deploys in parallel
// On completion of the deploys it runs NFT
// On completion of NFT it triggers a new run
// Note multiple triggers with the same parameters are coalesced

sleepDuration=15
manifestLocation="add-build-to-merged-manifest"

node ("linux") {

    stage name: 'Reading Manifest'
    step([$class: 'CopyArtifact', filter: 'manifest', projectName: manifestLocation, selector: [$class: 'StatusBuildSelector', stable: false]])
    sh "mv manifest targetmanifest"
    requiredVersions = readPropertiesFromFile("targetmanifest")

    try {
        step([$class: 'CopyArtifact', filter: 'manifest', projectName:env.JOB_NAME, selector: [$class: 'StatusBuildSelector', stable: false]])
        sh "mv manifest currentmanifest"
        currentVersions = readPropertiesFromFile("currentmanifest")
    } catch (Exception e) {
        echo e.toString()
        currentVersions = new Properties()
    }

    stage name: 'Determining Updated Apps'

    updatedVersions = compareVersions( requiredVersions, currentVersions)

    appsToUpdate=updatedVersions.stringPropertyNames().toArray()

    stage name: 'Updating Apps'
    checkpoint 'Starting App Update'

    if (appsToUpdate.size()>0) {
        log "Update Apps", "The following apps require updating: ${appsToUpdate.toString()}"

        def branches = [:]
        for (i=0; i < appsToUpdate.size(); i++) {
            def app=appsToUpdate[i]
            def revision = updatedVersions.getProperty(app)
            branches[app] = {
                decom(app, revision)
                deploy (app, revision)
            }
        }
        parallel branches
    }
    writePropertiesFile(requiredVersions, "manifest")
    archive 'manifest'
    writePropertiesFile(updatedVersions, "updates")
    archive 'updates'

}

stage concurrency: 1, name: 'Perform NFT'
checkpoint 'Starting NFT'
performNFT()

stage "Check queue and re-trigger"
triggerRun()



// ##################################################################################
//
//   Functions
//
// ##################################################################################


def compareVersions ( requiredVersions, currentVersions) {

    currentapps = currentVersions.stringPropertyNames().toArray()
    reqapps = requiredVersions.stringPropertyNames().toArray()
    Properties updatedVersions = new Properties()

    for (i=0; i < reqapps.size(); i++) {

        def app=reqapps[i]

        if (currentVersions.getProperty(app) == requiredVersions.getProperty(app) ) {
            log "Calculating Deltas", "Correct version of $app already deployed"
        } else {
            log "Calculating Deltas", "Adding $app for deployment"
            updatedVersions.setProperty(app, requiredVersions.getProperty(app))
        }
    }

    return updatedVersions
}


def decom(app, revision) {
    node ("$app-deploy-runner") {
        log ("Decomission", """Perform the decomission steps here for app: $app
eg call sh /scripts/$app/decom nft""")
        sleep time: sleepDuration
    }
}

def deploy(app, revision) {
    node ("$app-deploy-runner") {
        log ("Deploy", """Perform the deploy steps here for app: $app:$revision
eg call sh /scripts/$app/deploy nft $revision""")
        sleep time: sleepDuration
    }
}

def performNFT() {
    node ("nft-runner") {
        log ("Run NFT",  "Perform the NFT steps")
        sleep time: sleepDuration
    }
}

def triggerRun() {
    log "Trigger build", "Triggering a new build"
    build job: env.JOB_NAME, propagate: false, wait: false
}

def getBlockedBuilds(fullName) {
    def q = Jenkins.instance.queue
    items = q.items
    Items[] matches = []
    for (hudson.model.Queue.Item item : items) {
        if (item.task.fullName==env.JOB_NAME) {
            log "Matched item", "matched item $item"
            matches.add( item)
        }
    }
    return matches
}

def writePropertiesFile(props, file) {
    log "WriteProperties", "File = $file"
    writeFile file: file, text: writeProperties(props)
}

@NonCPS def writeProperties (props) {
    def sw = new StringWriter()
    props.store(sw, null)
    return sw.toString()
}

def readPropertiesFromFile (file) {
    log "ReadProperties", "File = $file"
    def str = readFile file: file, charset : 'utf-8'
    def sr = new StringReader(str)
    def props = new Properties()
    props.load(sr)
    return props
}

def log (step, msg) {

    echo """************************************************************
Step: $step
$msg
************************************************************"""
}