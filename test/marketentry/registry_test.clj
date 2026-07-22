(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 860000.0}]
    (is (== 860000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "FSM" 0)
        s (registry/register-submit "eng-1" "FSM" 0)]
    (is (= "FSM-DFT-000000" (get d "draft_number")))
    (is (= "FSM-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "FSM" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest citizen-bidder-eligible-ownership-residency-tax-gate
  (testing "all three §402(1) criteria required for a personal-property purchase (no construction workforce/materials branch)"
    (is (true? (registry/citizen-bidder-eligible?
                {:contract-type :personal-property
                 :pct-fsm-ownership 0.51 :fsm-resident-1yr? true
                 :paid-fsm-gross-revenue-tax-1yr? true})))
    (is (false? (registry/citizen-bidder-eligible?
                {:contract-type :personal-property
                 :pct-fsm-ownership 0.50 :fsm-resident-1yr? true
                 :paid-fsm-gross-revenue-tax-1yr? true}))
        "ownership below 51% threshold fails")
    (is (false? (registry/citizen-bidder-eligible?
                {:contract-type :personal-property
                 :pct-fsm-ownership 0.60 :fsm-resident-1yr? false
                 :paid-fsm-gross-revenue-tax-1yr? true}))
        "missing residency fails even with sufficient ownership")
    (is (false? (registry/citizen-bidder-eligible? {:contract-type :personal-property})))))

(deftest citizen-bidder-eligible-construction-adds-workforce-and-materials-gate
  (testing "construction projects ALSO require >=25% citizen jobsite workers AND >=25% domestic materials (§405(4))"
    (is (true? (registry/citizen-bidder-eligible?
                {:contract-type :construction
                 :pct-fsm-ownership 0.51 :fsm-resident-1yr? true
                 :paid-fsm-gross-revenue-tax-1yr? true
                 :pct-citizen-jobsite-workers 0.25 :pct-domestic-materials 0.25})))
    (is (false? (registry/citizen-bidder-eligible?
                {:contract-type :construction
                 :pct-fsm-ownership 0.60 :fsm-resident-1yr? true
                 :paid-fsm-gross-revenue-tax-1yr? true
                 :pct-citizen-jobsite-workers 0.10 :pct-domestic-materials 0.05}))
        "ownership/residency/tax all satisfied but workforce/materials below 25% fails")))

(deftest citizen-bidder-preference-tier-lookup
  (testing "15% for $20k-$500k construction or $50k-$500k personal-property"
    (is (= 0.15 (registry/citizen-bidder-preference-tier :construction 300000.0)))
    (is (= 0.15 (registry/citizen-bidder-preference-tier :personal-property 100000.0))))
  (testing "10% construction $500k-$1.5M"
    (is (= 0.10 (registry/citizen-bidder-preference-tier :construction 800000.0))))
  (testing "the statute's own text is silent for personal-property $500k-$1.5M -- honestly undetermined, not guessed"
    (is (= :undetermined-by-statute (registry/citizen-bidder-preference-tier :personal-property 800000.0))))
  (testing "5% for either type $1.5M-$10M"
    (is (= 0.05 (registry/citizen-bidder-preference-tier :construction 2000000.0)))
    (is (= 0.05 (registry/citizen-bidder-preference-tier :personal-property 2000000.0))))
  (testing "0% for either type >= $10M"
    (is (= 0.0 (registry/citizen-bidder-preference-tier :construction 15000000.0)))
    (is (= 0.0 (registry/citizen-bidder-preference-tier :personal-property 15000000.0)))))

(deftest citizen-bidder-preference-correct-pct-ineligible-is-zero
  (is (= 0.0 (registry/citizen-bidder-preference-correct-pct
              {:contract-type :construction :contract-value 300000.0}))
      "no declared eligibility facts at all -> ineligible -> 0.0, never a positive preference"))

(deftest citizen-bidder-preference-mismatch-is-entity-scope-gated
  (testing "an engagement NOT declared :citizen-bidder? is never flagged, even if it would fail eligibility"
    (is (false? (registry/citizen-bidder-preference-mismatch?
                 {:citizen-bidder? false :contract-type :construction :contract-value 300000.0
                  :claimed-preference-pct 0.15}))))
  (testing "a citizen-bidder engagement that fails eligibility but claims a preference -> mismatch"
    (is (true? (registry/citizen-bidder-preference-mismatch?
                {:citizen-bidder? true :contract-type :construction :contract-value 300000.0
                 :pct-fsm-ownership 0.60 :fsm-resident-1yr? true
                 :paid-fsm-gross-revenue-tax-1yr? true
                 :pct-citizen-jobsite-workers 0.10 :pct-domestic-materials 0.05
                 :claimed-preference-pct 0.15}))))
  (testing "a citizen-bidder engagement that IS eligible but claims the WRONG tier percentage -> mismatch"
    (is (true? (registry/citizen-bidder-preference-mismatch?
                {:citizen-bidder? true :contract-type :construction :contract-value 300000.0
                 :pct-fsm-ownership 0.60 :fsm-resident-1yr? true
                 :paid-fsm-gross-revenue-tax-1yr? true
                 :pct-citizen-jobsite-workers 0.30 :pct-domestic-materials 0.30
                 :claimed-preference-pct 0.10}))))
  (testing "a citizen-bidder engagement that IS eligible and claims the CORRECT tier percentage -> not flagged"
    (is (false? (registry/citizen-bidder-preference-mismatch?
                 {:citizen-bidder? true :contract-type :construction :contract-value 300000.0
                  :pct-fsm-ownership 0.60 :fsm-resident-1yr? true
                  :paid-fsm-gross-revenue-tax-1yr? true
                  :pct-citizen-jobsite-workers 0.30 :pct-domestic-materials 0.30
                  :claimed-preference-pct 0.15}))))
  (testing "eligible but the statute's own text is silent for this contract-type/value -> mismatch (cannot validate, not auto-approved)"
    (is (true? (registry/citizen-bidder-preference-mismatch?
                {:citizen-bidder? true :contract-type :personal-property :contract-value 800000.0
                 :pct-fsm-ownership 0.60 :fsm-resident-1yr? true
                 :paid-fsm-gross-revenue-tax-1yr? true
                 :claimed-preference-pct 0.10})))))
