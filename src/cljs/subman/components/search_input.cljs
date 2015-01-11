(ns subman.components.search-input
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [timeout <!]]
            [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]
            [jayq.core :refer [$]]
            [subman.const :as const]
            [subman.autocomplete :refer [get-completion]]
            [subman.helpers :refer [value]]))

(defn completion-source
  "Source for typeahead autocompletion"
  [langs sources query cb]
  (cb (->> (get-completion query langs sources)
           (map #(js-obj "value" %))
           (take const/autocomplete-limit)
           (apply array))))

(defn icon-part
  [app]
  (html [:span.search-icon-box
         (if (= "" (:search-query app))
           [:i.search-icon]
           [:a.clear-input {:on-click (fn [e]
                                            (.preventDefault e)
                                            (om/update! app :search-query ""))
                                :href "#"}
            [:i.back-icon]])]))

(defn input-field-part
  [app]
  (html [:input.search-input
         {:on-change #(om/update! app :search-query (value %))
          :value (:search-query app)
          :placeholder "Type search query"
          :type "text"}]))

(defcomponent search-input [app owner]
  (display-name [_] "Search Input")
  (render [_] (html [:div.search-input-box
                     {:data-spy "affix"
                      :data-offset-top "40"}
                     (icon-part app)
                     (input-field-part app)]))
  (did-mount [_] (let [input (.find ($ (om/get-node owner))
                                    "input.search-input")]
                   (.typeahead input
                               #js {:highlight true}
                               #js {:source #(completion-source
                                              (get-in @app [:options :language :options])
                                              (get-in @app [:options :source :options])
                                              %1 %2)})
                   (.on input "typeahead:closed"
                        #(om/update! app
                                     :search-query (.val input)))
                   (.focus input)
                   (.select input))))
