package com.domasmar.impl.commands;

/**
 * Created by domas on 14.11.5.
 */
public enum Commands {
    LS, // Get current directory content
    CD, // Change current directory content
    DOWNLOAD, // Download a file
    UPLOAD, // Upload a file
    LOGIN, // Login to ftp
    QUIT, // End session
    INVALID_COMMAND
}
