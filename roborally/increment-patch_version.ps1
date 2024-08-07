# Get the directory of the current script
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

# Fetch the latest tag version from Git
$latestTag = git describe --tags --abbrev=0
if (-not $latestTag) {
    Write-Output "No tags found in the repository. Initializing version to 1.0.0-alpha."
    $newVersion = "1.0.0-alpha"
} else {
    Write-Output "Latest tag found: $latestTag"

    # Increment the patch number while keeping the suffix
    if ($latestTag -match "^(\d+)\.(\d+)\.(\d+)(-.+)?$") {
        $major = [int]$matches[1]
        $minor = [int]$matches[2]
        $patch = [int]$matches[3]
        $suffix = $matches[4]

        $patch++
        $newVersion = "$major.$minor.$patch$suffix"
    } else {
        Write-Output "Invalid tag format. Expected format: major.minor.patch[-suffix]"
        exit 1
    }
}

Write-Output "New version: $newVersion"

# Update the version file
$versionFile = Join-Path $scriptDir "version"
Set-Content -Path $versionFile -Value $newVersion

Write-Output "Version incremented to $newVersion and updated in version file."

# Add the updated version file to the commit
git add $versionFile
