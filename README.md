# Start.jar
Minimal Java HTTP server to start html5 games. **Start.jar** has zero dependencies besides java and it works on Windows, Linux and Mac OS X.

## Why?
To be able to distribute a HTML5 game/application to the users without hosting the game. **Start.jar** will act as an HTTP server/launcher for your game. It will open your the main file of your game in the default browser immediately after starting.

### Testing tool
**Start.jar** can be used as a testing tool while developing an HTML5 game. Instead of wasting your time trying to install and configure Apache/Nginx just use simple and straightforward **start.jar**. You can add **start.jar** to your `PATH` and run any HTML5 game just by executing a command under the game's directory.

## How?
Put **start.jar** in the directory with your game:

```
+- [My Game]
   |
   +- [js]
   +- [css]
   +- index.html
   +- start.jar
```

Actually this is it for default configuration. **Start.jar** will deploy a HTTP server at `127.0.0.1:8888` and open `index.html` the default browser.

*.jar files should be runnable with double click in MS Windows if Java is installed properly. But also you can create a BAT file with necessary `java -jar` command and optional start.jar arguments:

```
java -jar start.jar
```

Similarly you can create bash/shell script for UNIX-like OS:

```
#!/bin/sh
java -jar start.jar
```

And don't forget to set execution rights for your script file:
```
chmod +x start.sh
```

### More configuration
You can change defaults of the start.jar by passing arguments to the program or by creating a configuration file.

#### Arguments

You can specify program arguments like this:

```shell
$ java -jar start.jar --host game.localhost --port 1234
```

* `--host` `-h` - host to be bound for your HTTP server. Default value is `127.0.0.1`
* `--port` `-p` - port to bind. Default value is `8888`
* `--root` `-r` - Document root for your server. Default value is the current working directory
* `--frame` `-w` - `1` for enable showing the program window (default) or `0` to run the program in background
* `--config` `-c` - Path of the configuration file. Default value is `start.config` in the current working directory
* `--index` `-i` - Default file to load if not specified (default value is `index.html`)
* `--shutdown-after` or `-S` - Determines the time in seconds to shutdown the server after the last request. Default value is `300` (5 minutes)

#### Configuration file

By default **start.jar** is looking for `start.config` in the current directory, but you can change this by passing `-config` argument to the program.

Configuration file has format like this:
```
DocumentRoot=C:\My Documents\Game
Host=127.0.0.1
Port=8888
ShowFrame=true
ShutdownAfter=300
DefaultFile=index.html
```

Each option should be placed on it's own line. Parameters and values are separated with `=` sign. No spaced allowed.

**Available parameters:**

* `DocumentRoot` - Path to the root directory for your server.
* `Host` - Host to bind your server at.
* `Port` - Port to bind your server at.
* `ShowFrame` - Enable or disable showing the program window (`true` or `false`)
* `ShutdownAfter` - Time is seconds after last request to shutdown the server (default value is `300` or 5 minutes)
* `DefaultFile` - Default file if not specified in path. Default value is `index.html`

## Requirements
Java

## How to build
Easy.

All you need is JDK and installed Ant. Just run
```
ant
```
under root directory of the project. It will create `start.jar` file under `./dist` directory.
