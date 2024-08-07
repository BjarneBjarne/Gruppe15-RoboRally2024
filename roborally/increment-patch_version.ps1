# Get the directory of the current script
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

# Define the version variable from project.properties
$propertiesFile = Join-Path $scriptDir "project.properties"
if (-not (Test-Path -Path $propertiesFile)) {
    Write-Output "project.properties not found. Please create project.properties with the version number."
    exit 1
}

$versionLine = Get-Content $propertiesFile | Select-String -Pattern "^version="
if ($versionLine -match "^version=(\d+)\.(\d+)\.(\d+)(-.*)?") {
    $major = [int]$matches[1]
    $minor = [int]$matches[2]
    $patch = [int]$matches[3]
    $suffix = $matches[4]

    # Increment patch version
    $patch += 1

    # Format the new version
    $newVersion = "$major.$minor.$patch$suffix"

    # Update the properties file
    (Get-Content $propertiesFile) -replace "^version=.*", "version=$newVersion" | Set-Content $propertiesFile

    Write-Output "Updated version to $newVersion"
} else {
    Write-Output "Version format is incorrect in project.properties."
    exit 1
}
