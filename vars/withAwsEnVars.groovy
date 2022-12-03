#!/usr/bin/env groovy

/*
* paramters:
*   roleArn (required)
*   roleAccount (required)
*   sessionName (optional)
*   sessionDuration (optional)
*
* Examples:
*   stage('test aws credential') {
*     steps {
*       withAwsEnVars(roleName:'cicd-execution-role', roleAccount: '12345678910') {
*         sh "echo TOKEN: ${AWS_SESSION_TOKEN}"
*         sh "echo KEY: ${AWS_SECRET_ACCESS_KEY}"
*         sh "echo ID: ${AWS_ACCESS_KEY_ID}"
*         sh 'aws s3 ls'
*       }
*       sh "exit 1"
*     }
*   }
*/
def call(Map params, Closure body) {

  if (!params.roleName) {
    error """
      parameter 'roleName' is required.
      ---
      Example:  withAwsEnVars(roleName:'cicd-execution-role', roleAccount: '12345678910') {...}
    """
  }

  if (!params.roleAccount) {
    error """
      parameter 'roleAccount' is required.
      ---
      Example:  withAwsEnVars(roleName:'cicd-execution-role', roleAccount: '12345678910') {...}
    """
  }

  // get optional parameters if not set default
  String sessionName = params.get('sessionName', 'jenkins')
  Integer duration = params.get('sessionDuration', 900)

  cred = awsCredentials.getFromAssumeRole(params.roleName, params.roleAccount, sessionName, duration)

  AWS_ACCESS_KEY_ID = cred.AccessKeyId
  AWS_SECRET_ACCESS_KEY = cred.SecretAccessKey
  AWS_SESSION_TOKEN = cred.SessionToken

  wrap([
      $class: 'MaskPasswordsBuildWrapper',
      varPasswordPairs: [
        [password: AWS_ACCESS_KEY_ID],
        [password: AWS_SECRET_ACCESS_KEY],
        [password: AWS_SESSION_TOKEN]
      ]
  ]) {
    withEnv([
      "AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}",
      "AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}",
      "AWS_SESSION_TOKEN=${AWS_SESSION_TOKEN}"
    ]) {
      body()
    }
  }
}