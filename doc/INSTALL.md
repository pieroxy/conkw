# conkw documentation - installation guide

## Status

This project is being developped, so things aren't all smoothed out yet. When ready for a v1, so, soon, a binary version will be available to make things easier.

## Prerequisites

conkw works on Linux, MacOS and Windows.

In order to install conkw you will need to have a working version of `java`, `ant`, and `maven` at your disposal. Java needs to be version 1.8 or greater.

## Howto

### 1. Check out the repository

```sh
git clone https://github.com/pieroxy/conkw.git
```

### 2. Build the solution

Note that if you are on windows or MacOS, you will need to edit `local.properties` to hold the location of the `git` binary.

```sh
cd conkw/src/java
cp local.sample.properties local.properties
mvn package
```

### 3. Run the program

```sh
java -jar target/conkw-bin-*.jar install
```

This last step will install the program in `~/.conkw`. If you want it installed somewhere else just specify where with the `--home` option. Just remember that at this stage, the program keeps all it needs in one place: Binaries, configuration, temp files, etc...

The installation program will give you instructions as to how to run the program.

After it runs, you can access the program with any browser by typing the URL `http://localhost:12789`. You'll then be looking at the default UI, with access to the grabbers and the documentation.

Hint: Click on the clock to see the different clock faces available.

### 4. Upgrade or reinstall the program

The step 3 above will not do anything if conkw is already installed. To force installation to happen again, use the `--upgrade` flag. If you edited and changed files in the `config` and/or `ui` directories, they will be kept and the new files will be installed as `whatever.file.1`.

If you want to reset the configuration to the new install configuration, use the `--override-config-files` flag. This way, both files will be reset, and if you made changes to them, your version will be saved as `whatever.file.1`, so nothing is really lost there. Same for the UI files with the `--override-default-ui` flag.
