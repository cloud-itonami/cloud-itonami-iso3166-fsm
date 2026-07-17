(ns culture.facts-test
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [culture.facts :as facts]))

(deftest fsm-has-culture-basis
  (let [sb (facts/spec-basis "FSM")]
    (is (= 4 (count sb)))
    (is (= (count sb) (count (set (map :culture/id sb)))))
    (is (every? #(str/starts-with? (:culture/url %) "https://") sb))
    (is (every? #(= "FSM" (:culture/country %)) sb))
    (is (every? #(nil? (:culture/municipality %)) sb))
    (is (every? #(seq (:culture/summary %)) sb))
    (is (every? #(string? (:culture/retrieved-at %)) sb))))

(deftest unknown-jurisdiction-has-no-basis
  (is (nil? (facts/spec-basis "PLW")))
  (is (nil? (facts/spec-basis "zzz"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["FSM" "PLW"])]
    (is (= 2 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["PLW"] (:missing-jurisdictions c)))))

(deftest by-kind-filters
  (is (= 0 (count (facts/by-kind "FSM" :dish))))
  (is (= ["fsm.craft.rai-stones"]
         (mapv :culture/id (facts/by-kind "FSM" :craft))))
  (is (empty? (facts/by-kind "FSM" :other)))
  (is (empty? (facts/by-kind "PLW" :dish))))

(deftest tx-file-matches-catalog
  (let [tx (edn/read-string (slurp "data/culture-tx.edn"))
        flat (mapcat val (sort-by key facts/catalog))]
    (is (= (vec flat) (vec tx)))))
