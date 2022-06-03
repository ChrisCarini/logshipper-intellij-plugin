#!/usr/bin/env bash

#!/usr/bin/env bash

# Borrowed bash subcommand template from: https://gist.github.com/waylan/4080362

prog_name=$(basename $0)

sub_help() {
  echo "Usage: $prog_name <subcommand> [options]\n"
  echo "Subcommands:"
  echo "    init      Start the ELK Stack & add sample Logshipper dashboard"
  echo ""
  echo "    start     Start the ELK Stack"
  echo "    stop      Stop the ELK Stack"
  echo "    status    Status of ELK Stack"
  echo "    restart   Restart the ELK Stack"
  echo ""
  echo "    purge     Stop & delete all containers associated with Logshipper"
  echo ""
  echo "Note: This script assumes you have 'docker-compose' installed."
  echo ""
}

access_info() {
  echo "#################################################"
  echo "##                                             ##"
  echo "##  Access Kibana via: http://localhost:5601/  ##"
  echo "##                                             ##"
  echo "##    Username: elastic                        ##"
  echo "##    Password: changeme                       ##"
  echo "##                                             ##"
  echo "#################################################"
}

sub_init() {
  pushd ./elk >/dev/null || exit

  # Bring up the containers
  docker-compose up -d --force-recreate --renew-anon-volumes

  # Test for Kibana being ready
  function test_kibana_status() {
    docker exec -it elk-kibana-1 /bin/bash -c "curl localhost:5601/api/status -u 'elastic:changeme'" 2>&1 | jq .status.overall.summary 2>&1
  }
  until test_kibana_status | grep -q -m 1 "All services are available"; do
    printf "[$(date +"%Y-%m-%dT%H:%M:%S%z")] Waiting for Kibana to be available...\r"
    sleep 1
  done
  echo 'Kibana ready!'

  # Add a sample dashboard into Kibana
  echo "Adding dashboard for 'Logshipper Telemetry PoC'..."
  DASHBOARD_FILE=./kibana/Logshipper\ Telemetry\ PoC\ Dashboard.ndjson
  cat "${DASHBOARD_FILE}" | jq -c >"${DASHBOARD_FILE}.TMP" && mv "${DASHBOARD_FILE}.TMP" "${DASHBOARD_FILE}"
  docker-compose exec kibana /bin/bash -c "curl -X POST localhost:5601/api/saved_objects/_import?createNewCopies=true \
        -H 'kbn-xsrf: true' \
        --form file=@/usr/share/kibana/dash.ndjson \
        -u 'elastic:changeme'
    "
  git restore "${DASHBOARD_FILE}"
  echo 'Dashboard added!'

  popd >/dev/null || exit

  sub_status
}

sub_start() {
  pushd ./elk >/dev/null || exit
  docker-compose up -d --force-recreate --renew-anon-volumes
  popd >/dev/null || exit

  sub_status
}

sub_stop() {
  pushd ./elk >/dev/null || exit
  docker-compose stop
  popd >/dev/null || exit
}

sub_status() {
  pushd ./elk >/dev/null || exit
  docker-compose ps
  popd >/dev/null || exit

  access_info
}

sub_restart() {
  sub_stop
  sub_start
}

sub_purge() {
  pushd ./elk >/dev/null || exit
  docker-compose rm -s -v -f
  docker-compose down -v
  docker volume prune -f
  popd >/dev/null || exit
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
