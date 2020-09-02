#!/usr/bin/env bash

#!/usr/bin/env bash

# Borrowed bash subcommand template from: https://gist.github.com/waylan/4080362

prog_name=$(basename $0)

STACK_NAME=elk

sub_help(){
    echo "Usage: $prog_name <subcommand> [options]\n"
    echo "Subcommands:"
    echo "    start     Start the ELK Stack"
    echo "    stop      Stop the ELK Stack"
    echo "    status    Status of ELK Stack"
    echo "    restart   Restart the ELK Stack"
    echo ""
    echo "Note: This script assumes you have 'docker-compose' installed."
    echo ""
}

sub_start(){
    pushd ./elk
    docker-compose up -d
    popd

    sub_status
}

sub_stop(){
    pushd ./elk
    docker-compose stop
    popd
}

sub_status(){
    pushd ./elk
    docker-compose ps
    popd

    echo "#################################################"
    echo "##                                             ##"
    echo "##  Access Kibana via: http://localhost:5601/  ##"
    echo "##                                             ##"
    echo "##    Username: elastic                        ##"
    echo "##    Password: changeme                       ##"
    echo "##                                             ##"
    echo "#################################################"
}

sub_restart(){
    sub_stop
    sub_start
}

subcommand=$1
case $subcommand in
    "" | "-h" | "--help")
        sub_help
        ;;
    *)
        shift
        sub_${subcommand} $@
        if [[ $? = 127 ]]; then
            echo "Error: '$subcommand' is not a known subcommand." >&2
            echo "       Run '$prog_name --help' for a list of known subcommands." >&2
            exit 1
        fi
        ;;
esac
