(ns subman.components.edit-option
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [subman.helpers :refer [value]]))

(defn edit-option
  "Component for editing options"
  [option owner]
  (om/component
   (apply dom/select #js {:value (om/value (:value option))
                          :className "edit-option"
                          :onChange #(om/update! option :value
                                                 (value %))}
          (let [vals (:options option)]
            (for [val (if (:is-sorted option)
                        (sort vals)
                        vals)]
              (dom/option #js {:value (om/value val)}
                          (om/value val)))))))