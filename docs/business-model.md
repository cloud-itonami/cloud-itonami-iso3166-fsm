# Business model — FSM

Independent public-sector market-entry compliance for the Federated
States of Micronesia.

- Department of Finance and Administration (DOFA) public procurement --
  Public Contracts Act (FSM Code Title 55 Chapter 4, PL 2-65); DOFA's
  own current procurement page cites different, higher operative
  thresholds under the Financial Management Regulations (as amended
  2021) than the 2001-codified statute text (see
  `src/marketentry/facts.cljc`)
- Foreign Investment Act of 1997 (Title 32 Chapter 2) national/state
  split -- three National Categories (Red/Amber/Green List) plus a
  parallel State Foreign Investment Permit process for every other
  economic sector
- Registrar of Corporations (Title 36 Chapters 1-2), Department of
  Resources and Development -- national by default, with a real,
  unexercised (as far as this iteration could confirm) State
  incorporation opt-in (§206)
- Citizen-bidder preference gate (Public Contracts Act §§402(1), 404(2),
  405(4)) -- independently recomputes a bidder's eligibility (>=51% FSM
  ownership, >=1 year residency, FSM gross-revenue tax payment, and for
  construction, >=25% citizen jobsite workers / >=25% domestic
  materials) and the four-tier bid-price-reduction percentage that
  eligibility entitles it to

## Trust Controls

Any actual portal registration or filing submission requires
Market-Entry Compliance Governor clearance and always escalates to
human sign-off. A false or fabricated regulatory-requirement claim is a
HARD hold.
