#!/usr/bin/env groovy

/*
* Parameters:
*   roleName (required)
*   roleAccount (required)
*
* Examples:
*   cred = awsCredentials.getFromAssumeRole(...)
*   accessKey = awsCredentials.getFromAssumeRole(...).AccessKeyId
*/
String getFromAssumeRole(String roleName, String roleAccount, String sessionName='jenkins', Integer duration=900){

  String roleArn = 'arn:aws:iam::' + roleAccount +':role/'+ roleName

  List<String> options = []

  options += "--role-arn ${roleArn}"
  options += "--role-session-name ${sessionName}"
  options += "--duration-seconds ${duration}"
  options += "--query 'Credentials'"

  optionsString = options.join(" ")

  // this is used to mask any critical information
  wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: roleArn], [password: sessionName]]]) {
    String strCreds = sh(
      returnStdout: true,
      script: """
        aws sts assume-role ${optionsString}
        """).trim()

    return readJSON(text: strCreds)
  }
}