def INST=false

node ("linux") {
    parallel comp1: {


        sh "echo 'Doing COMP1 PRE' "

        waitUntil { INST==true }

        sh "echo 'Doing COMP1 INST' "



        // do something
    }, comp2: {

        sh "echo 'Doing COMP2 PRE' "
        waitUntil { INST==true }

        sh "echo 'Doing COMP2 INST' "

        // do something else
    }, comp3: {

        sh "echo 'Doing COMP3 PRE' "
        waitUntil { INST==true }

        sh "echo 'Doing COMP3 INST' "

        // do something else
    }, comp4: {

        sh "echo 'Doing COMP4 PRE' "
        waitUntil { INST==true }

        sh "echo 'Doing COMP4 INST' "


        // do something else
    }, approval: {

        input 'Proceed with INST?'

        INST=true
        echo "Value of INST = $INST"


        // do something else
    },
    failFast: true
}