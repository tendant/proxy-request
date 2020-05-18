(defproject tendant/proxy-request "0.1.0-SNAPSHOT"
  :description "Proxy HTTP Request"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [ring/ring-core "1.8.1"]
                 [org.clojure/tools.logging "1.1.0"]]
  :repl-options {:init-ns proxy-request.core})
