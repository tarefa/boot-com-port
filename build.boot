(def project 'tarefa/boot-com-port)
(def version "0.1.0")

(set-env! :resource-paths #{"resources" "src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "1.8.0"]
                            [boot/core "2.7.1" :scope "test"]
                            [adzerk/boot-test "1.1.2" :scope "test"]
                            [clj-serial "2.0.4-SNAPSHOT"]])

(task-options!
 pom {:project     project
      :version     version
      :description "A Boot-compatible serial port library"
      :url         "https://github.com/tarefa/boot-com-port"
      :scm         {:url "https://github.com/tarefa/boot-com-port"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build
  "Build and install this library locally."
  []
  (comp (pom) (jar) (install)))

(require '[adzerk.boot-test :refer [test]]
         '[tarefa.boot-com-port :refer [open-com-port
                                        close-com-port
                                        with-com-port]])
