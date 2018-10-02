# logshipper-intellij-plugin

## Description
A JetBrains IntelliJ IDEA plugin to ship logs to a remote logstash service.

This plugin adds a custom `log4j` `SocketAppender` to the root `Logger` of IntelliJ as to ship all logs to logstash.


## Configuration
Users are able to configure the `logstash`:
* hostname
* port
* reconnect delay

Additionally, users are able to:
* include location information from the `log4j` `LoggingEvent`.
* generate sample log messages for testing / debugging purposes.
    - **Note:** this *may* fill up your log, as the test log messages happen once every 2 seconds.


## Demo
For ease, this repo also includes a simple bash script to setup the ELK stack on an OSX machine. Use the `elk_stack.osx.sh` 
bash script to install / start / stop / restart / upgrade / uninstall ELK.

It assumes you already have `brew` installed on your OSX machine. See [brew.sh](https://brew.sh/) for installation details on `brew`.
#### `elk_stack.osx.sh` man page
```bash
Usage: ./elk_stack.osx.sh <subcommand> [options]

Subcommands:
    install   Install Elasticsearch, LogStash & Kibana, and setup basic configuration
    upgrade   Upgrade Elasticsearch, LogStash & Kibana
    uninstall Uninstall Elasticsearch, LogStash & Kibana
    start     Start the ELK Stack
    stop      Stop the ELK Stack
    restart   Restart the ELK Stack

Note: This script is for OSX only and assumes you already have 'brew' installed.
```