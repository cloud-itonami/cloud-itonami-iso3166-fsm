(ns statute.facts-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [statute.facts :as facts]))

(deftest fsm-has-spec-basis
  (let [sb (facts/spec-basis "FSM")]
    (is (= 2 (count sb)))
    (is (every? #(str/starts-with? (:statute/url %) "https://") sb))
    (is (every? :statute/law-number sb))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["FSM" "JPN" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["ATL" "JPN"] (:missing-jurisdictions c)))))

(deftest by-topic-filters
  (is (= ["fsm.title36-corporations-and-business-associations"]
         (mapv :statute/id (facts/by-topic "FSM" :corporate-governance))))
  (is (= ["fsm.title51-protection-of-resident-workers-act"]
         (mapv :statute/id (facts/by-topic "FSM" :labor))))
  (is (empty? (facts/by-topic "FSM" :data-protection))
      "no data-protection statute located this iteration -- honestly absent")
  (is (empty? (facts/by-topic "ATL" :labor))))
