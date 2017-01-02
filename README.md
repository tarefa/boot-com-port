# boot-com-port

A [Boot-compatible] serial port library written in [Clojure].

[Boot-compatible]: http://boot-clj.com/
[Clojure]: http://clojure.org/

## What does it do?

[boot-com-port] provides some basic functions to open and close serial ports.
While a port is open `boot-com-port` listens for incoming data and you can
supply a function to be called whenever data is received. You can also supply
`boot-com-port` with a function to call if the incoming data stops for any
length of time. It goes without saying that there are functions you can call to
send outgoing data, and `boot-com-port` can also perform echo-checking when
appropriate.

On top of this, `boot-com-port` gives you a set of tasks that you can call from
Boot to open and close serial ports. While Boot isn't particularly intended
for this, these tasks give you a handy way to configure serial ports on the
command line if you have a Boot-launched application or just want to work with
serial ports from the REPL.

Under the hood `boot-com-port` uses the [clj-serial] library so there's no
reason it shouldn't work wherever `clj-serial` does.

[boot-com-port]: https://github.com/tarefa/boot-com-port
[clj-serial]: https://github.com/peterschwarz/clj-serial

## Introduction to the serial port functions

To open a serial port and start processing incoming data:

    (require '[tarefa.com-port :as c])
    (c/open "COM3" :baud-rate 9600)

The port monitoring thread runs, in the background, inside your REPL. Opening
the serial port automatically provides you with these functions...

    (c/set-in-handler! "COM3" (fn [port-name data] ...)

This sets function `f` as the handler to be called when incoming data is
received via the given port. `f` will receive the `port-name` string and the
`data` as a (Java) byte array. **Note that serial ports are always referred to
by name**.

If you need to know when the line has gone quiet you can set another handler...

    TODO: idle handling

To send outgoing data...

    (c/output "COM3" data)

where the `data` is anything acceptable to `clj-serial/write`. To output a
string, first transform it to a byte array...

    (c/output "COM3 "(.getBytes "my string"))

Echo-checking is a little more involved...

    TODO: echo-checking

When you are done, call `(c/close "COM3")` to clean up.

Line idle detection and echo-checking require you to call `tarefa.com-port/tick`
periodically, for example using [Seesaw] to make a call every 10 milliseconds...

    (require '[seesaw.timer :as t])
    (let [rate 10
          ticker (fn [s] (tick rate))]
      (t/timer ticker :delay rate))

[Seesaw]: https://github.com/daveray/seesaw

## Boot tasks

    $ boot with-com-port --port COM3 --baud-rate 9600 repl

This pipeline will open a serial port and start a REPL. When you `(quit)` the
REPL it will clean up and close the port. The `with-com-port` task composes
together this pre-wrap task

    $ boot open-com-port --port COM3 --baud-rate 9600 repl

and this post-wrap task

    $ boot close-com-port --port COM3

## To use boot-com-port in your project

First clone this project and run `boot build` in it to install `boot-com-port`
locally. Then, in your own project's `build.boot`, add `[tarefa/boot-com-port
"0.1.0"]` to your `:dependencies` and, if you also want to use the Boot tasks,
require them like this:

    (require '[tarefa.boot-com-port :refer [open-com-port
                                            close-com-port
                                            with-com-port]])

## License

Copyright © 2017 Tim Sharples

Distributed under the Eclipse Public License either version 1.0 or (at your
option) any later version.
