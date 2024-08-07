# Step 1: Fetch all tags and commits
git fetch --tags --all

# Step 2: List all tags and filter the ones associated with the last 100 commits
$commits = git log --pretty=format:'%H' -n 100
$tags_to_delete = @()

foreach ($commit in $commits) {
    $tags = git tag --contains $commit
    $tags_to_delete += $tags
}

# Step 3: Remove duplicate tags
$unique_tags_to_delete = $tags_to_delete | Sort-Object -Unique

# Step 4: Delete the filtered tags locally
foreach ($tag in $unique_tags_to_delete) {
    if ($tag) {
        git tag -d $tag
    }
}

# Step 5: Push the deletions to the remote repository
foreach ($tag in $unique_tags_to_delete) {
    if ($tag) {
        git push origin :refs/tags/$tag
    }
}
