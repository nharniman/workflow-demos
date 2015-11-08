import hudson.model.*

// Application being triggered is supplied in the variable: app

// We need to check how this script has been called.
// app with a value of "-" means it has been re-invoked by itself, and there are no new deployments
//
stage "Update Deployments"
if ( app=='-' ) {
    log "Checking update",  "no new apps to deploy"
} else {
    log "Checking update",  """$app has changed and needs to be re-deployed
deploying Revision: $revision"""
    if (revision=="") {
        error 'No revision specified'
    } else {
        decom(app, revision)
        deploy (app, revision)
    }
}

stage "Perform NFT"
performNFT()

stage "Check queue and re-trigger"
triggerRun()


//
// function library
//

def decom(app, revision) {
    node ("$app-deploy-runner") {
        log ("Decomission", """Perform the decomission steps here for app: $app
eg call sh /scripts/$app/decom nft""")
        sleep time: 1, unit: 'MINUTES'
    }
}

def deploy(app, revision) {
    node ("$app-deploy-runner") {
        log ("Deploy", """Perform the deploy steps here for app: $app:$revision
eg call sh /scripts/$app/deploy nft $revision""")
        sleep time: 1, unit: 'MINUTES'
    }
}

def performNFT() {
    node ("nft-runner") {
        log ("Run NFT",  "Perform the NFT steps")
        sleep time: 1, unit: 'MINUTES'
    }
}

def triggerRun() {
    // Need to see if there has been another build triggered
    // If not, we need to fire a new async build
    if (getBlockedBuilds(env.JOB_NAME).size().equals(0) ){
        log "Trigger build", "Triggering a new build"
        build job: env.JOB_NAME, parameters: [
            [$class: 'StringParameterValue', name: 'app', value: '-']
        ], propagate: false, wait: false
    } else {
        log "Trigger build", "Build already queued, skipping"
    }
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


def log (step, msg) {

    echo """************************************************************
Step: $step
$msg
************************************************************"""
}