<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# IntelliJ Platform Plugin Template Changelog

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

## [5.0.2] - 2025-07-06

### Added

- Added a connection checker service to add/remove logger handler every 5 min, as needed

### Changed

- Upgrading IntelliJ from 2025.1.2 to 2025.1.3

## [5.0.1] - 2025-07-06

### Changed

- Upgrading IntelliJ from 2025.1.1 to 2025.1.2
- Upgrading IntelliJ from 2025.1 to 2025.1.1

## [5.0.0] - 2025-04-17

### Changed

- Upgrading IntelliJ from 2024.3.5 to 2025.1

## [4.2.8] - 2025-03-19

### Changed

- Upgrading IntelliJ from 2024.3.4 to 2024.3.5

## [4.2.7] - 2025-03-01

### Changed

- Upgrading IntelliJ from 2024.3.3 to 2024.3.4

## [4.2.6] - 2025-02-21

### Changed

- Upgrading IntelliJ from 2024.3.2.2 to 2024.3.3

## [4.2.5] - 2025-01-30

### Changed

- Upgrading IntelliJ from 2024.3.2.1 to 2024.3.2.2

## [4.2.4] - 2025-01-25

### Changed

- Upgrading IntelliJ from 2024.3.2 to 2024.3.2.1

## [4.2.3] - 2025-01-19

### Added

- Added more detailed logging for Logshipper log handler is added/removed to/from the root logger.

### Changed

- Upgrading IntelliJ from 2024.3.1.1 to 2024.3.2
- Use `String.format()` instead of `IdeBundle.message()` in logs, since the latter causes recursion leading to `StackOverflowError`.

### Removed

- Remove call to `@ApiStatus.Internal` method `ShutDownTracker.getInstance().registerShutdownTask()`.

## [4.2.2] - 2025-01-02

### Changed

- Upgrading IntelliJ from 2024.3.1 to 2024.3.1.1

## [4.2.1] - 2024-12-10

### Changed

- Upgrading IntelliJ from 2024.3 to 2024.3.1

## [4.2.0] - 2024-11-14

### Changed

- Upgrading IntelliJ from 2024.2.4 to 2024.3.0

## [4.1.4] - 2024-10-24

### Changed

- Upgrading IntelliJ from 2024.2.3 to 2024.2.4

## [4.1.3] - 2024-09-26

### Changed

- Upgrading IntelliJ from 2024.2.2 to 2024.2.3

## [4.1.2] - 2024-09-22

### Changed

- Upgrading IntelliJ from 2024.2.1 to 2024.2.2

## [4.1.1] - 2024-08-31

### Changed

- Upgrading IntelliJ from 2024.2 to 2024.2.1

## [4.1.0] - 2024-08-20

### Changed

- Upgrading IntelliJ from 2024.1.5 to 2024.2.0
- Changed gradle build to Kotlin DSL (from Groovy DSL) in preparation of `IntelliJ Platform Gradle Plugin 2.0.0`.

## [4.0.5] - 2024-08-07

### Changed

- Upgrading IntelliJ from 2024.1.4 to 2024.1.5

## [4.0.4] - 2024-06-22

### Changed

- Upgrading IntelliJ from 2024.1.3 to 2024.1.4

## [4.0.3] - 2024-06-13

### Changed

- Upgrading IntelliJ from 2024.1.2 to 2024.1.3

## [4.0.2] - 2024-05-31

### Changed

- Upgrading IntelliJ from 2024.1.1 to 2024.1.2

## [4.0.1] - 2024-04-30

### Changed

- Upgrading IntelliJ from 2024.1 to 2024.1.1

## [4.0.0] - 2024-04-05

### Changed

- Upgrading IntelliJ from 2023.3.6 to 2024.1.0

## [3.2.6] - 2024-03-22

### Changed

- Upgrading IntelliJ from 2023.3.5 to 2023.3.6

## [3.2.5] - 2024-03-13

### Changed

- Upgrading IntelliJ from 2023.3.4 to 2023.3.5

## [3.2.4] - 2024-02-14

### Changed

- Upgrading IntelliJ from 2023.3.3 to 2023.3.4

## [3.2.3] - 2024-01-30

### Changed

- Upgrading IntelliJ from 2023.3.2 to 2023.3.3

## [3.2.2] - 2023-12-21

### Changed

- Upgrading IntelliJ from 2023.3.1 to 2023.3.2

## [3.2.1] - 2023-12-14

### Changed

- Upgrading IntelliJ from 2023.3 to 2023.3.1

## [3.2.0] - 2023-12-08

### Changed

- Upgrading IntelliJ from 2023.2.5 to 2023.3.0

## [3.1.5] - 2023-11-10

### Changed

- Upgrading IntelliJ from 2023.2.4 to 2023.2.5

## [3.1.4] - 2023-10-28

### Changed

- Upgrading IntelliJ from 2023.2.3 to 2023.2.4

## [3.1.3] - 2023-10-12

### Changed

- Upgrading IntelliJ from 2023.2.2 to 2023.2.3

## [3.1.2] - 2023-09-21

### Changed

- Upgrading IntelliJ from 2023.2.1 to 2023.2.2

## [3.1.1] - 2023-09-06

### Changed

- Upgrading IntelliJ from 2023.2 to 2023.2.1

## [3.1.0] - 2023-07-27

### Changed

- Upgrading IntelliJ from 2023.1.5 to 2023.2.0

## [3.0.5] - 2023-07-26

### Changed

- Upgrading IntelliJ from 2023.1.4 to 2023.1.5

## [3.0.4] - 2023-07-14

### Changed

- Upgrading IntelliJ from 2023.1.3 to 2023.1.4

## [3.0.3] - 2023-06-22

### Changed

- Upgrading IntelliJ from 2023.1.2 to 2023.1.3

## [3.0.2] - 2023-05-17

### Changed

- Upgrading IntelliJ from 2023.1.1 to 2023.1.2

## [3.0.1] - 2023-04-29

### Changed

- Upgrading IntelliJ from 2023.1 to 2023.1.1

## [3.0.0] - 2023-03-29

### Changed

- Upgrading IntelliJ from 2022.3.3 to 2023.1.0

## [2.8.3] - 2023-03-13

### Changed

- Upgrading IntelliJ from 2022.3.2 to 2022.3.3

## [2.8.2] - 2023-02-04

### Changed

- Upgrading IntelliJ from 2022.3.1 to 2022.3.2

## [2.8.1] - 2022-12-28

### Changed

- Upgrading IntelliJ from 2022.3 to 2022.3.1

## [2.8.0] - 2022-12-28

### Changed

- Upgrading IntelliJ from 2022.2.4 to 2022.3.0

## [2.7.1] - 2022-11-28

### Changed

- Upgrading IntelliJ from 2022.2 to 2022.2.4

## [2.7.0] - 2022-07-29

### Changed

- Upgrading IntelliJ to 2022.2

## [2.6.0] - 2022-04-24

### Added

- Changing connection settings will begin shipping logs without needing an IDE restart.
- Plugin Icon <img src="https://raw.githubusercontent.com/ChrisCarini/logshipper-intellij-plugin/master/icon/pluginIcon.svg" width="24" />

### Changed

- Upgrading IntelliJ to 2022.1
- Upgrading demo ELK cluster to 8.0.0

### Removed

- Removing log4j dependency / reliance to align with [the IntelliJ Platform change](https://blog.jetbrains.com/platform/2022/02/removing-log4j-from-the-intellij-platform/).

## [2.5.2] - 2021-12-01

### Changed

- Upgrading IntelliJ to 2021.3

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

[Unreleased]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v5.0.2...HEAD
[5.0.2]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v5.0.1...v5.0.2
[5.0.1]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v5.0.0...v5.0.1
[5.0.0]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.2.8...v5.0.0
[4.2.8]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.2.7...v4.2.8
[4.2.7]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.2.6...v4.2.7
[4.2.6]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.2.5...v4.2.6
[4.2.5]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.2.4...v4.2.5
[4.2.4]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.2.3...v4.2.4
[4.2.3]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.2.2...v4.2.3
[4.2.2]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.2.1...v4.2.2
[4.2.1]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.2.0...v4.2.1
[4.2.0]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.1.4...v4.2.0
[4.1.4]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.1.3...v4.1.4
[4.1.3]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.1.2...v4.1.3
[4.1.2]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.1.1...v4.1.2
[4.1.1]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.1.0...v4.1.1
[4.1.0]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.0.5...v4.1.0
[4.0.5]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.0.4...v4.0.5
[4.0.4]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.0.3...v4.0.4
[4.0.3]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.0.2...v4.0.3
[4.0.2]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.0.1...v4.0.2
[4.0.1]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v4.0.0...v4.0.1
[4.0.0]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.2.6...v4.0.0
[3.2.6]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.2.5...v3.2.6
[3.2.5]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.2.4...v3.2.5
[3.2.4]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.2.3...v3.2.4
[3.2.3]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.2.2...v3.2.3
[3.2.2]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.2.1...v3.2.2
[3.2.1]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.2.0...v3.2.1
[3.2.0]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.1.5...v3.2.0
[3.1.5]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.1.4...v3.1.5
[3.1.4]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.1.3...v3.1.4
[3.1.3]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.1.2...v3.1.3
[3.1.2]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.1.1...v3.1.2
[3.1.1]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.1.0...v3.1.1
[3.1.0]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.0.5...v3.1.0
[3.0.5]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.0.4...v3.0.5
[3.0.4]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.0.3...v3.0.4
[3.0.3]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.0.2...v3.0.3
[3.0.2]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.0.1...v3.0.2
[3.0.1]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v3.0.0...v3.0.1
[3.0.0]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v2.8.3...v3.0.0
[2.8.3]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v2.8.2...v2.8.3
[2.8.2]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v2.8.1...v2.8.2
[2.8.1]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v2.8.0...v2.8.1
[2.8.0]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v2.7.1...v2.8.0
[2.7.1]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v2.7.0...v2.7.1
[2.7.0]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v2.6.0...v2.7.0
[2.6.0]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v2.5.2...v2.6.0
[2.5.2]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v2.5.0...v2.5.2
[2.5.0]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v2.4.0...v2.5.0
[2.4.0]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v2.3.0...v2.4.0
[2.3.0]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v2.2.2...v2.3.0
[2.2.2]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v2.2.1...v2.2.2
[2.2.1]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v2.2.0...v2.2.1
[2.2.0]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v2.1.0...v2.2.0
[2.1.0]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v1.0.1...v2.1.0
[1.0.1]: https://github.com/ChrisCarini/logshipper-intellij-plugin/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/ChrisCarini/logshipper-intellij-plugin/commits/v1.0.0
