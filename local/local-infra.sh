#!/usr/bin/env bash

readonly script_basename="$(basename "$0")"
readonly script_raw_args=("$@")
script_args=("$@")

function git_root_dir() {
  git rev-parse --show-toplevel
}

repo_root_dir="$(git_root_dir)"
docker_compose_yaml_path="${repo_root_dir}/local/docker-compose.yml"

docker_command=""

readonly DOCKER_COMMAND_UP="DOCKER_COMMAND_UP"
readonly DOCKER_COMMAND_STOP="DOCKER_COMMAND_STOP"
readonly DOCKER_COMMAND_DOWN="DOCKER_COMMAND_DOWN"

# reads script's parameters with "-" or "--"
function read_args() {
    local positional_params=""
    while (("$#")); do
        case "$1" in
        -d | --down)
            docker_command="${DOCKER_COMMAND_DOWN}"
            shift 1
            ;;
        -f | --file)
            IFS=',' read -r -a docker_compose_yaml_path <<< "$2"
            shift 2
            ;;
        -s | --stop)
          docker_command="${DOCKER_COMMAND_STOP}"
          shift 1
          ;;
        -u | --up)
          docker_command="${DOCKER_COMMAND_UP}"
          shift 1
          ;;
        --) # end argument parsing
            shift
            break
            ;;
        -* | --*=) # unsupported params
            echo "Invalid parameter: $1"
            usage
            exit_abnormally
            ;;
        *) # preserve positional params
            positional_params="$positional_params $1"
            shift
            ;;
        esac
    done

    # set positional arguments
    eval set -- "$positional_params"
    script_args=("$@")
}

function up() {
  docker compose -f "${docker_compose_yaml_path}" up -d
}
function stop() {
  docker compose -f "${docker_compose_yaml_path}" stop
}
function down() {
  echo "Are you sure you want to stop and remove the entire local infrastructure including data? (y/N)"
  read answer

  if [ "${answer,,}" = "y" ]; then
    docker compose -f "${docker_compose_yaml_path}" down
  fi
}

function execute_docker_command() {
  if [ "${docker_command}" = "${DOCKER_COMMAND_UP}" ]; then
    up
  elif [ "${docker_command}" = "${DOCKER_COMMAND_STOP}" ]; then
    stop
  elif [ "${docker_command}" = "${DOCKER_COMMAND_DOWN}" ]; then
    down
  else
    echo "Docker Compose command unrecognized: ${docker_command}"
    exit 1
  fi
}

read_args "${script_args[@]}"
execute_docker_command
