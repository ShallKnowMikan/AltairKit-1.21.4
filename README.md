# AltairKit

This project is meant to simplify Minecraft plugin development.

## Installation

1. Download the `AltairKit.jar` file from the releases and the `AltairBuilder.exe` file.  
2. Place both files in the same folder and run `AltairBuilder.exe`.  
3. Add the dependency to your project:

## OR 

1. Download the `AltairKit.jar` file from the releases and put it into a folder. 
2. then run: ```mvn install:install-file -DgroupId=dev.mikan -DartifactId=AltairKit -Dversion=1.21.4 -Dpackaging=jar -Dfile=dir/to/AltairKit-1.21.4.jar``` (be sure to have maven installed first)

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
implementation("dev.mikan:AltairKit:1.21.4")
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
| invalid input | return value |
|---------------|--------------|
|`Player`       |    `null`    |
| `int`         |    `-1`      |
| `double`      |    `-1.0`    |
| `String`      |     `""`     |

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

## ConfigManager
Its purpose is to make it easy to create and load yml files.

It has 2 main method: `load()` and `get()`

### Usage
```java
final ConfigManager manager = new ConfigManager(plugin);
        
manager.load("config.yml");
        
final FileConfiguration config = manager.get("config.yml");
```
`Important!` Try to use just one instance and get it with getters around the project

#### load:
Generates the file from the name you passed as parameter. It generates comments contained in the file as well.
If there was no directories or plugin folder they'll be automatically created 

#### get
Once the file is loaded it is possible to retrieve its FileConfiguration object with this method.
`Watch out!!` Before using get you must have loaded that file with `ConfigManager#load(<name>)`.

It is also possible to combine the 2 method together:
```java
final ConfigManager manager = new ConfigManager(plugin);
        
final FileConfiguration config = manager.load("config.yml").get("config.yml");
```

## AltairKit - Time Utilities
This module provides a set of utility classes to manage and manipulate date and time values in a simple and readable format. It includes parsing, formatting, adding time, and calculating time differences between two dates.

### TimeUtils
A utility class that offers methods to:
- Get the current time (`current`)
- Add time to an existing `DateTime` (`add`)
- Calculate the time left between two `DateTime` instances (`timeLeft`)

#### Usage
```java
val utils = TimeUtils()

// Get the current date-time
val now = utils.current()

// Add 2 days and 3 hours to the current time
val future = utils.add(now, year = 0, month = 0, day = 2, hour = 3, minute = 0, second = 0)

// Calculate time left between two DateTime objects
val left = utils.timeLeft(now, future)
println(left.toString()) // Example: 51h 00m 00s
```
All dates are handled using the Europe/Rome timezone and formatted as dd/MM/yyyy HH:mm:ss.

### DateTime
A custom data structure to represent a full date and time, parsed from a `String`.

#### instantiation
```java
  new DateTime("05/08/2025 14:30:15")
```

#### Properties
- `day`, `month`, `year`: Parsed from the date string
- `time`: A Time object with hours, minutes, and seconds
- `valid`: Indicates whether the string was parsed successfully.

#### Notes
- The input string must follow the format: `"dd/MM/yyyy HH:mm:ss"`.
- If the input is invalid or empty, the object is marked as `valid = false`.
#### Example
```java
val date = DateTime("01/01/2025 12:00:00")
  
if (date.valid) {
  println("Year: ${date.year}")
  println("Time: ${date.time}")
}
```

### Time
A class representing a duration or clock time.

#### Instantiation
```java
// 2 hours, 30 minutes, 45 seconds
new Time(2, 30, 45) 
```

#### Properties 
- `hours`, `minutes`, `seconds`: Basic time units.
- `valid`: true if all values are non-negative.
- `zero`: true if all units are zero.

#### Example
```java
Time t = new Time(1, 20, 0)
println(t.toString()) // "01h 20m 00s"
```

## AltairKit - Event
This abstract class simplifies the creation of custom Spigot events. It implements Cancellable and provides a built-in handler list.

### Features

- Extends `org.bukkit.event.Event`
- Implements `Cancellable`
- Adds a `run()` abstract method to execute event logic
- Custom cancellation logic

### Usage
```java
public class MyEvent extends dev.mikan.altairkit.utils.Event {

    @Override
    public void run() {
        if (isCancelled()) return;
        // custom logic here
    }
}

// Usage
MyEvent event = new MyEvent();
Bukkit.getPluginManager().callEvent(event);
event.run();
```
Make sure to check if (isCancelled()) at the beginning of run().

## AltairKit - Module
This class defines a reusable module structure for Spigot plugins. It helps split plugin logic into manageable components. Useful in bigger projects.

### Features

- Manages plugin reference and logger
- Stores and registers event listeners
- Provides utility logging methods: `info()`, `warning()`, `error()`
- Contains lifecycle hooks:
  - `onEnable()`
  - `onReload()`
  - `onDisable()`
  - `loadConfig()`
  - `registerCommands()`
  - `registerListeners()`
 
### Usage 
```java
public class ExampleModule extends dev.mikan.altairkit.utils.Module {

    public ExampleModule(Plugin plugin) {
        super(plugin, "ExampleModule");
    }

    @Override
    public void onEnable() {
        loadConfig();
        registerCommands();
        registerListeners();
        info("Module enabled!");
    }

    @Override
    public void loadConfig() {
        // Load config here
    }

    @Override
    public void registerCommands() {
        // Register commands
    }

    @Override
    public void registerListeners() {
        this.listeners = Set.of(new MyListener());
        listen();
    }

    @Override
    public void onReload() {}

    @Override
    public void onDisable() {}
}
```
Use `listen()` to automatically register all listeners stored in this.listeners.

## AltairKit - Tree
Tree<T> is a generic tree data structure with support for dynamic insertion, deletion, searching, and traversal (DFS/BFS).

### Features
- Set or define root node
- Insert nodes (with or without custom conditions)
- Search or fetch nodes by value or condition
- Remove nodes
- Filter nodes
- Check if a node is a leaf
- Generate a string-based tree visualization with depth info

### Usage
```java
Tree<String> tree = new Tree<>();
tree.setRoot("root");
tree.insert("root", "child1");
tree.insert("root", "child2");
tree.insert("child1", "subchild");

System.out.println(tree.contains("child2")); // true
System.out.println(tree.isLeaf("child2"));    // true
System.out.println(tree.toString());          // Tree representation
```
### Methods overview

| Method                          | Description |
|---------------------------------|-------------|
| `setRoot(value)`                | Sets the root node |
| `insert(parent, value)`         | Adds a new node under a parent |
| `insert(parent, value, fetchCondition)` | Inserts with condition |
| `search(value)`                 | Returns the node matching the value |
| `get(value)`                    | Gets the value from a node |
| `contains(value)`               | Checks if the value exists |
| `isLeaf(value)`                 | Returns true if node has no children |
| `remove(value)`                 | Removes a node |
| `values()`                      | Returns all values in the tree |
| `filter(condition)`             | Filters and returns nodes that pass a condition |
| `fetch(condition)`              | Returns the first value that matches a condition |
| `toString()`                    | Text-based visual representation of the tree |

### toString example
```
│
├─── Depth: 0 : root 
│
├──────── Depth: 1 : child1 
│
├──────────── Depth: 2 : subchild 
│
└──────── Depth: 1 : child2 
```
