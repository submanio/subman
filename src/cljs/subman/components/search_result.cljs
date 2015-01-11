(ns subman.components.search-result
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]
            [subman.components.result-entry :refer [result-entry]]))

(defn search-resul-list
  "Render search result when something found"
  [results]
  (html [:div.search-result-holder
         [:div.search-result-list
          (map-indexed #(om/build result-entry %2
                                  {:react-key (str "search-result-" %1)})
                       results)]]))

(defn info-box
  "Render information box in search result"
  [text]
  (html [:div.search-info
         [:h2 text]]))

(defcomponent search-result [{:keys [stable-search-query results in-progress]} _]
  (display-name [_] "Search Result")
  (render [_] (cond
                (pos? (count results)) (search-resul-list results)
                in-progress (info-box "Searching...")
                :else (info-box (str "Nothing found for \"" stable-search-query "\"")))))
