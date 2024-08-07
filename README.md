# Introduction

This project is a video game adaptation of the RoboRally board game. The instructions below will guide you through the installation steps, as well as how to play the game. I've tried to make the installation guide comprehensive enough for *almost* any user with enough patience, can build and install the game and/or the server themselves.

# Prerequisites, Build & Installation

While not tested with other versions, the project may run with some other versions of the prerequisites. If in doubt, you can just install the ones specified.

If you want to build the game, it is beneficial to know a bit about environment variables and how to use your system's terminal/console. If you need help with steps involving these, there are plenty of helpful tutorials on YouTube and elsewhere. If this is new to you, note that the process of setting environment variables differs depending on your operating system and the terminal/console/shell/etc you're using.

If you already have the installer for the game version you need (check the tags for versions) and do not intend to host a server, you don't need to install the prerequisites.

You can run the installer by double-clicking it. After the installation is complete, you can open the installed application from the installed RoboRally directory in your system's default application directory. This is usually located at the following paths:

**Common system installation Paths**:
  - Windows:   `C:\Program Files\RoboRally\`
  - Mac:       `Applications/RoboRally`

## Building the Client & Server JARs

This section is for those who want to run a server, have the game without taking as much disk space, or already have the prerequisites installed correctly.

### Notice

The `PATH` variable mentioned multiple times can differ in its name, depending on the system you're running. It is sometimes called `Path` instead.

### Building the UBER-JAR
To build the game as a JAR file, ensure you have the following installed and configured:

- **Java 22.0.1**:
  - Download the JDK from [Oracle](https://www.oracle.com/java/technologies/downloads/). Unzip the `jdk-22` where you want to keep the JDK installed.
  - Set the `JAVA_HOME` environment variable to point to the Java JDK 22 directory.
  - Add the `bin` directory inside the JDK 22 directory to the `PATH` variable.

- **Apache Maven 3.9.8**:
  - Download Maven from [Apache Maven](https://maven.apache.org/download.cgi).
  - Add the `bin` directory inside the Apache Maven 3.9.8 directory to the `PATH` variable.

- **JavaFX 22.0.1**:
  - Download JavaFX from [GluonHQ](https://gluonhq.com/products/javafx/).
  - Set a custom/unofficial environment variable `PATH_TO_FX` pointing to the `lib` folder inside the downloaded JavaFX SDK directory.

When you have done this, you are ready to build!

1. Open your preferred terminal and navigate to the repo directory `Gruppe15-RoboRally2024/roborally`.

2. If maven is installed correctly, you can enter the command `mvn clean install` in there, which will compile and package the UBER-JAR, as well as install other needed dependencies for you.

### Building the Installer

To build the game installer, ensure you have the same prerequisites as for the UBER-JAR, as it is needed for the installer.

1. Again, in the terminal, you should be at the directory `Gruppe15-RoboRally2024/roborally`.

2. Run one of the following commands to run one of the provided build scripts, depending on what operating system you are on:
   build_client_win.ps1

**Build scripts**:
- Windows:   `.\build_client_win.ps1`
- Mac:       `./build_client_mac.ssh`
- Linux:     `./build_client_lin.ssh`

The build script will compile the project, create a JLink runtime image, and package the application using jpackage.




# How to play the game
## Creating and joining a game
Once the application has opened, you should see the main menu with the RoboRally titel at the top, as well as three buttons.
To create or join a game, press the "Multiplayer" button in the Main Menu.
* Enter your player name.
* Enter the address for the server/computer running the server application, in the format already present.

To create a new game, press the "Create Lobby" button.

To join an existing game, enter the Game ID of the lobby, given by any other player in a lobby, then press the "Join Lobby" button.

When joining a game, every player MUST have a unique name to join.

## Setting up the game in the lobby
Players can view the different playable courses by scrolling with the mouse in the panel to the left.
The player who created the game, will be referred to as the "host".
The host will have a star icon next to them in the lobby.
The host can select a course to play by clicking on any of the playable courses to the left.

When all players have selected a unique robot to play as, all players who are not the host, needs to press the now green "Ready" button.
Players who are ready have a green tick mark next to them.
When every player, besides the host, have pressed ready, the host can press the now green "Start" button to start the game.

## Start of the game
After the course has loaded, it is time for each player to pick a starting position for their robot.
Press on one of the six white circles on the board to choose your spawn point.
After choosing a spawn point, press one of the gray arrows next to your player, to pick a starting direction and
confirming your spawn point choice.

## Playing a game
### Programming phase
Once all players have confirmed their starting position and direction, they are each dealt a hand of programming cards.
Each player will select up to five cards to play during a turn. The cards are played by dragging them from hand, on the
left side, to your program registers, at the bottom of your player mat at the bottom center. Once every player has
chosen the programming cards, they wish to play, press the Ready button. When all players have pressed the ready button,
the interaction phase begins.

### Interaction phase
Each player's programming cards are execute in the order of their distance to the antenna on the board, with the closest
player being first. All the player programs at register 1 are executed first, followed by the board elements activating,
as well as player lasers. After that, a new order is decided by the antenna, where after all player programs at register
2 is activated, and so on.
If you are as lucky as to have a programming card or an upgrade card which allows a choice at its register, you need to
make the choice by pressing any of the choice buttons, located where the Ready button were. When you've chosen an
option, the game continues.
When the 5'th and final register is done, the upgrade phase starts.

### Upgrade phase
At the upgrade phase, players take turn in buying upgrade cards.

Upgrade cards can be bought with your energy cubes. You can view your number of energy cubes on your player mat. Energy
cubes can be collected by ending your register on an energy cube, or ending your turn on an energy space, located where
energy cubes are put at the start of the game.
Energy cubes do not respawn.

Yellow (permanent) cards can be bought by dragging them
to the top of your player mat.
Red (temporary) cards can be bought by dragging them to one of the slots to the right of your player mat.

If you want to replace an upgrade card, you can drag a new upgrade card on top of an owned upgrade card, effectively
removing the old card.

## Winning a game
The goal of the game is to be the first player to reach all the different check points on the board. Checkpoints must be
reach in order, going from checkpoint 1, to 2, etc. You can reach a checkpoint, simply by ending a register on a
checkpoint space.

# Showcase video 
The video shows a now outdated version of the game:

https://www.youtube.com/watch?v=IkSLw8IZ5Xg
