(defproject btc-charts "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [mysql/mysql-connector-java "5.1.25"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [stock-algorithms "0.1.0-SNAPSHOT"]
                 [incanter/incanter-core "1.5.5"]
                 [incanter/incanter-charts "1.5.5"]]
  :main ^:skip-aot btc-charts.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
