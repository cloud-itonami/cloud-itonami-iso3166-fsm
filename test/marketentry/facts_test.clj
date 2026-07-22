(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest fsm-has-spec-basis
  (let [sb (facts/spec-basis "FSM")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/corporate-number-spec-basis "FSM")))
    (is (some? (facts/citizen-bidder-spec-basis "FSM")))
    (is (some? (facts/foreign-investment-spec-basis "FSM")))))

(deftest fsm-rep-spec-basis-is-honestly-absent
  (testing "the Public Contracts Act's own full text (§§401-418) was read directly and contains no representative/conflict-of-interest bidder-exclusion provision"
    (is (nil? (facts/rep-spec-basis "FSM")))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "FSM")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "FSM" all)))
    (is (not (facts/required-evidence-satisfied? "FSM" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["FSM" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))

(deftest citizen-bidder-spec-basis-criteria
  (let [cb (facts/citizen-bidder-spec-basis "FSM")]
    (is (= 0.51 (get-in cb [:citizen-bidder-criteria :pct-fsm-ownership-threshold])))
    (is (= 0.25 (get-in cb [:citizen-bidder-criteria :construction-workforce-pct-threshold])))
    (is (= 0.25 (get-in cb [:citizen-bidder-criteria :construction-materials-pct-threshold])))
    (is (= 4 (count (get-in cb [:citizen-bidder-criteria :tiers]))))))

(deftest foreign-investment-spec-basis-categories
  (let [fi (facts/foreign-investment-spec-basis "FSM")]
    (is (contains? (get-in fi [:foreign-investment-categories :red]) :arms-manufacture))
    (is (contains? (get-in fi [:foreign-investment-categories :amber]) :insurance))
    (is (contains? (get-in fi [:foreign-investment-categories :green]) :telecommunications))))
