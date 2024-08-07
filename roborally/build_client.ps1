# Get the directory of the current script
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Join-Path $scriptDir "client"
$outputDir = Join-Path $projectRoot "target\output"
$jlinkImageDir = Join-Path $projectRoot "target\jlink-image"

# Define the version variable from version file
$versionFile = Join-Path $scriptDir "version"
if (-not (Test-Path -Path $versionFile)) {
    Write-Output "Version file not found. Please create version file with the version number."
    exit 1
}
$version = Get-Content -Path $versionFile

Write-Output "`n* Building Windows installer for RoboRally client version $version *`n"

# Check if Maven is installed
$mvnPath = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $mvnPath) {
    Write-Output "Maven is not installed. Please install Maven to continue."
    exit 1
}

# Run mvn clean package
Write-Output "Running mvn clean package..."
$mvnResult = & mvn -f "$scriptDir\pom.xml" clean package
if ($LASTEXITCODE -ne 0) {
    Write-Output "Maven build failed. Exiting."
    exit 1
}

# Clean output and jlink directories if they exist
if (Test-Path -Path $outputDir) {
    Write-Output "Cleaning output directory..."
    Remove-Item -Recurse -Force $outputDir
}

if (Test-Path -Path $jlinkImageDir) {
    Write-Output "Cleaning jlink image directory..."
    Remove-Item -Recurse -Force $jlinkImageDir
}

# Check if JAVA_HOME is set
if (-not $env:JAVA_HOME) {
    Write-Output "JAVA_HOME is not set. Please set JAVA_HOME to the path of your JDK."
    exit 1
}

# Check if PATH_TO_FX is set
if (-not $env:PATH_TO_FX) {
    Write-Output "PATH_TO_FX is not set. Please set PATH_TO_FX to the path of your JavaFX SDK."
    exit 1
}

# Create custom runtime image using jlink
Write-Output "Making runnable image with jlink..."
jlink --module-path "$env:JAVA_HOME\jmods;$env:PATH_TO_FX" `
      --add-modules java.base,java.desktop,javafx.controls,javafx.fxml,jdk.zipfs `
      --output $jlinkImageDir
if ($LASTEXITCODE -ne 0) {
    Write-Output "jlink failed. Exiting."
    exit 1
}

# Package the application using jpackage
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
