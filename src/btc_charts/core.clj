(ns btc-charts.core
  (:gen-class)
  (:require [clojure.java.jdbc :as j]
            [stock-algorithms.aroon :as aroon]
            [stock-algorithms.psar :as psar]
            [incanter.charts :as c]
            [incanter.core :as i]))

(def default-query
  "SELECT * FROM `Trades_By_Min` ORDER BY `Time` ASC LIMIT 500 OFFSET 1000")

(defn get-data [query]
  (println "Getting data using:")
  (println query)
  (j/query {:subprotocol "mysql"
            :subname "//localhost:3306/Bitstamp"
            :user "root"
            :password "omgipwn"} [query]))

(defn build-aroon-data [prices period]
  (loop [aroon-data []
         prices prices]
    (if (< (count prices) period)
      aroon-data
      (recur (conj aroon-data (aroon/aroon (take period prices)))
             (rest prices)))))

(defn build-psar-data [highs lows closes]
  (loop [psar-data []
         highs highs
         lows lows
         closes closes
         sar (first closes)
         alpha psar/alpha-base
         ep (first highs)]
    (if (< (count closes) 2)
      psar-data
      (let [[n-sar alpha ep] (psar/psar
                               sar alpha ep
                               [(first highs) (second highs)]
                               [(first lows) (second lows)]
                               [(first closes) (second closes)])]
        (recur (conj psar-data n-sar) (rest highs) (rest lows)
               (rest closes) n-sar alpha ep)))))

(defn draw-aroon [prices]
  (let [aroon-data (build-aroon-data prices 30)
        chart (c/xy-plot)]
    (c/set-title chart "Aroon")
    (aroon/draw chart aroon-data 30)
    (i/view chart)))

(defn draw-psar [highs lows prices]
  (let [psar-data (build-psar-data highs lows prices)
        chart (c/scatter-plot)]
    (c/set-title chart "PSAR")
    (psar/draw chart (map inc (range (count prices))) psar-data prices)
    (i/view chart)))

(defn -main
  [& args]
  ;get btc data from database & chart it, look at cmd line flags for indicators to chart
  ;keep it simple for now, can always add a gui and that messy stuff later. Keep it mostly in
  ;functions to make it easy to use from the repl so it can be helpful in botting the market
  (let [data (get-data default-query)
        highs (map :high data)
        lows (map :low data)
        opens (map :open_price data)
        closes (map :close_price data)
        prices (map :close_price data)]
    (draw-aroon prices)
    (draw-psar highs lows prices)))
