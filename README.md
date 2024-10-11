# Unduplicator

Traverse a directory and list/remove duplicate files in _that_ directory.
Recurses through subdirectories.

Duplicates are calculated by the md5 hash of the file content.

Running it without `-delete` will only list the duplicates.

Running it with `-delete` will prompt you at start that duplicates will be
deleted (enter something that starts with (case insensitive) `Y` to continue,
otherwise exit).

It will keep the file with the shortest path and delete the others.

Build it with something like

```bash
scala-cli package . -o ~/bin/unduplicator --force
```

Run it with something like

```bash

# Current directory
unduplicator

# Specific directory
unduplicator /path/to/directory

# Perform the cleanup
unduplicator --delete
```

## Motivation

I made this after finally going through all my takeout archives from when Google
Music shut down, and old DVDs of mp3 backups. I had a lot of duplicates.

It would probably also be useful for cleaning up a photo collection.
