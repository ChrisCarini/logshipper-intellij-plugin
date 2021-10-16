<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# IntelliJ Platform Plugin Template Changelog

## [Unreleased]
### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

## [2.5.0] - 2021-10-15
### Added
- Restructured file to extract all variables into file.
- Adding ability to publish to different channels based on SemVer pre-release labels.
- Adding [JetBrains Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html)
- Update Dependabot to include Gradle dependencies.
- Adding GitHub build & release workflows.
- Adding JetBrains Qodana (experimental, testing only)

### Changed
- Upgrading Gradle to 6.6
- Upgrading IntelliJ to 2021.2.2
- Upgrading IntelliJ Gradle plugin to 1.2.0

### Removed
- Remove `description` from `plugin.xml` _(value is taken from `README.md` as part of Gradle `patchPluginXml` task)_

## [2.4.0] - 2021-04-10
### Changed
- Upgrading to 2021.1
- Upgrading IntelliJ Gradle plugin to 0.7.2

## [2.3.0] - 2020-12-04
### Changed
- Upgrading to 2020.3
- Upgrading IntelliJ Gradle plugin to 0.6.5
- Upgrading Java 11 - see [the JetBrains Platform blog post announcing the migration](https://blog.jetbrains.com/platform/2020/09/intellij-project-migrates-to-java-11/)

## [2.2.2] - 2020-11-28
### Fixed
- <a href="https://github.com/ChrisCarini/logshipper-intellij-plugin/issues/1">#1 - Better handle socket appender when application closing.</a>

## [2.2.1] - 2020-09-28
### Added
- More detailed information to the shipped logs.

### Fixed
- Issue where network disconnection would cause all future log messages to not be shipped.

## [2.2.0] - 2020-09-01
### Changed
- Upgrading to 2020.2
- Upgrading IntelliJ Gradle plugin to 0.4.21

## [2.1.0] - 2020-04-11
### Added
- GitHub Workflow Action for <a href="https://github.com/marketplace/actions/intellij-platform-plugin-verifier">IntelliJ Platform Plugin Verifier</a>

### Changed
- Upgrading to 2020.1
- Upgrading IntelliJ Gradle plugin to 0.4.16
- Upgrading Gradle to 6.2

## [1.0.1] - 2018-10-02
### Changed
- Changed name from `logshipper-intellij-plugin` to `Logshipper` for approval on JetBrains plugin repo.

## [1.0.0] - 2018-10-01
### Added
- Initial release.