#!/usr/bin/env groovy

import hudson.AbortException

void info(message) {
  println "\033[32mINFO: ${message}\033[0m"
}

void warning(message) {
  println "\033[33mWARN: ${message}\033[0m"
}

void debug(message) {
  println "\033[34mDEBUG: ${message}\033[0m"
}

void error(message) {
  throw new AbortException("\033[31m${message}\033[0m")
}

void trace(message) {
  println "\033[35mTRACE: ${message}\033[0m"
}

void fatal(message) {
  throw new AbortException("\033[31m${message}\033[0m")
}