(ns tarefa.com-port
  "A Boot-compatible serial port library."

  (:require [serial.core :as s]))

; I'm relying on clj-serial (and PureJavaComm underneath it) to throw out any
; attempts to open the same port twice. After that I'm counting on the user to
; play nice. The atomicity of com-port and the port-name->com-port map together
; with the thread-safe nature of PureJavaComm should help. If this proves
; inadequate I'll move everything into agents to serialise access.

(def ^:private port-name->com-port (atom {}))

(defn open
  "Open a serial port and start processing incoming data.
  Options mirror those of clj-serial."
  [port-name & {:keys [baud-rate stopbits databits parity]
                :or   {baud-rate 9600
                       stopbits  s/STOPBITS_1
                       databits  s/DATABITS_8
                       parity    s/PARITY_NONE}}]
  (let [port     (s/open port-name
                         :baud-rate baud-rate
                         :stopbits  stopbits
                         :databits  databits
                         :parity    parity)
        com-port (atom {:port-name port-name :port port :on-rx-data (fn [_])})]
    (s/listen! port (fn [in-stream]
                      (let [n (.available in-stream)
                            b (byte-array n)]
                        (.read in-stream b 0 n)
                        ((:on-rx-data @com-port) port-name b))))
    (swap! port-name->com-port assoc port-name com-port)
    nil))

(defn set-in-handler!
  "Set function f as the handler to be called when incoming data is received."
  [port-name f]
  (when-let [com-port (@port-name->com-port port-name)]
    (swap! com-port assoc :on-rx-data f)))

(defn set-echo-checking!
  "Enable or disable echo checking."
  [port-name])

(defn output
  "Send outgoing data via a serial port.
  Call f with optional args when the data has gone."
  [port-name data f & args]
  (when-let [com-port (@port-name->com-port port-name)]
    (s/write (:port @com-port) data))
  (apply f args))

(defn tick
  "Call tick periodically to count down line idle and echo timeouts.
  Pass in the elapsed time in milliseconds since the last call."
  [elapsed-ms])

(defn close
  "Stop processing data and close a serial port."
  [port-name]
  (when-let [com-port (@port-name->com-port port-name)]
    (swap! port-name->com-port dissoc port-name)
    (s/close! (:port @com-port))))
