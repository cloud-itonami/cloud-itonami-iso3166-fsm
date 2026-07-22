(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of Federated States of Micronesia (FSM) procurement law,
  whether a claimed engagement fee actually equals base + months x
  rate, whether the engagement's own declared citizen-bidder preference
  claim actually satisfies the Public Contracts Act's own §402(1)/
  §405(4) eligibility criteria and its own §404(2) value-tiered
  percentage, whether an FSM wage/gross-revenue tax filing has been
  verified for a filing that requires it, or when a draft stops being a
  draft and becomes a real-world dofa.gov.fm procurement submission, so
  this MUST be a separate system able to *reject* a proposal and fall
  back to HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints).

  This blueprint's own text (docs/business-model.md Trust Controls:
  'any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off'; 'a false or fabricated regulatory-requirement claim
  is a HARD hold') names exactly the checks below.

  Six checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them. The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `marketentry.phase`: for `:stake
  :actuation/draft-filing`/`:actuation/submit-filing` NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source
                                       (`marketentry.facts`), or invent
                                       one?
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full evidence
                                       checklist on file?
    3. Citizen-bidder preference
       mismatch                     -- for `:filing/submit`, when the
                                       engagement declares
                                       `:citizen-bidder? true` (i.e. it
                                       claims the Public Contracts
                                       Act's own bid-price-reduction
                                       preference), INDEPENDENTLY
                                       recompute whether the
                                       engagement's own declared
                                       ownership percentage, FSM
                                       residency, tax-payment history,
                                       and (for construction) workforce/
                                       materials composition actually
                                       satisfy the Act's own §402(1)/
                                       §405(4) eligibility criteria, AND
                                       whether the claimed preference
                                       percentage matches the Act's own
                                       §404(2) value-tiered lookup
                                       table, and HARD-hold if not (or
                                       if the statute's own text is
                                       silent for this exact contract-
                                       type/value combination -- see
                                       `marketentry.facts`). FLAGSHIP
                                       genuinely new check for the
                                       iso3166 family (grep-verified
                                       absent as a governor check
                                       function name fleet-wide at
                                       build time) -- a NUMERIC
                                       LOOKUP-TABLE RECOMPUTE gated by a
                                       four-criterion eligibility
                                       AND-test, a check SHAPE
                                       genuinely different from every
                                       prior sibling's (OR-of-threshold
                                       inclusion-eligibility / boolean
                                       sector-exclusion / ordered-tier
                                       classification / signing-method
                                       validity) -- the first in this
                                       family to independently recompute
                                       a SPECIFIC PERCENTAGE from a
                                       value-tiered lookup table rather
                                       than a boolean or a classification
                                       label.
    4. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
    5. FSM tax filing unverified    -- for `:filing/submit`, when the
                                       engagement declares
                                       `:requires-fsm-tax-filing?
                                       true`, INDEPENDENTLY check
                                       `:fsm-tax-filing-verified?`.
                                       CONDITIONAL on the engagement's
                                       own ground truth. Grounded in
                                       the Federated States of
                                       Micronesia Income Tax Law (FSM
                                       Code Title 54 Chapter 1, §§131-
                                       132), Department of Finance and
                                       Administration (see
                                       `marketentry.facts`).
    6. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate.

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value)."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real portal package and submitting a real portal
  registration are the two real-world actuation events this actor
  performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence must actually be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(外国投資許可/法人登録/事業免許/市民入札者優遇資格確認/税務申告確認等)が充足していない状態での提案"}]))))

(defn- citizen-bidder-preference-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own declared legal/ownership/workforce facts satisfy the
  Public Contracts Act's own citizen-bidder preference eligibility AND
  value-tiered percentage -- the flagship check this vertical adds.
  HARD-hold when the engagement declares `:citizen-bidder? true` but is
  not independently confirmed eligible for its claimed preference
  percentage."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (registry/citizen-bidder-preference-mismatch? e)
        [{:rule :citizen-bidder-preference-mismatch
          :detail (str subject " は市民入札者優遇(citizen-bidder preference)を宣言しているが、"
                      "独立再計算(FSM市民所有比率51%以上・FSM居住1年以上・FSM総収入税納付歴、"
                      "建設案件の場合は現場労働者25%以上/資材調達25%以上を追加要件とする§402(1)/§405(4)、"
                      "および§404(2)の契約価値別段階的優遇率表)による適格性・優遇率が申告値と一致しない")}]))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                      ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- fsm-tax-filing-unverified-violations
  "For `:filing/submit`, when the engagement declares
  `:requires-fsm-tax-filing? true`, INDEPENDENTLY check
  `:fsm-tax-filing-verified?` -- CONDITIONAL on the engagement's own
  ground truth. Grounded in the Federated States of Micronesia Income
  Tax Law (FSM Code Title 54 Chapter 1, §§131-132), Department of
  Finance and Administration."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:requires-fsm-tax-filing? e))
                 (not (true? (:fsm-tax-filing-verified? e))))
        [{:rule :fsm-tax-filing-unverified
          :detail (str subject " はFSM賃金・総収入税申告(FSM Code Title 54 Chapter 1 §§131-132)の確認を要するが未確認 -- 提出提案は進められない")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (citizen-bidder-preference-mismatch-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (fsm-tax-filing-unverified-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
