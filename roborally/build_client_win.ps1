# Variables
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Join-Path $scriptDir "client"
$outputDir = Join-Path $projectRoot "target\output"
$jlinkImageDir = Join-Path $projectRoot "target\jlink-image"
$versionFile = Join-Path $scriptDir "version"

# Run the version update script
& "$scriptDir\update_version.ps1"

# Version
if (-not (Test-Path -Path $versionFile)) {
    Write-Output "Version file not found. Please create version file with the version number."
    exit 1
}
$version = Get-Content -Path $versionFile

Write-Output "`n *** Building Windows installer for RoboRally client version $version *** `n"

# Maven check
$mvnPath = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $mvnPath) {
    Write-Output "Maven is not installed. Please install Maven to continue."
    exit 1
}
# JAVA_HOME check
if (-not $env:JAVA_HOME) {
    Write-Output "JAVA_HOME is not set. Please set JAVA_HOME to the path of your JDK."
    exit 1
}

# PATH_TO_FX check
if (-not $env:PATH_TO_FX) {
    Write-Output "PATH_TO_FX is not set. Please set PATH_TO_FX to the path of your JavaFX SDK."
    exit 1
}

# mvn clean package
Write-Output "Running mvn clean package..."
& mvn -f "$scriptDir\pom.xml" clean package
if ($LASTEXITCODE -ne 0) {
    Write-Output "Maven build failed. Exiting."
    exit 1
}

# Clean build directories
if (Test-Path -Path $outputDir) {
    Write-Output "Cleaning output directory..."
    Remove-Item -Recurse -Force $outputDir
}
if (Test-Path -Path $jlinkImageDir) {
    Write-Output "Cleaning jlink image directory..."
    Remove-Item -Recurse -Force $jlinkImageDir
}

# JLink runtime image
Write-Output "Making runnable image with jlink..."
jlink --module-path "$env:JAVA_HOME\jmods;$env:PATH_TO_FX" `
      --add-modules java.base,java.desktop,javafx.controls,javafx.fxml,jdk.zipfs `
      --output $jlinkImageDir
if ($LASTEXITCODE -ne 0) {
    Write-Output "jlink failed. Exiting."
    exit 1
}

# JPackage installer
Write-Output "Packaging installer with jpackage..."

jpackage `
    --name RoboRally `
    --input (Join-Path $projectRoot "target") `
    --main-jar client-0.2.jar `
    --main-class com.group15.roborally.client.StartRoboRally `
    --runtime-image $jlinkImageDir `
    --type exe `
    --dest $outputDir `
    --icon (Join-Path $projectRoot "src\main\resources\com\group15\roborally\client\images\Icon\icon.ico") `
    --win-shortcut `
    --win-menu `
    --win-dir-chooser `
    --win-per-user-install `
    --win-upgrade-uuid "a6c0e52c-a7a5-44be-adeb-4392b7a4f3a6" `
    --app-version $version

if ($LASTEXITCODE -ne 0) {
    Write-Output "jpackage failed. Exiting."
    exit 1
}

Write-Output "`nSuccessfully built and packaged Windows installer for RoboRally client at: $outputDir\RoboRally-$version.exe"
