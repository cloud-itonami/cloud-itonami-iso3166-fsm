(ns culture.facts
  "Country-level regional-culture catalog for the Federated States of
  Micronesia (FSM) -- national dishes, protected products, beverages,
  crafts, festivals and heritage sites, per ADR-2607171400 addendum 2
  (cloud-itonami-municipality-culture-catalog Wave 1, in
  com-junkawasaki/root). Sibling namespace to `marketentry.facts` /
  `statute.facts` (ADR-2607141700); city-level counterparts live in the
  cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url. (FSM is thinly
  documented: dish candidates whose sources did not confirm an FSM-level
  association were dropped rather than fabricated.)")

(def catalog
  "iso3 -> vector of culture entries."
  {"FSM"
   [{:culture/id "fsm.beverage.sakau"
     :culture/name "Sakau (kava)"
     :culture/country "FSM"
     :culture/kind :beverage
     :culture/summary "In Pohnpei kava is known as sakau, prepared by mixing the root with the fibrous bark of Hibiscus tiliaceus before pressing; Pohnpei is among the Pacific islands where kava was historically cultivated."
     :culture/url "https://en.wikipedia.org/wiki/Kava"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "fsm.craft.rai-stones"
     :culture/name "Rai stones"
     :culture/country "FSM"
     :culture/kind :craft
     :culture/summary "Large limestone disks with central holes manufactured and treasured by the native inhabitants of the Yap islands in Micronesia, used as currency for ceremonial transactions with ownership tracked through oral history."
     :culture/url "https://en.wikipedia.org/wiki/Rai_stones"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "fsm.festival.yap-day"
     :culture/name "Yap Day"
     :culture/country "FSM"
     :culture/kind :festival
     :culture/summary "Legal holiday in Yap State, one of the four states of the Federated States of Micronesia, held annually on 1 March and celebrating traditional Yapese culture with dances and competitions."
     :culture/url "https://en.wikipedia.org/wiki/Yap_Day"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "fsm.heritage.nan-madol"
     :culture/name "Nan Madol"
     :culture/country "FSM"
     :culture/kind :heritage
     :culture/summary "Archaeological site of artificial islands linked by canals on Pohnpei in the Federated States of Micronesia, capital of the Saudeleur dynasty until about 1628; designated a UNESCO World Heritage Site in 2016."
     :culture/url "https://en.wikipedia.org/wiki/Nan_Madol"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-fsm culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "FSM"))
                 " FSM entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
