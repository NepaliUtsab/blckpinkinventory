#!/bin/bash

# Script to launch the Black and Pink Inventory Management Application
# This script should be placed in the project root directory

# Determine the script directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "====================================================="
echo "  Black and Pink Inventory Management Application"
echo "====================================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 17 or higher"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | grep -i version | cut -d'"' -f2)
echo "Found Java version: $JAVA_VERSION"

# Check if the DMG file exists
DMG_FILE="$SCRIPT_DIR/build/compose/binaries/main/dmg/BlackAndPink-1.0.0.dmg"
if [ -f "$DMG_FILE" ]; then
    echo "DMG installer found at: $DMG_FILE"
    echo "For a better experience, install the application from the DMG."
    echo "Or you can continue with the development version."
    read -p "Open DMG installer? (y/n): " open_dmg
    if [[ "$open_dmg" == "y" || "$open_dmg" == "Y" ]]; then
        open "$DMG_FILE"
        exit 0
    fi
fi

# Run the application
echo "Starting Black and Pink Inventory Management..."
cd "$SCRIPT_DIR" && ./gradlew run
