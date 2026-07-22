(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `citizen-bidder-preference-correct-pct` / `citizen-bidder-preference-
  mismatch?` are the SAME discipline applied to a genuinely Federated
  States of Micronesia-specific mechanism: the Public Contracts Act
  (FSM Code Title 55 Chapter 4, §§402(1), 404(2), 405(4)) own
  CITIZEN-BIDDER PREFERENCE -- a business >= 51% FSM-citizen-owned, FSM
  resident >= 1 year, and having paid FSM gross-revenue taxes for the
  preceding year (§402(1)), plus, for construction projects only, a
  commitment that >= 25% of on-site workers are FSM citizens and >= 25%
  of materials/supplies are purchased within the FSM (§405(4)), is
  entitled to have its bid price REDUCED, for evaluation purposes, by a
  percentage that itself depends on which of FOUR contract-value tiers
  the contract falls into (§404(2)).

  This is a GENUINELY DIFFERENT check SHAPE than every prior iso3166
  sibling this repo mirrors: Central African Republic's Marché réservé
  mechanism is a multi-criterion INCLUSION-ELIGIBILITY test (an OR of
  workforce-composition thresholds and legal-form membership); Bhutan's
  FDI Negative List is a CATEGORICAL SECTOR-EXCLUSION allow-list gate
  (a boolean set-membership read, no numeric output at all); Botswana's
  citizen/resident-preference check is an ORDERED-TIER
  CLASSIFICATION (which of several discrete preference TIERS an
  engagement's ownership/arrangement facts fall into, again a
  classification, not an amount); Estonia's digital-signing-method
  check tests the VALIDITY OF THE FILING'S OWN EXECUTION INSTRUMENT, a
  procedural axis unrelated to price at all. FSM's citizen-bidder
  preference is none of these: it is a NUMERIC LOOKUP-TABLE RECOMPUTE
  -- independently computing a SPECIFIC PERCENTAGE (not a boolean, not
  a discrete tier label, not a pass/fail classification) from the
  engagement's own declared contract type and contract value, gated by
  a FOUR-CRITERION eligibility AND-test (ownership %, residency
  duration, tax-payment history, and -- construction only -- workforce/
  materials composition). It is the first in this family to combine an
  eligibility AND-gate with a genuinely ARITHMETIC value-tiered
  lookup-table output, the same numeric-recompute FAMILY as
  `engagement-fee-matches-claim?` below but applied to a different
  quantity (a statutory preference percentage, not a service fee) with
  a lookup table instead of a linear formula.

  `citizen-bidder-preference-correct-pct` also HONESTLY reproduces a
  real gap in the Public Contracts Act's OWN text rather than smoothing
  it over: §404(2)(b), the $500,000-$1,500,000 bracket, names ONLY
  'construction projects' -- unlike (a) and (c), which each explicitly
  cover both construction and personal-property purchases in their own
  value range. For a personal-property purchase valued at
  $500,000-$1,500,000, this function returns `:undetermined-by-statute`
  rather than guessing 15%, 10%, or 5% -- see `marketentry.facts`'s
  namespace docstring for the full research trail.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real procurement system. It builds the RECORD an
  operator would keep, not the act of submitting a filing itself (that
  is `marketentry.operation`'s `:filing/submit`, always human-gated --
  see README Actuation)."
  (:require [clojure.string :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  (+ (double base-fee)
     (* (double monthly-rate) (double monitoring-months))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (== (double claimed-fee) (compute-engagement-fee engagement)))

(def citizen-bidder-eligibility-thresholds
  "Public Contracts Act (FSM Code Title 55 Chapter 4), §402(1)(b),(d),(e)
  and §405(4), own primary text."
  {:pct-fsm-ownership 0.51
   :pct-citizen-jobsite-workers 0.25
   :pct-domestic-materials 0.25})

(def citizen-bidder-preference-tiers
  "Public Contracts Act §404(2), own primary text, four contract-value
  tiers. `:undetermined-by-statute` (see `citizen-bidder-preference-tier`)
  covers the one combination the statute's own text does not name: a
  personal-property purchase valued $500,000-$1,500,000."
  [{:min 20000    :max 500000    :construction-pct 0.15 :personal-property-pct 0.15}
   {:min 500000   :max 1500000   :construction-pct 0.10 :personal-property-pct :undetermined-by-statute}
   {:min 1500000  :max 10000000  :construction-pct 0.05 :personal-property-pct 0.05}
   {:min 10000000 :max nil       :construction-pct 0.0  :personal-property-pct 0.0}])

(defn citizen-bidder-eligible?
  "The ground-truth citizen-bidder eligibility for `engagement`,
  independently recomputed from its own declared ownership percentage,
  residency, and tax-payment history (§402(1)), plus -- for
  construction projects only -- workforce/materials composition
  (§405(4)). ALL applicable criteria are required (an AND-gate, unlike
  CAF's OR-of-thresholds Marché réservé test). A missing/nil declared
  value on any required branch simply fails eligibility (does not
  throw)."
  [{:keys [contract-type pct-fsm-ownership fsm-resident-1yr?
           paid-fsm-gross-revenue-tax-1yr?
           pct-citizen-jobsite-workers pct-domestic-materials]}]
  (boolean
   (and (some? pct-fsm-ownership)
        (>= (double pct-fsm-ownership) (:pct-fsm-ownership citizen-bidder-eligibility-thresholds))
        (true? fsm-resident-1yr?)
        (true? paid-fsm-gross-revenue-tax-1yr?)
        (if (= contract-type :construction)
          (and (some? pct-citizen-jobsite-workers)
               (>= (double pct-citizen-jobsite-workers) (:pct-citizen-jobsite-workers citizen-bidder-eligibility-thresholds))
               (some? pct-domestic-materials)
               (>= (double pct-domestic-materials) (:pct-domestic-materials citizen-bidder-eligibility-thresholds)))
          true))))

(defn citizen-bidder-preference-tier
  "The §404(2) preference PERCENTAGE for `contract-type` (`:construction`
  or `:personal-property`) and `contract-value`, or
  `:undetermined-by-statute` for the one combination the statute's own
  text does not name (personal-property, $500,000-$1,500,000), or nil
  if `contract-value` falls below every tier's own `:min` (no tier
  applies -- e.g. below the §403 competitive-bidding threshold)."
  [contract-type contract-value]
  (let [v (double contract-value)
        tier (some #(when (and (>= v (:min %)) (or (nil? (:max %)) (< v (:max %)))) %)
              citizen-bidder-preference-tiers)]
    (when tier
      (case contract-type
        :construction (:construction-pct tier)
        :personal-property (:personal-property-pct tier)
        nil))))

(defn citizen-bidder-preference-correct-pct
  "The ground-truth citizen-bidder preference percentage for
  `engagement`, independently recomputed: 0.0 if not eligible
  (`citizen-bidder-eligible?` false -- an ineligible bidder gets no
  preference, full stop); the §404(2) tiered lookup value if eligible
  and the statute's own text names a percentage for this contract-
  type/value combination; `:undetermined-by-statute` if eligible but
  the statute's own text does not name one (see namespace docstring)."
  [{:keys [contract-type contract-value] :as engagement}]
  (if-not (citizen-bidder-eligible? engagement)
    0.0
    (citizen-bidder-preference-tier contract-type contract-value)))

(defn citizen-bidder-preference-mismatch?
  "Does `engagement` declare `:citizen-bidder? true` (i.e. it claims the
  Public Contracts Act's own citizen-bidder bid-price-reduction
  preference) while the INDEPENDENTLY recomputed correct percentage
  either (a) does not equal the engagement's own `:claimed-preference-
  pct`, or (b) is `:undetermined-by-statute` (the statute's own text is
  silent for this exact contract-type/value combination, so a claimed
  preference cannot be validated at all)? A non-citizen-bidder
  engagement is never flagged by this check (entity/engagement-scope-
  gated, the same discipline Bhutan's `:foreign-company?`-gated FDI
  check and CAF's `:reserved-market?`-gated check use)."
  [{:keys [citizen-bidder? claimed-preference-pct] :as engagement}]
  (boolean
   (and citizen-bidder?
        (let [correct (citizen-bidder-preference-correct-pct engagement)]
          (or (= correct :undetermined-by-statute)
              (not (some-> correct double (== (double claimed-preference-pct)))))))))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real procurement
  system."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper-case jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting a filing
  (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper-case jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
