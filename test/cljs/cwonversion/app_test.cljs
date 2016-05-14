(ns cwonversion.app-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [cljs.test :as t]
            [cwonversion.app :as cw]))

(deftest test-kr-number-parse []
  (is (= (cw/parse-number :krw "만원")
         1e4))
  (is (= (cw/parse-number :krw "십조원")
         (* 10 1e12)))
  (is (= (cw/parse-number :krw "100원")
         100))
  (is (= (cw/parse-number :krw "100만원")
         (* 100 1e4)))
  (is (= (cw/parse-number :krw "0.1만원")
         (* 0.1 1e4)))
  (is (= (cw/parse-number :krw "1원")
         1)))

(deftest test-us-num-parse []
  (is (= (cw/parse-num :usd "1 thousand")
         1000)))

(deftest re-test []
  (is (re-find #"\d*[\.\d+]?\d*" "1.4 thousand")
      "1.4"))
