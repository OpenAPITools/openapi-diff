# Guidelines For Contributing

## Tips
- Smaller changes are easier to review
- Add test case(s) to cover the change
- Document the fix in the code to make the code more readable
- Make sure test cases passed after the change
- File a PR with meaningful title, description and commit messages
- Make sure the option "Allow edits from maintainers" in the PR is selected so that the maintainers can update your PRs with minor fixes, if needed.
- Recommended git settings
   - `git config core.autocrlf input` to tell Git convert CRLF to LF on commit but not the other way around 
- To close an issue (e.g. issue 1542) automatically after a PR is merged, use keywords "fix", "close", "resolve" in the PR description, e.g. `fix #1542`. (Ref: [closing issues using keywords](https://help.github.com/articles/closing-issues-using-keywords/))
