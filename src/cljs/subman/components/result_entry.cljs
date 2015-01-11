(ns subman.components.result-entry
  (:require [sablono.core :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]
            [clj-di.core :refer-macros [let-deps]]
            [subman.helpers :refer [is-filled? format-season-episode]]))

(defn get-result-entry-title
  "Get formatted title for result entry"
  [{:keys [show name]}]
  (str show (if (is-filled? name)
              (str " - " name)
              "")))

(defn get-result-season-episode
  "Get formatted season-episode of result entry"
  [{:keys [season episode]}]
  (if (some is-filled? [season episode])
    (str " " (format-season-episode season episode))
    ""))

(defn get-result-source
  "Get formatted source of result entry"
  [{:keys [source]}]
  (let-deps [sources :sources]
    (str "Source: " (sources source))))

(defn get-result-lang
  "Get formatted language of result entry"
  [{:keys [lang]}]
  (str "Language: " lang))

(defn get-result-version
  "Get formatted version of result entry"
  [{:keys [version]}]
  (if (is-filled? version)
    (str "Version: " version)
    ""))

(defcomponent result-entry [entry _]
  (display-name [_] "Result Entry")
  (render [_] (html [:a.result-entry
                     {:href (:url entry)
                      :target "_blank"}
                     [:h3 (get-result-entry-title entry)
                      [:span (get-result-season-episode entry)]]
                     [:p.pull-right (get-result-source entry)]
                     [:p (get-result-lang entry)]
                     [:p (get-result-version entry)]])))
