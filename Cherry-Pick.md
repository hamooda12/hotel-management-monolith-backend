# Git Cherry-Pick

## What is Git Cherry-Pick?

`git cherry-pick` is a Git command used to copy a specific commit from one branch and apply it to another branch.

It is useful when a commit was made on the wrong branch, but you still want to move that change to the correct branch without merging the whole branch.

Example:

```bash
git checkout correct-branch
git cherry-pick commit-hash
```

## Why Git Cherry-Pick is Useful

Cherry-pick is useful because it allows developers to move only the needed commit instead of merging all changes from another branch.

For example, if a developer accidentally commits a bug fix on the wrong branch, they can cherry-pick that commit into the correct branch.

It is also useful when one commit is needed in multiple branches, such as applying the same hotfix to both `main` and `develop`.

## Common Use Cases

* Moving a commit from the wrong branch to the correct branch.
* Applying a hotfix from one branch to another.
* Taking only one feature or fix without merging the full branch.
* Reusing a specific change from an old branch.

## Risks of Using Git Cherry-Pick

Although cherry-pick is useful, overusing it can cause problems.

First, it creates a new commit with a different commit hash. This can make the Git history harder to understand.

Second, it can cause duplicate commits if the same change is later merged normally.

Third, cherry-picking many commits can create conflicts, especially if the branches are very different.

Also, cherry-pick can hide the real branch history because it copies changes without showing the original development flow.

## Best Practice

Use cherry-pick only when you need a specific commit.

Do not use it as a normal replacement for merging or rebasing.

For normal feature work, it is better to use pull requests and merge the full feature branch.

Cherry-pick should mainly be used for special cases like wrong-branch commits or urgent hotfixes.

## Conclusion

`git cherry-pick` is a powerful command that helps move specific commits between branches.

It is very useful when used carefully, but overusing it can make the project history confusing and harder to maintain.
