# Using Inventory Data Backup and Restore

This document explains how to use the backup and restore functionality in the Black and Pink Inventory Management System.

## Creating a Backup

1. Open the application
2. Click on the "Settings" card on the home screen
3. In the Settings screen, find the "Data Management" section
4. Click on the "Export Data Backup" button
5. Select a location to save your backup file (.zip)
6. Click "Save" to create the backup

## What's Included in Backups

Backups include:
- All inventory items with their properties and quantities
- All inventory sessions with their start/end dates
- Transaction history for all items
- Analytics data
- Session summaries

## Restoring from a Backup

1. Open the application
2. Click on the "Settings" card on the home screen
3. In the Settings screen, find the "Data Management" section
4. Click on the "Import Data from Backup" button
5. Select a previously created backup file (.zip)
6. Click "Open" to restore from the backup
7. Confirm the restore operation when prompted

## Safety Features

- Automatic backup: The system automatically creates a backup of your current data before importing, so you can revert if needed
- Validation: The imported data is validated to ensure it's a proper backup file
- Consistent state: All data is imported together to maintain data consistency

## Troubleshooting

If you experience issues with import/export:

1. Ensure you have proper read/write permissions to the backup folder
2. Check that the backup file is not corrupted
3. Make sure you're selecting a backup file created with this application
4. If the application crashes during import, try restarting it
5. Look for backup files in your home directory under `.blackandpink/backup_*`

## Using Backups for Data Migration

You can use the backup/restore functionality to transfer your inventory between different computers:

1. Export a backup on the source computer
2. Copy the backup file to the destination computer
3. Install the application on the destination computer
4. Import the backup on the destination computer

The complete inventory, sessions, and configuration will be transferred.
