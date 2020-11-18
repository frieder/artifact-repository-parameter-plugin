# Artifact Repository Parameter Plugin

[![Build Status](https://ci.jenkins.io/job/Plugins/job/artifact-repository-parameter-plugin/job/master/badge/icon)](https://ci.jenkins.io/blue/organizations/jenkins/Plugins%2Fartifact-repository-parameter-plugin/activity)
[![Open Issues](https://img.shields.io/github/issues-raw/jenkinsci/artifact-repository-parameter-plugin)](https://github.com/jenkinsci/artifact-repository-parameter-plugin/issues)
[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/artifact-repository-parameter.svg?label=latest%20version)](https://plugins.jenkins.io/artifact-repository-parameter)
[![Contributors](https://img.shields.io/github/contributors/jenkinsci/artifact-repository-parameter-plugin.svg)](https://github.com/jenkinsci/artifact-repository-parameter-plugin/graphs/contributors)

The goal of the plugin is to make certain information of an artifact repository available as a
[Jenkins](https://www.jenkins.io) build parameter to pipeline builds. The following endpoints are 
supported.

* __Path__ - Display all deployed artifacts.
* __Version__ - Display all available versions of an artifact.
* __Repositories__ - A list of all available repositories.

The following artifact repositories have been tested during development.

* __Sonatype Nexus 3 OSS__
* __JFrog Artifactory 6 Pro__
* __JFrog Artifactory 7 OSS__

## Configuration

Detailed instructions how to configure the plugin can be found in [docs/config.md](docs/config.md).

## Contributing

If you want to contribute to this project please have a look at [CONTRIBUTING.md](CONTRIBUTING.md)
first.

## Known Limitations

An overview of known limitations of this plugin can be found in [docs/limitations.md](docs/limitations.md).

## 3rd Party Code Recognition

This project uses code from 3rd parties. Refer to [COPYING.md](COPYING.md) for detailed information.
