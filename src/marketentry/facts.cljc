(ns marketentry.facts
  "Per-jurisdiction public-procurement market-entry regulatory catalog
  -- the G2-style spec-basis table the Market-Entry Compliance Governor
  checks every `:jurisdiction/assess` proposal against ('did the advisor
  cite an OFFICIAL public source for this jurisdiction's requirements,
  or did it invent one?').

  Federated States of Micronesia's real market-entry surface (curl/
  WebFetch-verified 2026-07-22; where a page could not be reached, or a
  federal/state question could not be independently confirmed, that is
  stated explicitly rather than silently assumed):

  - **Government procurement is national, not state-split** (this
    iteration specifically checked, rather than assumed by analogy to
    a sibling with a federal/provincial split): the FSM Code's own
    Title 55 (GOVERNMENT FINANCE & CONTRACTS), Chapter 4, is the
    'Public Contracts Act' (PL 2-65, as amended PL 4-4/PL 4-100),
    §§401-418 -- own primary text fetched and read directly from
    `fsmlaw.org` (the FSM Judiciary's own Legal Information System,
    the same site that hosts the FSM Constitution and Supreme Court
    decisions). §403 requires free-and-open competitive SEALED bidding
    for any National Government agency contract involving construction
    projects >= $20,000 or the purchase of personal property >=
    $50,000. §409-411 set qualification/advertisement/bid-opening
    procedure; §417 gives any citizen taxpayer or unsuccessful bidder
    standing to sue in the FSM Supreme Court to enjoin a contract let
    in violation of the chapter.
  - **This iteration separately confirmed the Department of Finance and
    Administration's (DOFA) own CURRENT website states different,
    HIGHER operative thresholds than the 2001-codified statute text**:
    `dofa.gov.fm/services/procurement/` (fetched directly) states
    'construction projects above $50,000 or goods and services above
    $100,000 -- must undergo open competition', and
    `dofa.gov.fm/services/legislation/` (fetched directly) states DOFA's
    work 'is governed by the Financial Management Act of 1979, the
    Financial Management Regulations (as amended in 2021)'. The
    'Financial Management Act of 1979' is independently confirmed as
    FSM Code Title 55 Chapter 2's own short title (§201, own primary
    text: 'This chapter shall be known as the Financial Management Act
    of 1979'). This iteration did NOT independently fetch/read the
    Financial Management Regulations' own primary text (only DOFA's own
    summary of them) -- the exact regulation number/section that
    supersedes the Public Contracts Act's 2001-codified $20,000/$50,000
    thresholds with DOFA's stated $50,000/$100,000 figures is an HONEST
    GAP, not resolved by guessing which figure controls a given filing.
    `:national-spec` below reports BOTH figures, attributed precisely.
  - **CURRENCY CAVEAT, applying to every FSM Code citation in this
    catalog**: `fsmlaw.org`'s own FSM Code introduction page (fetched
    directly) states this is 'an unofficial legislative version of the
    1997 Code ... This version is updated through Public Law 12-12
    (August 19, 2001)'. This iteration could NOT independently confirm
    whether Title 55 Chapter 4, Title 32, Title 36, Title 54, or Title
    51 have been amended by any FSM Congress public law enacted after
    2001 -- the DOFA news items this iteration separately found (a
    2021 Financial Management Regulations amendment, and an ongoing
    2026 National Tax Reform Task Force) are independent confirmation
    that the underlying legal framework HAS continued to evolve past
    2001, so this caveat is a real, live gap, not a formality.
  - **The flagship check this vertical adds** (see
    `marketentry.governor` / `marketentry.registry`) is grounded in a
    mechanism this iteration found directly in the Public Contracts
    Act's own text and confirmed is NOT a delegated/unread number:
    the CITIZEN-BIDDER PREFERENCE. §402(1) defines (post-Trusteeship,
    which terminated in 1986, long before today) a 'citizen bidder' as
    a business that is (b) >= 51% owned by FSM citizens, (d) resident
    in the FSM for >= 1 year immediately prior to bidding, AND (e) has
    paid FSM gross-revenue taxes for the 1-year period immediately
    prior to bidding -- a THREE-CRITERION AND-gate, not a single
    ownership test. §405(4) adds a FOURTH, construction-only
    commitment: >= 25% of on-site workers must be FSM citizens AND >=
    25% of materials/supplies must be purchased within the FSM. §404(2)
    then scales the PRICE REDUCTION given to an eligible citizen bidder
    across FOUR discrete contract-value tiers (own primary text, read
    directly, not delegated to any regulation):
      (a) construction $20,000-$500,000, OR personal property
          $50,000-$500,000 -> 15% bid-price reduction;
      (b) construction $500,000-$1,500,000 -> 10% reduction (the
          statute's own text names ONLY 'construction projects' for
          this bracket -- see the honest gap noted below);
      (c) construction OR personal property $1,500,000-$10,000,000 ->
          5% reduction;
      (d) construction OR personal property >= $10,000,000 -> NO
          preference (0%).
    **This iteration reads §404(2)(b) EXACTLY as drafted and does NOT
    fill a real gap in the statute's own text**: sub-paragraph (b), the
    $500,000-$1,500,000 bracket, names ONLY 'construction projects' --
    unlike (a) and (c), which each explicitly cover BOTH construction
    and personal-property purchases in their own value range, (b) does
    not state a personal-property preference percentage for that
    specific value range at all. This may be a genuine drafting gap in
    the underlying public law (PL 2-65/PL 4-4), or personal-property
    purchases of that scale may simply not have been contemplated when
    it was drafted. `marketentry.registry`'s recompute function returns
    `:undetermined-by-statute` for that one specific combination
    (personal-property, $500,000-$1,500,000) rather than guessing 15%,
    10%, or 5% -- the same honest scope-narrowing discipline this
    family's siblings use for a value delegated to an unread regulation
    (e.g. CAF's Marché réservé threshold, Benin's Art. 77 discretionary
    branch), reapplied here to a gap in the PRIMARY STATUTE TEXT ITSELF
    rather than a delegated instrument.
  - **Foreign investment IS a genuine national/state split** (this
    iteration specifically investigated rather than assumed by
    analogy): the 'Foreign Investment Act of 1997' (FSM Code Title 32
    Chapter 2, §§201-225, PL 10-49) -- own primary text read directly
    -- requires (§204) any noncitizen 'engaging in business' (a broad
    §203(4) definition that explicitly includes holding >= 20%
    ownership interest in a business entity) to hold a currently valid
    FSM Foreign Investment Permit. §205(1) establishes THREE National
    Categories (own text, exact lists, not delegated): Category A
    ('National Red List', closed to foreign investment everywhere in
    the FSM: arms manufacture; minting coins/printing currency notes;
    nuclear power/radioactivity business); Category B ('National Amber
    List', permit issuable only if national criteria in the FSM Foreign
    Investment Regulations are met: banking other than under Title 29;
    insurance); Category C ('National Green List', permit issuable with
    no special criteria: banking as defined in Title 29;
    telecommunications; fishing in the FSM's Exclusive Economic Zone;
    international/interstate air transport; international shipping).
    §205(2) is the DISPOSITIVE federal/state boundary text: 'economic
    sectors that are not of special national significance ... are
    delegated to the jurisdiction of the State Governments ... An
    economic sector included in any of the Categories for National
    Regulation ... shall not appear in any of the Categories for State
    Regulation.' §208 confirms a PARALLEL 'State Foreign Investment
    Permit' process exists under each State's own 'State Foreign
    Investment Legislation and State Foreign Investment Regulations'.
    This is NOT modeled as this vertical's flagship check (the Bhutan
    sibling's own flagship, `marketentry.governor/fdi-sector-restricted-
    violations`, is already a national FDI sector-list check for this
    family, and this iteration did not want to force FSM's genuinely
    richer three-category-plus-jurisdiction-routing mechanism into a
    near-duplicate of a sibling's shape); it is documented here and
    surfaced in `:required-evidence` / `foreign-investment-spec-basis`
    as real, well-grounded catalog content for a foreign-investor
    engagement's evidence checklist.
  - **Corporate registration defaults to national, with a real
    state-opt-in provision this iteration found and could NOT confirm
    any State has exercised**: FSM Code Title 36 (Corporations &
    Business Associations) Chapter 1 §101 gives the President authority
    to grant corporate charters nationally (administered by the
    Registrar of Corporations, Department of Resources and Development,
    Chapter 2 §201). Chapter 2's own §206 ('Authority of the States'),
    read directly, states '(1) Nothing in this chapter may be construed
    as preventing a State from establishing its own process of
    incorporation. (2) Upon application by a State, the President shall
    transfer the function of incorporation to the State for any
    corporate matter not within the exclusive authority of the National
    Government' -- and its own case annotation states 'Corporate
    regulation is governed by national law unless or until the states
    undertake to establish corporate codes of their own' (Mid-Pacific
    Constr. Co. v. Semes, 7 FSM Intrm. 102, 105 (Pon. 1995), as quoted
    verbatim on the same fsmlaw.org page). This iteration confirmed
    Pohnpei State independently maintains its own 2012-edition State
    Code (fetched directly from `fsmlaw.org/pohnpei/code/indexcode.htm`
    -- 'The year 2012 edition of the Pohnpei Code is the most recent
    publication of the codification of the laws of Pohnpei State') but
    did NOT parse that Code's own (large, PDF-only) text to confirm
    whether Pohnpei -- or Chuuk, Kosrae, or Yap -- has actually
    exercised the §206 opt-in to run its own incorporation process, or
    what a STATE's own general (non-import/export/securities/insurance)
    business-licensing requirement looks like. This is an HONEST GAP,
    consistent with this catalog's discipline: `:required-evidence`
    below names the national default and states the gap precisely
    rather than asserting a specific State statute this iteration did
    not read.
  - **Business licensing at the NATIONAL level is narrowly scoped**:
    FSM Code Title 32 Chapter 1 ('Business Licensing', §§101-110, own
    primary text) requires a national license from the Secretary of
    Resources and Development ONLY for importing, exporting, selling of
    securities, or insurance (§101(1)) -- general retail/services
    businesses are not covered by this national license at all. This
    is consistent with (though does not, by itself, prove) general
    business licensing being a State matter, per the Title 36 §206
    pattern above.
  - **Tax filing** (this vertical's conditional evidence check, the
    same shape as sibling actors' DGID/TPN checks, but grounded in a
    genuinely different real mechanism): the Federated States of
    Micronesia Income Tax Law (FSM Code Title 54 Chapter 1, §§111-157,
    own primary text) requires (§131) every employer to withhold
    wage-and-salary tax and (§132) file a quarterly return -- 'to the
    National revenue officer of the State in which the employer has
    his principal place of business, or to the Secretary [of Finance],
    if the employer has no place of business in the Federated States of
    Micronesia' (own text, confirming filing itself runs through
    STATE-level national revenue officers in the ordinary case). This
    iteration did NOT find a separate TIN/'corporate number'
    registration scheme distinct from this filing duty in the primary
    text it read -- `:corporate-number-*` below names the filing duty
    itself, not a fabricated registration-number scheme.
  - This iteration also specifically looked for a representative/
    conflict-of-interest bidder-exclusion provision (the shape Benin's
    Art. 61/62 or Bulgaria's ЗОП Art. 54(2)-(3) document for their own
    laws) in the Public Contracts Act's OWN full text (§§401-418, read
    directly in full, not merely OCR-sampled). None exists in the text
    read -- `rep-spec-basis` is honestly nil for FSM because this
    iteration confirmed its ABSENCE from the primary text, not because
    it failed to look. (§409's disqualification-appeal cross-reference
    to the Administrative Procedures Act, Title 17 Chapter 1, is a
    procedural review right, not a substantive exclusion ground.)

  Coverage is reported HONESTLY (see `coverage`): a jurisdiction not in
  this table has NO spec-basis, full stop -- the advisor must not
  fabricate one, and the governor holds if it tries.")

(def catalog
  "iso3 -> requirement map. `:required-evidence` mirrors the generic
  intake/portal-registration/filing evidence set; `:legal-basis` /
  `:owner-authority` / `:provenance` are the G2 citation the governor
  requires before any `:jurisdiction/assess` proposal can commit. FSM
  deliberately carries NO `:rep-owner-authority` -- see the namespace
  docstring's honest-absence note (this iteration read the Public
  Contracts Act's own full text and confirmed no such provision exists,
  rather than merely failing to find one). `:citizen-bidder-owner-
  authority` / `:citizen-bidder-legal-basis` / `:citizen-bidder-
  criteria` / `:citizen-bidder-provenance` ground this vertical's
  flagship governor check (`citizen-bidder-preference-mismatch?` in
  `marketentry.registry`). `:foreign-investment-owner-authority` /
  `:foreign-investment-legal-basis` / `:foreign-investment-categories`
  / `:foreign-investment-provenance` document the real, richly-
  confirmed Foreign Investment Act national/state split -- surfaced as
  catalog content and evidence-checklist material, deliberately NOT
  built into a second governor check (see namespace docstring)."
  {"FSM" {:name "Federated States of Micronesia"
          :owner-authority "Department of Finance and Administration (DOFA), National Government of the FSM -- administers the Financial Management Act of 1979 (FSM Code Title 55 Chapter 2) framework; each individual National Government agency's own contracting officer lets and awards contracts under the Public Contracts Act's (Title 55 Chapter 4) own procedures"
          :legal-basis "Public Contracts Act, FSM Code Title 55 (Government Finance & Contracts), Chapter 4, §§401-418 (PL 2-65, as amended PL 4-4 § 1-6, PL 4-100 §§1-2) -- own primary text read directly from fsmlaw.org (the FSM Judiciary's Legal Information System). §403 requires free-and-open competitive sealed bidding for National Government agency contracts: construction projects >= $20,000, or purchase of personal property >= $50,000. CURRENCY CAVEAT: fsmlaw.org's own FSM Code introduction states this text is 'updated through Public Law 12-12 (August 19, 2001)' -- this iteration could not independently confirm amendments after 2001."
          :national-spec "Financial Management Act of 1979 (FSM Code Title 55 Chapter 2, §§201-228, own short title at §201) is DOFA's own governing framework. DOFA's OWN current website (dofa.gov.fm/services/procurement/, fetched directly) states DIFFERENT, HIGHER operative thresholds than the 2001-codified statute text this iteration read: 'construction projects above $50,000 or goods and services above $100,000 -- must undergo open competition', attributed by DOFA's own legislation page to 'the Financial Management Regulations (as amended in 2021)' -- this iteration did NOT independently fetch/read that regulation's own primary text (an honest gap on which figure currently controls a given filing; both figures are reported here rather than silently picking one)"
          :provenance "https://www.fsmlaw.org/fsm/code/title55/T55_Ch04.htm ; https://www.fsmlaw.org/fsm/code/title55/T55_Ch02.htm ; https://dofa.gov.fm/services/procurement/ ; https://dofa.gov.fm/services/legislation/"
          :required-evidence ["FSM Foreign Investment Permit record (Foreign Investment Act of 1997, FSM Code Title 32 Chapter 2 §§201-225, Department of Resources and Development), when the engagement declares :foreign-investor? true (see `foreign-investment-spec-basis`)"
                              "Registrar of Corporations charter record (FSM Code Title 36 Chapters 1-2, Department of Resources and Development), OR the relevant State's own incorporation record if that State has exercised its Title 36 §206 opt-in authority -- this iteration could NOT independently confirm whether any of the four States (Chuuk, Kosrae, Pohnpei, Yap) has exercised this opt-in as of today, an honest gap"
                              "Business License record (FSM Code Title 32 Chapter 1 §§101-110, Department of Resources and Development) for import/export/securities-dealing/insurance activity specifically -- this national license does not cover general retail/services business, which this iteration could not confirm is governed by a specific, citable State statute this iteration read"
                              "Citizen-bidder preference eligibility confirmation record, when the engagement declares :citizen-bidder? true"
                              "FSM wage/salary withholding and gross-revenue tax filing record (FSM Code Title 54 Chapter 1 §§131-132, Department of Finance and Administration)"
                              "Authorized-representative confirmation record"]
          :corporate-number-owner-authority "Department of Finance and Administration (DOFA)"
          :corporate-number-legal-basis "Federated States of Micronesia Income Tax Law, FSM Code Title 54 Chapter 1, §131 (own primary text): every employer must deduct/withhold wage-and-salary tax; §132 requires a quarterly return filed 'to the National revenue officer of the State in which the employer has his principal place of business, or to the Secretary, if the employer has no place of business in the Federated States of Micronesia'. This iteration did NOT find a separate TIN/'corporate number' registration scheme distinct from this filing duty in the primary text it read."
          :corporate-number-provenance "https://www.fsmlaw.org/fsm/code/title54/T54_Ch01.htm"
          :citizen-bidder-owner-authority "Each National Government agency's own contracting officer, per the Public Contracts Act's own text (FSM Code Title 55 Chapter 4)"
          :citizen-bidder-legal-basis "Public Contracts Act, FSM Code Title 55 Chapter 4, §§402(1), 404(2), 405(4) (own primary text): a 'citizen bidder' (§402(1)(b),(d),(e)) is a business >= 51% FSM-citizen-owned, FSM-resident >= 1 year, AND having paid FSM gross-revenue taxes for the preceding year; for construction projects, §405(4) additionally requires a commitment that >= 25% of on-site workers are FSM citizens and >= 25% of materials/supplies are purchased within the FSM. §404(2) scales the bid-price reduction across four value tiers: 15% ($20,000-$500,000 construction, or $50,000-$500,000 personal property); 10% ($500,000-$1,500,000 construction -- the statute's own text names ONLY construction for this bracket, see namespace docstring's honest gap note); 5% ($1,500,000-$10,000,000, either type); 0% (>= $10,000,000, either type)"
          :citizen-bidder-criteria {:pct-fsm-ownership-threshold 0.51
                                    :construction-workforce-pct-threshold 0.25
                                    :construction-materials-pct-threshold 0.25
                                    :tiers [{:contract-type :any        :min 20000    :max 500000    :pct 0.15 :note "construction only below $50,000; both types $50,000-$500,000"}
                                            {:contract-type :construction :min 500000   :max 1500000   :pct 0.10 :note "statute's own text names only construction for this bracket"}
                                            {:contract-type :any        :min 1500000  :max 10000000  :pct 0.05}
                                            {:contract-type :any        :min 10000000 :max nil       :pct 0.0}]}
          :citizen-bidder-provenance "https://www.fsmlaw.org/fsm/code/title55/T55_Ch04.htm"
          :foreign-investment-owner-authority "Secretary of the Department of Resources and Development (national Categories A/B/C); responsible State authorities (all other economic sectors, per each State's own State Foreign Investment Legislation)"
          :foreign-investment-legal-basis "Foreign Investment Act of 1997, FSM Code Title 32 Chapter 2, §§201-225 (PL 10-49), own primary text: §204 requires any noncitizen 'engaging in business' (§203(4), including >= 20% ownership interest in a business entity) to hold a currently valid FSM Foreign Investment Permit. §205(1) sets three National categories: Category A 'National Red List' (closed everywhere: arms manufacture; minting coins/printing currency notes; nuclear power/radioactivity business); Category B 'National Amber List' (permit issuable only if national criteria are met: banking other than under Title 29; insurance); Category C 'National Green List' (permit issuable with no special criteria: banking as defined in Title 29; telecommunications; fishing in the FSM's Exclusive Economic Zone; international/interstate air transport; international shipping). §205(2): any economic sector not in Categories A/B/C is delegated to State jurisdiction; §208 confirms a parallel State Foreign Investment Permit process exists under each State's own legislation."
          :foreign-investment-categories {:red #{:arms-manufacture :currency-minting-or-printing :nuclear-or-radioactivity}
                                          :amber #{:banking-other-than-title-29 :insurance}
                                          :green #{:banking-under-title-29 :telecommunications :eez-fishing :international-or-interstate-air-transport :international-shipping}}
          :foreign-investment-provenance "https://www.fsmlaw.org/fsm/code/title32/T32_Ch02.htm"}
   "USA" {:name "United States"
          :owner-authority "U.S. General Services Administration (GSA) / SAM.gov"
          :legal-basis "Federal Acquisition Regulation (FAR); System for Award Management"
          :national-spec "SAM.gov entity registration + NAICS self-certification"
          :provenance "https://sam.gov/"
          :required-evidence ["EIN record"
                              "SAM.gov registration record"
                              "State business registration record"
                              "Authorized-representative record"]}
   "DEU" {:name "Germany"
          :owner-authority "Beschaffungsamt des BMI / e-Vergabe platforms"
          :legal-basis "Gesetz gegen Wettbewerbsbeschränkungen (GWB) / VgV"
          :national-spec "e-Vergabe supplier registration under EU procurement directives"
          :provenance "https://www.evergabe-online.de/"
          :required-evidence ["Handelsregister extract"
                              "e-Vergabe registration record"
                              "USt-IdNr record"
                              "Authorized-representative record"]}})

(defn spec-basis
  "The jurisdiction's requirement map, or nil -- nil means NO spec-basis,
  and the governor must hold any proposal that tries to assess or file
  on it."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report: how many of the requested jurisdictions actually
  have a spec-basis entry. Never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-fsm R0: " (count catalog)
                 " jurisdictions seeded with an official spec-basis. "
                 "This is a starting catalog for market-entry navigation, "
                 "not a survey of all ~194 jurisdictions -- extend "
                 "`marketentry.facts/catalog`, never fabricate a "
                 "jurisdiction's requirements.")})))

(defn required-evidence-satisfied?
  "Does `submitted` (a set/coll of evidence keywords or strings) satisfy
  every evidence item listed for `iso3`? Missing spec-basis -> never
  satisfied."
  [iso3 submitted]
  (when-let [{:keys [required-evidence]} (spec-basis iso3)]
    (let [need (count required-evidence)
          have (count (filter (set submitted) required-evidence))]
      (= need have))))

(defn evidence-checklist [iso3]
  (:required-evidence (spec-basis iso3) []))

(defn rep-spec-basis
  "The jurisdiction's representative-related requirement map, or nil when
  this catalog has no such regime. For FSM this is deliberately nil --
  this iteration read the Public Contracts Act's own full text
  (§§401-418) directly and confirmed no representative/conflict-of-
  interest bidder-exclusion provision exists there, distinct from
  simply not having looked (see namespace docstring)."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:rep-owner-authority sb)
      (select-keys sb [:rep-owner-authority :rep-legal-basis :rep-provenance]))))

(defn corporate-number-spec-basis
  "The jurisdiction's corporate-number / tax-filing regime, or nil."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:corporate-number-owner-authority sb)
      (select-keys sb [:corporate-number-owner-authority
                       :corporate-number-legal-basis
                       :corporate-number-provenance]))))

(defn citizen-bidder-spec-basis
  "The jurisdiction's citizen-bidder preference regime, or nil. For FSM
  this is real and current (subject to the namespace docstring's 2001
  currency caveat) -- the flagship check this vertical adds is grounded
  here (Public Contracts Act, FSM Code Title 55 Chapter 4, §§402(1),
  404(2), 405(4))."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:citizen-bidder-owner-authority sb)
      (select-keys sb [:citizen-bidder-owner-authority
                       :citizen-bidder-legal-basis
                       :citizen-bidder-criteria
                       :citizen-bidder-provenance]))))

(defn foreign-investment-spec-basis
  "The jurisdiction's foreign-investment permit regime, or nil. For FSM
  this documents a genuinely rich national/state split (Foreign
  Investment Act of 1997, FSM Code Title 32 Chapter 2) -- surfaced as
  catalog/evidence-checklist content, deliberately not built into a
  second governor check (see namespace docstring)."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:foreign-investment-owner-authority sb)
      (select-keys sb [:foreign-investment-owner-authority
                       :foreign-investment-legal-basis
                       :foreign-investment-categories
                       :foreign-investment-provenance]))))
