package com.slashdevops.cicd

class Configuration implements Serializable {

  public static final AWS_CICD_ROLE = 'cicd-execution-role'

  // Branch behaviour according to AWS Account
  public static final Map BRANCH_ENVIRONMENT = [
    devel      : [accountId: '<your AWS devel account here>',      region: '<Your AWS Region here>', deploy: true ],
    production : [accountId: '<your AWS production account here>', region: '<Your AWS Region here>', deploy: true ],
    PR         : [accountId: 'None',                               region: 'None',                   deploy: false],
    release    : [accountId: 'None',                               region: 'None',                   deploy: false],
    other      : [accountId: 'None',                               region: 'None',                   deploy: false],
  ]

  // AWS codeArtifact
  public static final Map REPOSITORIES = [
    'maven-artifacts' : [domain: '<your AWS CodeArtifact domain here>', owner: '<your AWS AccountId here>', region: '<Your AWS Region here>'],
    'maven-private'   : [domain: '<your AWS CodeArtifact domain here>', owner: '<your AWS AccountId here>', region: '<Your AWS Region here>'],
    'maven-public'    : [domain: '<your AWS CodeArtifact domain here>', owner: '<your AWS AccountId here>', region: '<Your AWS Region here>'],
    'npm-artifacts'   : [domain: '<your AWS CodeArtifact domain here>', owner: '<your AWS AccountId here>', region: '<Your AWS Region here>'],
    'npm-private'     : [domain: '<your AWS CodeArtifact domain here>', owner: '<your AWS AccountId here>', region: '<Your AWS Region here>'],
    'npm-public'      : [domain: '<your AWS CodeArtifact domain here>', owner: '<your AWS AccountId here>', region: '<Your AWS Region here>'],
    'pip-private'     : [domain: '<your AWS CodeArtifact domain here>', owner: '<your AWS AccountId here>', region: '<Your AWS Region here>'],
    'pip-public'      : [domain: '<your AWS CodeArtifact domain here>', owner: '<your AWS AccountId here>', region: '<Your AWS Region here>'],
  ]
}