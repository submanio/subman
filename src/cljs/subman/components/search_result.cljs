(ns subman.components.search-result
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [subman.const :as const]
            [subman.components.result-entry :refer [result-entry]]))

(defn search-result
  "Component for search displaing all search results"
  [{:keys [search-query results in-progress]} owner]
  (om/component
   (cond
    (pos? (count results)) (apply dom/div {:className "search-result"}
                                  (om/build-all result-entry results))
    in-progress (dom/h2 nil "Searching...")
    :else (dom/h2 nil (str "Nothing found for \"" search-query "\"")))))