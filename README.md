# Inventory Management System

A cross-platform desktop application for inventory management using Kotlin and Compose for Desktop, targeting macOS and Windows.

## Project Structure

```
blackandpink/
├── build.gradle.kts         # Gradle build configuration
├── src/
│   ├── jvmMain/            # Main source code
│   │   ├── kotlin/        # Kotlin code
│   │   │   └── com/
│   │   │       └── blackandpink/  # App's code
│   │   │           ├── components/   # UI components
│   │   │           ├── model/       # Data models
│   │   │           ├── screens/     # Application screens
│   │   │           ├── storage/     # Local storage handling
│   │   │           └── util/        # Utility functions
│   │   └── resources/     # Resources like icons
│   └── jvmTest/           # Test source code
└── gradle/                # Gradle wrapper files
```

## Features

- Complete inventory management system with local storage
- Session-based inventory tracking with opening and closing dates
- Analytics with charts and reporting
- Item management with categories, SKUs, and stock levels
- Transaction history for item movements
- Cross-platform UI with Kotlin and Compose for Desktop
- Dark and light theme support
- Import/Export functionality for data backup and migration
- Improved error handling with proper exception handling
- Unit tests for model classes and storage operations
- Packaging for macOS (.dmg) and Windows (.msi)

## Using the Inventory Management System

### Home Screen
The home screen provides access to the main modules:
- **Inventory**: Manage your items, check stock levels, and make adjustments
- **Sessions**: Create and review inventory counting sessions
- **Analytics**: View reports and charts about your inventory
- **Settings**: Configure application preferences

### Inventory Management
- Add, edit, and delete inventory items
- Each item can have SKU, category, price, cost, quantities, and location
- Set minimum and maximum stock levels for automatic alerts
- Filter items by category and search by name or SKU
- Track item transactions (additions, removals, or adjustments)

### Session Management
- Create new inventory counting sessions with start and end dates
- Sessions capture a snapshot of inventory at specific times
- Record transactions during a session
- Close sessions when inventory counting is complete
- Review past sessions for historical data

### Analytics
- Visual charts showing value by category
- Low stock alerts for items below minimum levels
- Top items by value
- Inventory summary statistics

### Local Storage
All data is stored locally on your machine - no cloud server required!
- Items are stored in a central inventory file
- Each session is stored as a separate file
- All data is saved in JSON format for easy backup or transfer

### Data Backup and Migration
- Export your entire inventory database to a ZIP file for backup
- Import data from a previously exported backup file
- Automatic backup creation before import operations
- All inventory sessions, items, transactions, and analytics are included in backups

## Development

### Prerequisites

- JDK 17 or higher
- Gradle (wrapper included)

### Running the Application

```bash
./gradlew run
```

Or you can use the provided scripts:

#### macOS
```bash
./run.sh
```

#### Windows
```bash
run.bat
```

These scripts check for Java installation and also look for packaged installers.

### Building the Application

```bash
./gradlew build
```

### Running Tests

```bash
./gradlew test
```

This will run all unit tests for the application. Test reports can be found in `build/reports/tests/`.

### Packaging for Distribution

#### macOS

```bash
./gradlew packageDmg
```

This will create a `.dmg` file in the `build/compose/binaries/main/dmg/` directory.

#### Windows

```bash
./gradlew packageMsi
```

This will create a `.msi` file in the `build/compose/binaries/main/msi/` directory.

#### Cross-Platform Windows Installer (Docker)

For creating Windows installers on macOS without needing a Windows machine:

```bash
# Quick automated build
./build-windows-installer.sh --wine

# Using Make
make installer

# Test Docker setup first
./test-docker-setup.sh
```

This creates professional Windows installers (both MSI and EXE) using Docker. See [DOCKER_INSTALLER_GUIDE.md](DOCKER_INSTALLER_GUIDE.md) for detailed instructions.

#### All Platforms

```bash
./gradlew packageDmg packageMsi packageDeb packageRpm
```

## Deployment Options

### Local Development
- Use `./gradlew run` for quick testing
- Package for your platform using the appropriate Gradle tasks

### GitHub Actions (Recommended)
- Automatic builds on every push
- Cross-platform installer generation
- See [GITHUB_SETUP.md](GITHUB_SETUP.md) for setup instructions

### Docker-based Windows Installers
- Build Windows installers on any platform
- No need for Windows VMs or machines
- See [DOCKER_INSTALLER_GUIDE.md](DOCKER_INSTALLER_GUIDE.md) for details

## Icon Files

The application uses two icon files:

- `icon.icns` - For macOS
- `icon.ico` - For Windows

These are placeholder files that should be replaced with properly formatted icons:

- For macOS, create a proper `.icns` file using tools like IconUtil
- For Windows, create a `.ico` file using image editors like GIMP or online converters

## License

This project is open-source and available under the MIT license.
