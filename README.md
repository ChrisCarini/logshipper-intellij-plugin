# Logshipper

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
For ease, this repo also includes the necessary `docker-compose` files to setup the ELK stack. Use the `elk_stack.sh` 
bash script to start / stop / restart / status ELK.

It assumes you already have `docker-compose` installed on your machine.
#### `elk_stack.sh` man page
```bash
Usage: ./elk_stack.sh <subcommand> [options]

Subcommands:
    start     Start the ELK Stack
    stop      Stop the ELK Stack
    status    Status of ELK Stack
    restart   Restart the ELK Stack

Note: This script assumes you have 'docker-compose' installed.
```