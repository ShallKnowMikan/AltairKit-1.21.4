# AltairKit

This project is meant to simplify Minecraft plugin development.

## Installation

1. Download the `AltairKit.jar` file and the `AltairBuilder.exe` file.  
2. Place both files in the same folder and run `AltairBuilder.exe`.  
3. Add the dependency to your project:

### Dependency

#### Maven

```xml
<dependency>
  <groupId>dev.mikan</groupId>
  <artifactId>AltairKit</artifactId>
  <version>${version}</version>
</dependency>
```
### Gradle (.kts)
```kotlin
implementation("dev.mikan:AltairKit:1.21.4"
```
## Commands
Commands API allows you to create commands in the easiest way, by using `annotations`.

In order to successfully register it, the command method must be wrapped in a class that implements `CmdClass` and have at least the `@Command("")` annotation and an Actor object as the `first` parameter.

### Command example
```java
public class CmdTest implements CmdClass {

  @Command("Altair")
  @Complete({"kit", "by", "mikan"})
  @Permission("dev.mikan.module")
  @Sender(User.PLAYER)
  public void altair(final Actor actor) {
    actor.reply("<green>Thank you for using altair kit!");
  }
}
```
Once you create a class with methods like this, you can register the command by calling:

```java
AltairKit.registerCommands(cmdClass);
```

### Annotations
- `@Command("")` Defines the command. You can write a subcommand by adding a space. Root command and subcommands will automatically be tab-completed.
- `@Complete(array)` Adds additional tab completion on the last subcommand.
- `@Permission("node", blocking)` Specifies the required permission. blocking is a boolean (default true) if set to false, the method will still be called even if the player lacks permission
- `@Sender(SenderType)` Blocks the command execution if the sender type does not match.

### Actor
It represents the sender of the command. It offers useful methods like:
- `reply("<color>Message")` Sends a message to the sender (automatically colorized).
- `asPlayer()` Casts the sender to a Player, returns `null` if not a player.
- `asConsole()` Casts the sender to console, returns `null` if not console.

## Parameters
You can define as many parameters as needed:
```java
@Command("Altair")
@Complete({"kit", "by", "mikan"})
@Permission("dev.mikan.module")
@Sender(User.PLAYER)
public void altair(final Actor actor, double number, String message) {
  actor.reply("<green>Thank you for using altair kit!");
}
```
If the user does not pass those arguments, the defaults are:
- `Player`: `null`
- `int`: `-1`
- `double`: `-1.0`
- `String`: `""`

### @Default annotation
```java
@Command("Altair")
@Complete({"kit", "by", "mikan"})
@Permission("dev.mikan.module")
@Sender(User.PLAYER)
public void altair(final Actor actor, @Default Player target, String message) {
  target.sendMessage(message);
}
```
In this case, if the player is passed as parameter, it behaves normally.
If not, and the command is called like `/altair Hi!`,
then `target` will automatically be filled with the sender instance.
