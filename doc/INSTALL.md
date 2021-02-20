# conkw documentation - installation guide

## Status

This project is being developped, so things aren't all smoothed out yet. When ready for a v1, so, soon, a binary version will be available to make things easier.

## Prerequisites

conkw only works on Linux for now.

In order to install conkw you will need to have a working version of `java`, `ant`, and `maven` at your disposal. Java needs to be version 1.8 or greater.

## Howto

1. Check out the repository

```
git clone https://github.com/pieroxy/conkw.git
```

2. Package the UI

```
cd conkw/src/web
ant
```

3. Build the solution

```
cd ../java
mvn package
```

4. Run the program

```
java -jar ~/SRC/perso/conkw/src/java/target/conkw.jar
```

This last step will ask you if you want to install the program in `~/.conkw`. If you want it installed somewhere else just define a `CONKW_HOME` environment variable and launch the program again. Just remember that at this stage, the program keeps all it needs in one place: Binaries, configuration, temp files, etc...

The installation proram will give you instructions as to how to run the program.

After it runs, you can access the program with any browser by typing the URL `http://localhost:12789`. You'll then be looking at the default setup, with one grabber: The system grabber.

Hint: Click on the clock to see the different clock faces available.