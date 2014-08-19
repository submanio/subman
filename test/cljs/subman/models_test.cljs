(ns subman.models-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cemerick.cljs.test :refer-macros [deftest testing is done]]
            [test-sugar.core :refer [is=]]
            [cljs.core.async :refer [<!]]
            [cljs-http.client :as http]
            [subman.deps :as d]
            [subman.models :as m]))

(deftest test-create-search-url
         (testing "with query"
                  (is= "/api/search/?lang=english&source=-1&query=test&offset=0"
                       (m/create-search-url "test" 0 "english" "all")))
         (testing "with query and lang"
                  (is= "/api/search/?lang=ru&source=-1&query=test&offset=0"
                       (m/create-search-url "test :lang ru" 0 "english" "all")))
         (testing "with offset"
                  (is= "/api/search/?lang=english&source=-1&query=test&offset=100"
                       (m/create-search-url "test" 100 "english" "all")))
         (testing "with source"
                  (is= "/api/search/?lang=english&source=0&query=test&offset=0"
                       (m/create-search-url "test :source addicted" 0 "english" "all")))
         (testing "with source and lang"
                  (is= "/api/search/?lang=uk&source=0&query=test&offset=0"
                       (m/create-search-url "test :source addicted :lang uk" 0 "english" "all"))))

(deftest test-get-source-id
         (testing "for source"
                  (is= 0 (m/get-source-id "addicted")))
         (testing "for source in wrong case"
                  (is= 1 (m/get-source-id "podNApisi")))
         (testing "with source = all"
                  (is= -1 (m/get-source-id "all")))
         (testing "with wrong source"
                  (is= -2 (m/get-source-id "wtf-this-source"))))

(deftest ^:async test-get-search-result
         (reset! d/http-get (fn [url]
                              (go (when (= url "/api/search/?lang=uk&source=0&query=test&offset=0")
                                    {:body (prn-str [:test-search-result])}))))
         (go (is (= (<! (m/get-search-result "test :source addicted :lang uk"
                                             0 "english" "all"))
                    [:test-search-result]))
             (done)))