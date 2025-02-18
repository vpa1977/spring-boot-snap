#!/bin/sh

# Run a command in elevated mode (sudo or pkexec, whichever is available)
run_elevated() {
    shift  # Remove the first argument to keep only the command
    local command_to_execute=("$@")

    # Check if the script is running as root
    if [[ "$EUID" -eq 0 ]]; then
        "${command_to_execute[@]}"
    else
        sudo "${command_to_execute[@]}"
    fi

    return $?
}

# Main script execution
if [[ ! -e "/snap/devpack-for-spring-manifest/current/supported.yaml" ]]; then
    echo "Welcome to Devpack for Spring® on Snap!"
    echo "Looks like this is your first time running it, hold tight while I check available content."
    if ! run_elevated "0" snap install devpack-for-spring-manifest; then
        echo "We could not install the manifest content snap, please check your credentials or run this command with sudo."
        exit 255
    fi
fi
$SNAP/usr/bin/java -jar $SNAP/install/configure-1.0-shaded.jar $*
