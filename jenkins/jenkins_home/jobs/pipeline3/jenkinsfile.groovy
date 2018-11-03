// Name the build out of the parameters
node {
  def buildName = "${instanceUuid}"
  def cause = ""
  def sep = " + "
  if (artifactsChanges == "true") { cause += "artifacts + " }
  if (dataChanges == "true") { cause += "data" + sep }
  if (deploymentChanges == "true") { cause += "deployment" + sep }
  if (creation == "true") { cause += "creation" + sep }
  cause = cause.substring(0, cause.length() - sep.length())
  buildName += ": " + cause
  currentBuild.displayName = buildName
  currentBuild.description = buildName
}

pipeline {
  agent any
  options {
    ansiColor('xterm')
  }
  tools {
    maven 'Maven 3.5.0' 
    nodejs 'NodeJS 8.6.0'
  }
  stages {
    stage ('Pre-host connection preparation') {
      steps {
        sh 'node /opt/node-scripts/src/$JOB_NAME/prehost-preparation.js'
        sh 'cat $BUILD_PATH/prehost-prepare.sh'
        sh '$BUILD_PATH/prehost-prepare.sh'
        sh 'node /opt/node-scripts/src/$JOB_NAME/update-status.js $BUILD_PATH/status.json'
      }
    }
    stage ('Host preparation') {
      steps {
        sh 'node /opt/node-scripts/src/$JOB_NAME/host-preparation.js'
        sh 'cat $BUILD_PATH/host-prepare.sh'
        sh '$BUILD_PATH/host-prepare.sh'
        sh 'node /opt/node-scripts/src/$JOB_NAME/update-status.js $BUILD_PATH/status.json'
      }
    }
    stage ('Maintenance mode: ON') {
      when {
        expression { return false }
      }
      steps {
        sh 'echo "Stage/steps awaiting implementation... Skipping."'
      }
    }
    stage ('Production host preparation') {
      when {
        expression { return false }
      }
      steps {
        sh 'echo "Stage/steps awaiting implementation... Skipping."'
      }
    }
    stage ('Start or restart the instance') {
      steps {
        sh 'node /opt/node-scripts/src/$JOB_NAME/start-instance.js'
        sh 'cat $BUILD_PATH/start-instance.sh'
        sh '$BUILD_PATH/start-instance.sh'
        sh 'node /opt/node-scripts/src/$JOB_NAME/update-status.js $BUILD_PATH/status.json'
      }
    }
    stage ('Instance startup status monitoring') {
      steps {
        sh 'node /opt/node-scripts/src/$JOB_NAME/startup-monitoring.js'
        sh 'cat $BUILD_PATH/startup-monitoring.sh'
        sh '$BUILD_PATH/startup-monitoring.sh'
        sh 'node /opt/node-scripts/src/$JOB_NAME/update-status.js $BUILD_PATH/status.json'
      }
    }
    stage ('Post startup actions') {
      steps {
        sh 'node /opt/node-scripts/src/$JOB_NAME/post-start.js'
        sh 'cat $BUILD_PATH/post-start.sh'
        sh '$BUILD_PATH/post-start.sh'
        sh 'node /opt/node-scripts/src/$JOB_NAME/update-status.js $BUILD_PATH/status.json'
      }
    }
    stage ('Maintenance mode: OFF') {
      when {
        expression { return false }
      }
      steps {
        sh 'echo "Stage/steps awaiting implementation... Skipping."'
      }
    }
  }
}