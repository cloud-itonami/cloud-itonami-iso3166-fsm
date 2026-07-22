# cloud-itonami-iso3166-fsm

**FSM**: Federated States of Micronesia.

- Public Contracts Act (FSM Code Title 55 Chapter 4) public-procurement
  compliance -- Department of Finance and Administration (DOFA)
- Foreign Investment Act of 1997 (Title 32 Chapter 2) + Registrar of
  Corporations (Title 36); citizen-bidder bid-price-reduction
  preference eligibility gate

AGPL-3.0-or-later.

## Market-entry / statute catalogs

Governed public-sector market-entry compliance actor, same architecture
as `cloud-itonami-iso3166-caf`/`-btn`/`-bwa`/`-est`:

- `src/marketentry/{facts,governor,phase,sim,operation,registry,store,
  marketentryllm}.cljc` -- the actor. `facts.cljc` cites the Public
  Contracts Act (FSM Code Title 55 Chapter 4, PL 2-65, §§401-418, own
  primary text read directly from fsmlaw.org, the FSM Judiciary's
  Legal Information System) for national government-contract
  competitive bidding, and separately documents DOFA's own current
  procurement page (`dofa.gov.fm/services/procurement/`) citing
  different, higher operative thresholds under the Financial
  Management Regulations (as amended 2021) -- both figures are
  reported, with an honest gap on which one currently controls a given
  filing (this iteration did not independently fetch the regulation's
  own text). It also documents the Foreign Investment Act of 1997
  (Title 32 Chapter 2) -- a genuinely rich NATIONAL/STATE split (three
  National Categories -- Red/Amber/Green List -- plus a parallel State
  Foreign Investment Permit process for every other economic sector)
  and the Registrar of Corporations (Title 36), which itself carries a
  real, confirmed-but-unexercised State incorporation opt-in (§206).
  `governor.cljc`'s flagship check independently recomputes the Public
  Contracts Act's own CITIZEN-BIDDER PREFERENCE (§§402(1), 404(2),
  405(4)): a four-criterion eligibility AND-gate (>=51% FSM ownership,
  >=1 year FSM residency, FSM gross-revenue tax payment, and -- for
  construction only -- >=25% citizen jobsite workers / >=25% domestic
  materials) gates a bid-price reduction PERCENTAGE independently
  looked up from a four-tier contract-value table -- a numeric
  lookup-table recompute, a check shape genuinely different from every
  other iso3166 sibling this repo mirrors (see the namespace
  docstrings for the full research trail, including the honestly
  preserved gap in §404(2)(b)'s own text for a personal-property
  purchase valued $500,000-$1,500,000, and facts this iteration could
  NOT verify, such as whether any State has exercised its Title 36
  §206 incorporation opt-in).
- `src/statute/facts.cljc` -- general-law catalog: Corporations and
  Business Associations (FSM Code Title 36, national default, PL
  1-135) and the Protection of Resident Workers Act (FSM Code Title 51
  Chapter 1, own primary text). Smaller than some siblings' catalogs
  -- a separate minimum-wage/working-conditions title was not located
  this iteration (an honest gap, not an omission by design; see the
  namespace docstring).

Every citation is curl/WebFetch-verified against an official source
(fsmlaw.org -- the FSM Judiciary's own Legal Information System --
dofa.gov.fm, gov.fm); this catalog's own currency caveat applies
throughout: fsmlaw.org's own FSM Code introduction states its
codification is "updated through Public Law 12-12 (August 19, 2001)",
so any amendment enacted after 2001 is not independently confirmed by
this iteration.

## Culture catalog

This repo carries a **country-level regional-culture catalog**
(ADR-2607171400 addendum 2, `cloud-itonami-municipality-culture-catalog`
Wave 1, in `com-junkawasaki/root`) — national dishes, protected products,
beverages, crafts, festivals and heritage sites for the Federated States
of Micronesia:

- `src/culture/facts.cljc` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
