(ns statute.facts
  "General-law compliance catalog for the Federated States of
  Micronesia (FSM) -- extends this repo's existing `marketentry.facts`
  (public-procurement market-entry only, narrow scope) with a second,
  orthogonal catalog of statutes a company operating in this
  jurisdiction must generally track for compliance. Mirrors
  cloud-itonami-iso3166-jpn/-deu/-bgr/-aze/-alb/-arm/-atg/-ben/-btn/-caf's
  `statute.facts` (ADR-2607141700, cloud-itonami-compliance-fact-
  federation).

  Every entry cites an OFFICIAL government-hosted (or, for the FSM
  Code entries, the FSM Judiciary's own Legal Information System
  `fsmlaw.org`, which also hosts the FSM Constitution and Supreme Court
  decisions) URL -- never fabricated.

  - Companies/business-associations law: this iteration specifically
    investigated, rather than assumed by analogy to a sibling with a
    supranational company-law instrument (e.g. CAF's OHADA AUSCGIE),
    whether FSM has its own domestic corporate-law title. It does --
    FSM is NOT a member of any supranational company-law community;
    corporate charters are granted NATIONALLY by the President (FSM
    Code Title 36, CORPORATIONS & BUSINESS ASSOCIATIONS, Chapter 1,
    §101, PL 1-135), administered by a Registrar of Corporations within
    the Department of Resources and Development (Chapter 2, §201).
    Chapter 2's own §206 ('Authority of the States'), read directly,
    states '(1) Nothing in this chapter may be construed as preventing
    a State from establishing its own process of incorporation. (2)
    Upon application by a State, the President shall transfer the
    function of incorporation to the State for any corporate matter not
    within the exclusive authority of the National Government' -- with
    its own case annotation confirming 'Corporate regulation is
    governed by national law unless or until the states undertake to
    establish corporate codes of their own' (Mid-Pacific Constr. Co. v.
    Semes, 7 FSM Intrm. 102, 105 (Pon. 1995), quoted verbatim on the
    same fsmlaw.org page this iteration fetched). This iteration
    confirmed Pohnpei State independently maintains its own 2012-
    edition State Code (fetched directly from
    `fsmlaw.org/pohnpei/code/indexcode.htm`) but did NOT parse that
    Code's own (large, PDF-only) text to confirm whether any State has
    actually exercised the §206 opt-in -- an honest gap, not resolved
    by guessing. `catalog` below cites the NATIONAL default only.
  - Labor law: this iteration specifically searched for FSM's own labor
    statute and confirmed a citation directly. FSM Code Title 51
    (LABOR), Chapter 1, is the 'Protection of Resident Workers Act'
    (§§111-169, own primary text fetched and read directly from
    fsmlaw.org). §113 states its own policy: 'it is essential to a
    balanced and stable economy ... that [FSM] citizen workers be given
    preference in employment ... and that the public interest requires
    that the employment of noncitizen workers ... not impair the wages
    and working conditions of [FSM] workers.' §114 requires resident-
    worker preference; §115 requires equal transportation/lodging
    benefits for resident employees hired alongside nonresident labor
    on government construction contracts. Administered by the Division
    of Labor, Department of Resources and Development (§112(2), own
    definition of 'Chief'). This iteration did NOT locate a separate
    FSM minimum-wage or working-conditions title distinct from this
    resident/nonresident-worker-preference framework -- Title 51's own
    table of contents (fetched directly) shows only two chapters
    (Protection of Resident Workers; Nonresident Workers' Health
    Certificates), and this iteration did not find a Title 51 Chapter 3
    or a separate wage/hours title elsewhere in the FSM Code's own
    table of contents -- an honest scope note, not a claim that no such
    protection exists at the State level (which this iteration did not
    investigate for labor law specifically).
  - CURRENCY CAVEAT (applies to every entry below): fsmlaw.org's own
    FSM Code introduction page (fetched directly) states this
    codification is 'updated through Public Law 12-12 (August 19,
    2001)' -- this iteration could not independently confirm whether
    Title 36 or Title 51 have been amended by any FSM Congress public
    law enacted after 2001.

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries. `:statute/url` + `:statute/law-number`
  are the citation the governor requires before any compliance-fact
  proposal referencing this law can commit. FSM's catalog is smaller
  than some siblings' -- this reflects an honest scope limit (no
  separate minimum-wage/working-conditions title was located, and
  whether any State has exercised its own Title 36 §206 incorporation
  opt-in could not be independently confirmed this iteration, see
  namespace docstring), not a design choice to omit them."
  {"FSM"
   [{:statute/id "fsm.title36-corporations-and-business-associations"
     :statute/title "Corporations and Business Associations, FSM Code Title 36, Chapter 1 (General Provisions) and Chapter 2 (Registrar of Corporations)"
     :statute/jurisdiction "FSM"
     :statute/kind :law
     :statute/law-number "PL 1-135 (Chapter 1, own short-title provenance at §101); Chapter 2 Registrar of Corporations established by PL 1-135 § 6 (§201). Own text read directly at fsmlaw.org; codification current only through Public Law 12-12 (August 19, 2001) per fsmlaw.org's own FSM Code introduction -- amendments after 2001 not independently verified this iteration."
     :statute/url "https://www.fsmlaw.org/fsm/code/title36/T36_Ch01.htm"
     :statute/url-provenance :official-fsm-judiciary-lis
     :statute/enacted-date nil
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:corporate-governance :incorporation}}
    {:statute/id "fsm.title51-protection-of-resident-workers-act"
     :statute/title "Protection of Resident Workers Act, FSM Code Title 51, Chapter 1"
     :statute/jurisdiction "FSM"
     :statute/kind :law
     :statute/law-number "COM PL 3C-44 (own short title at §111). Own text read directly at fsmlaw.org; codification current only through Public Law 12-12 (August 19, 2001) per fsmlaw.org's own FSM Code introduction -- amendments after 2001 not independently verified this iteration."
     :statute/url "https://www.fsmlaw.org/fsm/code/title51/T51_Ch01.htm"
     :statute/url-provenance :official-fsm-judiciary-lis
     :statute/enacted-date nil
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:labor}}]})

(defn spec-basis
  "The jurisdiction's statute vector, or nil -- nil means NO spec-basis
  for that jurisdiction yet."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report, same shape/discipline as `marketentry.facts/coverage`:
  never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-fsm statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "FSM")) " FSM statute(s) seeded with an "
                 "official citation (a separate minimum-wage/working-conditions "
                 "title was not located, and whether any State has exercised its "
                 "own Title 36 §206 incorporation opt-in could not be "
                 "independently confirmed this iteration -- an honest gap, not "
                 "an omission by design). Extend `statute.facts/catalog`, never "
                 "fabricate a law-id or URL.")})))

(defn by-topic
  "Statutes for `iso3` tagged with `topic` (e.g. :labor, :data-protection)."
  [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))
