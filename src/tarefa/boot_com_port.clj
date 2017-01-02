(ns tarefa.boot-com-port
  "Wrap library functions as Boot tasks."
  {:boot/export-tasks true}
  (:require [boot.core :as boot :refer [deftask]]
            [boot.util :as util]
            [tarefa.com-port :as c]))

(deftask open-com-port
  "Open a serial port for use in subsequent tasks."
  [p port      PORT str "the serial port to open"
   b baud-rate BAUD int "the line speed"
   s stopbits  BITS int "the number of stop bits"
   d databits  BITS int "the number of databits"
   q parity    OPT  int "the parity-checking option"]
  (boot/with-pre-wrap fs
    (let [{:keys [port baud-rate stopbits databits parity]} *opts*]
      (println "Opening serial port" port)
      (println port
         :baud-rate baud-rate
         :stopbits  stopbits
         :databits  databits
         :parity    parity)
      (c/open port
         :baud-rate baud-rate
         :stopbits  stopbits
         :databits  databits
         :parity    parity))
    fs))

(deftask close-com-port
  "Close a serial port after subsequent tasks have completed."
  [p port PORT str "the serial port to close"]
  (boot/with-post-wrap fs
    (println "Closing serial port" (:port *opts*))
    (c/close (:port *opts*))
    fs))

(deftask with-com-port
  "Open a serial port for use in subsequent and close it afterwards."
  [p port PORT str "the serial port to open"]
  (let [port (:port *opts*)]
    (comp
      (open-com-port :port port)
      (close-com-port :port port))))
