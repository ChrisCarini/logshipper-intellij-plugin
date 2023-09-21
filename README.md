# <img src="./src/main/resources/META-INF/pluginIcon.svg" width="32" /> Logshipper
<!-- Plugin description -->
A plugin for JetBrains IDEs to ship logs to a remote logstash service.
<!-- Plugin description end -->

[![GitHub License](https://img.shields.io/github/license/ChrisCarini/logshipper-intellij-plugin?style=flat-square)](https://github.com/ChrisCarini/logshipper-intellij-plugin/blob/main/LICENSE)
[![JetBrains IntelliJ Plugins](https://img.shields.io/jetbrains/plugin/v/11195-logshipper?label=Latest%20Plugin%20Release&style=flat-square)](https://plugins.jetbrains.com/plugin/11195-logshipper)
[![JetBrains IntelliJ Plugins](https://img.shields.io/jetbrains/plugin/r/rating/11195-logshipper?style=flat-square)](https://plugins.jetbrains.com/plugin/11195-logshipper)
[![JetBrains IntelliJ Plugins](https://img.shields.io/jetbrains/plugin/d/11195-logshipper?style=flat-square)](https://plugins.jetbrains.com/plugin/11195-logshipper)
[![All Contributors](https://img.shields.io/github/all-contributors/ChrisCarini/logshipper-intellij-plugin?color=ee8449&style=flat-square)](#contributors)
[![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/ChrisCarini/logshipper-intellij-plugin/build.yml?branch=main&logo=GitHub&style=flat-square)](https://github.com/ChrisCarini/logshipper-intellij-plugin/actions/workflows/build.yml)
[![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/ChrisCarini/logshipper-intellij-plugin/compatibility.yml?branch=main&label=IntelliJ%20Plugin%20Compatibility&logo=GitHub&style=flat-square)](https://github.com/ChrisCarini/logshipper-intellij-plugin/actions/workflows/compatibility.yml)

This plugin adds a custom `java.util.logging` `Handler` to the root `Logger` of IntelliJ, allowing all logs to be
shipped to a logstash server.

## Configuration
Users are able to configure the `logstash`:
* hostname
* port
* reconnect delay

Additionally, users are able to:
* include location information from the `LogRecord`.
* generate sample log messages for testing / debugging purposes.
    - **Note:** this *may* fill up your log, as the test log messages happen once every 2 seconds.

## Demo
For ease, this repo also includes the necessary `docker-compose` files to setup the ELK stack.

Use the `./elk_stack.sh` bash script to init / start / stop / restart / status / purge ELK stack.

### Quick Start

1. **Start ELK**
    ```shell
    ./elk_stack.sh init
    ```

2. **Launch the IDE** with Logshipper plugin
3. **Configure** Logshipper settings
   1. **Hostname:** `localhost`
   2. **Port:** `5000`
4. **Navigate** to http://localhost:5601 and login with `elastic/changeme`.
5. Find the 'Logshipper Telemetry PoC' dashboard
6. Profit!

#### `elk_stack.sh` man page

```bash
Usage: ./elk_stack.sh <subcommand> [options]

Subcommands:
    init      Start the ELK Stack & add sample Logshipper dashboard

    start     Start the ELK Stack
    stop      Stop the ELK Stack
    status    Status of ELK Stack
    restart   Restart the ELK Stack

    purge     Stop & delete all containers associated with Logshipper

Note: This script assumes you have 'docker-compose' installed.
```

## Contributors

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->