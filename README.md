# Artifact Repository Parameter Plugin

The goal of the plugin is to make certain information of an artifact repository available as
[Jenkins](https://www.jenkins.io) build parameter. Currently the following endpoints are supported.

* __Path__ - Display all deployed artifacts.
* __Version__ - Display all available versions of an artifact.
* __Repositories__ - A list of all available repositories.

The following artifact repositories were tested during development.

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
