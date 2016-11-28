Releasing
=========

1. Change the version in `gradle.properties` to a non-SNAPSHOT version.
2. Update the `CHANGELOG.md`.
3. Update the `README.md` with the new version.
4. `git commit -am "Prepare for release x.y.z"`
5. `git push` (travis will build and upload artifacts)
6. `git tag -a x.y.z -m "Version x.y.z"`
7. Update the `gradle.properties` to the next SNAPSHOT version.
8. `git commit -am "Prepare next development version."`
9. `git push && git push --tags`
