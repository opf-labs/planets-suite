Zip64 POM
=========

This simple Maven project builds Hartwig's Zip64 source code from SourceForge.
It is used to create suitable Maven artifacts for upload to Central.
As per https://docs.sonatype.org/display/Repository/Uploading+3rd-party+Artifacts+to+Maven+Central

mvn source:jar javadoc:jar package gpg:sign repository:bundle-create
