# Contributing

When contributing to this repository please discuss the change you wish to make first by opening
a [Github issue](https://github.com/jenkinsci/artifact-repository-parameter-plugin/issues) before 
making a change. We prefer to use Github issues over [Jenkins Jira](https://issues.jenkins-ci.org) 
whenever possible.

## Pull Request Process

1. Fork from [https://github.com/jenkinsci/artifact-repository-parameter-plugin](https://github.com/jenkinsci/artifact-repository-parameter-plugin)
   and apply your changes.
1. Ensure the project is building without any exceptions.
1. If you add unit tests make sure those unit tests run successfully.
1. If you add dependent plugins to `pom.xml` make sure to use the right version in accordance with 
   the Jenkins version (see property `<jenkins.version>` in `pom.xml`).
1. Try to avoid backwards incompatible changes. If the code of a PR contains such code without
   previous discussion and approval the PR will be rejected.
1. Make sure the code is formatted in accordance with the coding styleguide mentioned below.
1. If all requirements are met open a PR so we can have a look at it.

## Code Styleguide

We use the [Google style guide for Java](https://google.github.io/styleguide/javaguide.html) 
version 1.7. A Git pre-push hook gets installed automatically through the Maven build to format 
code before each push. This process allows developers to use whatever formatting rules they prefer 
for local development while ensuring a unified code formatting on remote origin.

For developers using IntelliJ IDEA we recommend the use of [google-java-format](https://plugins.jetbrains.com/plugin/8527-google-java-format)
for this project. Furthermore it is also possible to apply the formatting by running 
`mvn spotless:apply`.

## Versioning

We try to follow [semantic versioning](https://semver.org/) whenever applicable.

## Build Plugin

For local development one can simply run the following command.
```
mvn clean install && \
  MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=n" \
  mvn hpi:run -Djetty.port=8888 
```

This will start a local Jenkins instance with all required plugins at 
[http://localhost:8888/jenkins](http://localhost:8888/jenkins).

> This command will create a `work/` folder inside the project's folder and install
> Jenkins in there. The plugins are located in `work/plugins/`. Changes can survive
> instance restarts.
