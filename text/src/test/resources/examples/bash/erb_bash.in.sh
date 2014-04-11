#!/bin/sh

perl -e's/to=5/to=10/' /test/file
echo "Parsed at <%= Time.now %>"
echo "Executed at `Date`"
command 'open quote
other_command
