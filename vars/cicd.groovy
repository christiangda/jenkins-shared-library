#!/usr/bin/env groovy

import java.util.regex.Matcher
import com.slashdevops.cicd.Configuration

String getRole() {
  return Configuration.AWS_CICD_ROLE
}

Map getBehaviour(String branchName) {
  if (Configuration.BRANCH_ENVIRONMENT.containsKey(branchName)) {
    return Configuration.BRANCH_ENVIRONMENT[branchName]
  } else {
    return Configuration.BRANCH_ENVIRONMENT['other']
  }
}

String getBranchBehaviourName() {
  String branchName = gitInfo.branchName()
  String[] availableBranches = getBranches()

  // this is necessary to match the pull request branch name with the matrix in Configuration.BRANCH_ENVIRONMENT in Configuration.groovy
  if (branchName =~ /^PR-\d+([a-zA-Z0-9-_]){0,}$/) { // regex check: https://regex101.com/r/vIqmjX/1
    return 'PR'
  } else if (branchName =~ /^release([a-zA-Z0-9-_\/.]){0,}$/) { // regex check: https://regex101.com/r/kaatQS/1
    return 'release'
  } else if(availableBranches.contains(branchName)) {
    return branchName
  } else {
    return 'other'
  }
}

List<String> getBranches() {
  Configuration.BRANCH_ENVIRONMENT.keySet() as String[]
}

Boolean accountId() {
  String branchName = getBranchBehaviourName()

  if (Configuration.BRANCH_ENVIRONMENT.containsKey(branchName)) {
    return Configuration.BRANCH_ENVIRONMENT[branchName].accountId
  } else {
    return 'None'
  }
}

Boolean region() {
  String branchName = getBranchBehaviourName()

  if (Configuration.BRANCH_ENVIRONMENT.containsKey(branchName)) {
    return Configuration.BRANCH_ENVIRONMENT[branchName].region
  } else {
    return 'None'
  }
}


Boolean deploy() {
  String branchName = getBranchBehaviourName()

  if (Configuration.BRANCH_ENVIRONMENT.containsKey(branchName)) {
      return Configuration.BRANCH_ENVIRONMENT[branchName].deploy
  } else {
      return false
  }
}