(ns subman.components.search-input-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cemerick.cljs.test :refer-macros [deftest done testing is]]
            [cljs.core.async :refer [timeout <!]]
            [om.core :as om :include-macros true]
            [jayq.core :refer [$]]
            [subman.helpers :as h]
            [subman.components.search-input :refer [search-input]]))

(deftest ^:async test-search-input
  (let [state (atom {:search-query "initial"})]
    (go (let [[owner $el] (<! (h/render-node search-input state))
              input (.find $el ".tt-input")]
          (testing "set initial value"
            (is (= (.val input) "initial")))
          (testing "change app state eager"
            (.val input "test")
            (h/simulate (h/get-by-class owner "search-input") :change)
            (<! (timeout 1000))
            (is (= (:search-query @state) "test")))
          (testing "show back button with query"
            (is (re-find #"back-icon" (.html $el))))
          (testing "clear input on click"
            (h/simulate (h/get-by-class owner "clear-input") :click)
            (<! (timeout 1000))
            (is (= (:search-query @state) "")))
          (testing "show search icon without query"
            (is (re-find #"search-icon" (.html $el))))
          (done)))))
