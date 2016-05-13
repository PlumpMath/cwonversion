(ns cwonversion.app
  (:require [reagent.core :as reagent :refer [atom]]
            ;; [ajax.core :refer [GET]]
            [cljs.pprint :as pp]
            [clojure.string :as str]))

(def kr-number-units
  {"원" 1
   "일" 1
   "십" 10
   "백" 100
   "천" 1000
   "만" 1e4
   "억" 1e8
   "조" 1e12})

(def us-number-units
  {"hundred" 100
   "thousand" 1e3
   "k" 1e3
   "million" 1e6
   "mn" 1e6
   "billion" 1e9
   "bn" 1e9
   "trillion" 1e12
   "tn" 1e12})

(def app-state (atom {:xr 1175.25
                      :krw nil
                      :usd nil}))

(defn forex-display []
  [:p "The current FOREX rate for USD and KRW is: "
   [:b (:xr @app-state)]])

(defn parse-number
  "Takes a Korean number string, such as '100만', and parses it as an integer."
  [key s]
  (let [digits (re-find #"\d*[\.\d+]?\d*" s)
        n (if (seq digits) digits 1)
        units (case key
                :krw (re-find #"\D+" s)
                :usd (-> ((fnil str/trim "")
                          (re-find #"\D+" s))
                         (str/split #" ")))
        parser (case key
                 :krw kr-number-units
                 :usd us-number-units)]
    (if (empty? s)
      0
      (apply * n (remove nil? (map parser units))))))

(defn convert [input-str from to]
  (let [xr (case from
             :krw (/ 1 (:xr @app-state))
             :usd (:xr @app-state))]
    (swap! app-state assoc from input-str)
    (swap! app-state assoc to (.toLocaleString
                               (* xr (parse-number from input-str))))))

(defn currency-input [from to]
  [:form {:on-submit #(.preventDefault %)}
   [:input {:type "text"
            :value (from @app-state)
            :on-change #(convert (-> % .-target .-value)
                                 from to)}]
   (if (= from :krw)
     " 원"
     " dollars")])

(defn converter-container []
  [:div
   [currency-input :krw :usd]
   [currency-input :usd :krw]])

(defn app-state-display []
  [:pre (with-out-str (pp/pprint @app-state))])

(defn home []
  [:div
   [forex-display]
   [converter-container]
   ;; [app-state-display]
   ])

(defn init []
  (reagent/render [home]
                  (.getElementById js/document "container")))
