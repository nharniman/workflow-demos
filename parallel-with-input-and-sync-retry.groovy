boolean INST=false

node ("linux") {
    parallel comp1: {


        sh "echo 'Doing COMP1 PRE' "

        retry(count: 1440) {
           sh "sleep 60 && $INST"
        }

        sh "echo 'Doing COMP1 INST' "

    }, comp2: {

        sh "echo 'Doing COMP2 PRE' "
        retry(count: 1440) {
           sh "sleep 60 && $INST"
        }

        sh "echo 'Doing COMP2 INST' "

    }, comp3: {

        sh "echo 'Doing COMP3 PRE' "
        retry(count: 1440) {
           sh "sleep 60 && $INST"
        }

        sh "echo 'Doing COMP3 INST' "

    }, comp4: {

        sh "echo 'Doing COMP4 PRE' "

        retry(count: 1440) {
           sh "sleep 60 && $INST"
        }

        sh "echo 'Doing COMP4 INST' "

    }, approval: {

        input 'Proceed with INST?'

        INST=true
        echo "Value of INST = $INST"


        // do something else
    },
    failFast: true
}
