# Variables
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$versionFile = Join-Path $scriptDir "version"

# Fetch latest tags from the remote repository
git fetch --tags

# Function to get the latest valid tag
function Get-LatestTag {
    param (
        [string]$tagPattern = "^v[0-9]+\.[0-9]+\.[0-9]+(-alpha|-beta)?$"
    )
    $tags = git tag --sort=-v:refname
    foreach ($tag in $tags) {
        if ($tag -match $tagPattern) {
            return $tag
        }
    }
    return "v0.0.0"  # Default initial tag if no valid tag is found
}

# Get the latest valid tag
$latestTag = Get-LatestTag

# Ensure the latest tag is in the correct format
if ($latestTag -match "^v([0-9]+)\.([0-9]+)\.([0-9]+)(-alpha|-beta)?$") {
    $major = [int]$matches[1]
    $minor = [int]$matches[2]
    $patch = [int]$matches[3]
    $suffix = $matches[4]

    # Determine the increment type based on the branch name
    $branch = git rev-parse --abbrev-ref HEAD
    if ($branch -eq "main") {
        $newMajor = $major + 1
        $newMinor = 0
        $newPatch = 0
        $newVersion = "v$newMajor.0.0$suffix"
    } elseif ($branch -eq "dev") {
        $newMinor = $minor + 1
        $newPatch = 0
        $newVersion = "v$major.$newMinor.0$suffix"
    } else {
        $newPatch = $patch + 1
        $newVersion = "v$major.$minor.$newPatch$suffix"
    }

    # Update the version file
    Set-Content -Path $versionFile -Value "version=$newVersion"

    # Stage the updated version file, commit, and push
    git add $versionFile
    git commit -m "Increment version to $newVersion"
    git push origin HEAD
    git tag -a $newVersion -m "Release $newVersion"
    git push origin $newVersion

    Write-Output "Version updated to $newVersion and staged for commit."
} else {
    Write-Output "Invalid tag format. Expected format: v<major>.<minor>.<patch>[-alpha|-beta]"
    exit 1
}
