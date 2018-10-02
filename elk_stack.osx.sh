#!/usr/bin/env bash

# Borrowed bash subcommand template from: https://gist.github.com/waylan/4080362

prog_name=$(basename $0)

elk_services=( elasticsearch logstash kibana )

sub_help(){
    echo "Usage: $prog_name <subcommand> [options]\n"
    echo "Subcommands:"
    echo "    install   Install Elasticsearch, LogStash & Kibana, and setup basic configuration"
    echo "    upgrade   Upgrade Elasticsearch, LogStash & Kibana"
    echo "    uninstall Uninstall Elasticsearch, LogStash & Kibana"
    echo "    start     Start the ELK Stack"
    echo "    stop      Stop the ELK Stack"
    echo "    restart   Restart the ELK Stack"
    echo ""
    echo "Note: This script is for OSX only and assumes you already have 'brew' installed."
    echo ""
}

sub_install(){
    for service in "${elk_services[@]}"
    do
        echo "Installing $service..."
        brew install ${service} && brew info ${service}
    done

    ##
    # Configure Kibana to point at Elasticsearch
    ##
    # Uncomment 'server.port: 5601'
    sed -i.bak '/#server.port: 5601/s/^#//g' /usr/local/etc/kibana/kibana.yml
    # Uncomment 'elasticsearch.url: "http://localhost:9200"'
    sed -i.bak '/#elasticsearch.url\: \"http\:\/\/localhost\:9200\"/s/^#//g' /usr/local/etc/kibana/kibana.yml
    rm /usr/local/etc/kibana/kibana.yml.bak

    ##
    # Configure LogStash to receive TCP log messages
    # https://www.elastic.co/guide/en/logstash/current/plugins-inputs-tcp.html
    ##
    cat << EOL | sudo tee /etc/logstash/conf.d/logshipper.conf
input {
  tcp {
    port => 5000
    codec => json
  }
}
output {
  elasticsearch {
    hosts => ["127.0.0.1:9200"]
    index => "logshipper-demo"
  }
  stdout { codec => rubydebug }
}
EOL
}

sub_upgrade(){
    for service in "${elk_services[@]}"
    do
        echo "Upgrading $service..."
        brew upgrade ${service}
    done
}

sub_uninstall(){
    echo "Removing configuration files..."
    rm /usr/local/etc/kibana/kibana.yml
    sudo rm /etc/logstash/conf.d/logshipper.conf

    for service in "${elk_services[@]}"
    do
        echo "Uninstalling $service..."
        brew uninstall ${service}
    done
}

sub_start(){
    for service in "${elk_services[@]}"
    do
        echo "Starting $service..."
        brew services start ${service}
    done
    echo "Currently running services:"
    brew services list
}

sub_stop(){
    for service in "${elk_services[@]}"
    do
        echo "Stopping $service..."
        brew services stop ${service}
    done
}

sub_restart(){
    for service in "${elk_services[@]}"
    do
        echo "Stopping $service..."
        brew services restart ${service}
    done
}

subcommand=$1
case $subcommand in
    "" | "-h" | "--help")
        sub_help
        ;;
    *)
        shift
        sub_${subcommand} $@
        if [ $? = 127 ]; then
            echo "Error: '$subcommand' is not a known subcommand." >&2
            echo "       Run '$prog_name --help' for a list of known subcommands." >&2
            exit 1
        fi
        ;;
esac