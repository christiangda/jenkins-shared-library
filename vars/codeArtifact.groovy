#!/usr/bin/env groovy

import groovy.text.GStringTemplateEngine
import com.slashdevops.cicd.Configuration

List<String> getRepositories() {
  Configuration.REPOSITORIES.keySet() as String[]
}

String getDomain(String repoName) {
  if (Configuration.REPOSITORIES.containsKey(repoName)) {
    return Configuration.REPOSITORIES[repoName].domain
  } else {
    return ''
  }
}

String getOwner(String repoName) {
  if (Configuration.REPOSITORIES.containsKey(repoName)) {
    return Configuration.REPOSITORIES[repoName].owner
  } else {
    return ''
  }
}

String getRegion(String repoName) {
  if (Configuration.REPOSITORIES.containsKey(repoName)) {
    return Configuration.REPOSITORIES[repoName].region
  } else {
    return ''
  }
}

String getToken(String domain, String owner, String region='', Integer duration=900) {

  List<String> options = []
  options += "--domain ${domain}"
  options += "--domain-owner ${owner}"
  options += "--duration-seconds ${duration}"
  options += "--query authorizationToken"
  options += "--output text"

  if ((region != '')) {
    options += "--region ${region}"
  }

  optionsString = options.join(" ")

  // this is used to mask any critical information
  wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: domain], [password: owner]]]) {
    return sh(
      returnStdout: true,
      script: """
        aws codeartifact get-authorization-token ${optionsString}
      """).trim()
  }
}

String login(String tool, String repository, String namespace='', Integer duration=900) {

  domain = getDomain(repository)
  owner = getOwner(repository)
  region = getRegion(repository)

  List<String> options = []
  options += "--tool ${tool}"
  options += "--repository ${repository}"
  options += "--domain ${domain}"
  options += "--domain-owner ${owner}"
  options += "--duration-seconds ${duration}"

  if ((region != '')) {
    options += "--region ${region}"
  }

  if ((namespace != '')) {
    options += "--namespace ${namespace}"
  }

  optionsString = options.join(" ")
  return sh(
    returnStdout: true,
    script: """
      aws codeartifact login ${optionsString}
      """).trim()
}

void setupNpmrc(String repository, String namespace='', Integer duration=900, String fileName='.npmrc') {

  domain = getDomain(repository)
  owner = getOwner(repository)
  region = getRegion(repository)

  String token = getToken(domain, owner, region, duration)

  if ((namespace != '')) {
    namespace = "${namespace}:" // add : as suffix
  }

  nmpsrcTemplate = """\
${namespace}registry=https://${domain}-${owner}.d.codeartifact.${region}.amazonaws.com/npm/${repository}/
//${domain}-${owner}.d.codeartifact.${region}.amazonaws.com/npm/${repository}/:always-auth=true
//${domain}-${owner}.d.codeartifact.${region}.amazonaws.com/npm/${repository}/:_authToken=${token}
  """

  Map binding = [
    domain     : domain,
    owner      : owner,
    region     : region,
    repository : repository,
    namespace  : namespace,
    token      : token
  ]

  content = new GStringTemplateEngine().createTemplate(nmpsrcTemplate).make(binding).toString()
  String contentRead = ''

  if (fileExists(fileName)) {
    contentRead += readFile(file: fileName)
  }

  contentWrite = contentRead + "\n" + content

  writeFile(file: fileName, text: contentWrite)
}
