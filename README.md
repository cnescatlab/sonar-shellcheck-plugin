# sonar-shellcheck - A plugin for SonarQube
[![Build Status](https://travis-ci.org/lequal/sonar-shellcheck-plugin.svg?branch=master)](https://travis-ci.org/lequal/sonar-shellcheck-plugin)
![SonarQube Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonarqube.plugins%3Asonarshellcheck&metric=alert_status)
![SonarQube Bugs](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonarqube.plugins%3Asonarshellcheck&metric=bugs)
![SonarQube Coverage](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonarqube.plugins%3Asonarshellcheck&metric=coverage)
![SonarQube Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonarqube.plugins%3Asonarshellcheck&metric=sqale_index)

SonarQube plugin for the code analysis tool: ShellCheck.

SonarQube is an open platform to manage code quality. This plugin add the ability to auto-launch ShellCheck on analysis and gather results from analysis.

This plugin is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

You can get ShellCheck on GitHub: [koalaman/shellcheck](https://github.com/koalaman/shellcheck).

### Quickstart
- Setup a SonarQube instance
- Install ShellCheck
  - Centos : ̀ yum install shellcheck`
  - Debian : `apt install shellcheck`
  - Otherwise, make an alias to the shellcheck executer named "shellcheck"
- Install sonar shellcheck plugin by putting it into the ̀sonarqube/extensions/plugins` folder of SonarQube.
- On Sonarqube, in the menu "Administration > Shellcheck" enable autolaunch
- Run an analysis

### How to contribute
If you experienced a problem with the plugin please open an issue. Inside this issue please explain us how to reproduce this issue and paste the log. 

If you want to do a PR, please put inside of it the reason of this pull request. If this pull request fix an issue please insert the number of the issue or explain inside of the PR how to reproduce this issue.

### Feedback and Support
Contact : L-lequal@cnes.fr

Bugs and Feature requests: https://github.com/lequal/sonar-shellcheck-plugin/issues

### License
Licensed under the [GNU General Public License, Version 3.0](https://www.gnu.org/licenses/gpl.txt)
