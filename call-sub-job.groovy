node ('shared') {
    
    /* Call a job with no parameters */
    
    build 'freestyle-job'
    
    
    /* Call a job with a single parameter 
     * and get a handle to the Job's output 
     */
    
    def job = build job:'freestyle-job-with-parameter', parameters: [[$class: 'StringParameterValue', name:'param1', value: 'val1',]]

    
    /* The return from `build` is a handle to org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper
     * RunWrapper exposes various whitelisted attributes of a Run
     * If more details are required the Run object can be retrieved via getRawBuild()
     * See http://javadoc.jenkins-ci.org/index.html?hudson/model/Run.html
     * This will require Script Approval to be given
     */
    echo "Dumping properties for $job"

    echo "DisplayName: ${job.getDisplayName()}"
    echo "result: ${job.getResult()}"
    echo "buildId: ${job.getNumber()}"
    echo "timeInMillis: ${job.getTimeInMillis()}"
    
    def rawBuild=job.getRawBuild()

    echo "buildUrl: ${rawBuild.getUrl()}"
    echo "projectUrl: ${rawBuild.getParent().getUrl()}"

    echo "========= dumping sub-job log============"
    echo rawBuild.getLog()
    echo "========= end dumping sub-job log============"


}