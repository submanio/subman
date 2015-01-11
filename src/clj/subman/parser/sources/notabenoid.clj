(ns subman.parser.sources.notabenoid
  (:require [clojure.string :as string]
            [net.cgrand.enlive-html :as html]
            [swiss.arrows :refer [-<>>]]
            [subman.helpers :as helpers :refer [defsafe]]
            [subman.const :as const]
            [subman.parser.base :refer [defsource]]))

(def force-lang "russian")

(defn- make-url
  "Make url for notabenoid"
  [end-part]
  (str "http://notabenoid.com" end-part))

(defn- get-release-page-url
  "Get url of release page"
  [page]
  (str (make-url "/search/index/t//cat/1/s_lang/0/t_lang/1/ready/1/gen/1/sort/4/Book_page/")
       page))

(defsafe book-from-line
  "Get translation book from line"
  [line]
  (-> (:attrs line)
      :href
      make-url
      helpers/download-with-url))

(defn- get-book-title
  "Get title from book"
  [book]
  (-> (html/select book [:h1])
      first
      :content
      first
      (string/split #"/")
      first
      string/trim))

(defsafe episode-ready?
  "Check is episode ready from line"
  [line]
  (->> (html/select line [:td.r])
       first
       :content
       first
       (re-find #"^100")))

(defn- get-episode-title-line
  "Get episode title line"
  [line]
  (-> (html/select line [:td.t :a])
      first
      :content
      first))

(defn- get-episode-name
  "Get single episode name from line"
  [title]
  (-> (string/split title #" - ")
      last
      string/trim))

(defn- get-episode-url
  "Get episode url from line"
  [line]
  (-> (html/select line [:td])
      last
      (html/select [:a])
      first
      :attrs
      :href
      make-url))

(defsafe episode-from-line
  "Get episide from line"
  [line]
  (let [title (get-episode-title-line line)
        [season episode] (helpers/get-season-episode title)]
    {:season season
     :episode episode
     :lang force-lang
     :name (get-episode-name title)
     :url (get-episode-url line)
     :version ""}))

(defn- episodes-from-book
  "Get episodes from translation book"
  [book]
  (let [title (get-book-title book)]
    (->> (html/select book [:table#Chapters :tbody :tr])
         (filter episode-ready?)
         (map episode-from-line)
         (remove nil?)
         (map #(assoc % :show title)))))

(defsafe get-htmls-for-parse
  "Get htmls for parsing for subtitles"
  [page]
  (-<>> (get-release-page-url page)
        helpers/fetch
        (html/select <> [:ul.search-results :li :p :a])
        (map book-from-line)))

(defsafe get-subtitles
  "Get subtitles from html"
  [html _]
  (episodes-from-book (helpers/get-from-line html)))

(defsource notabenoid-source
  :type-id const/type-notabenoid
  :get-htmls-for-parse get-htmls-for-parse
  :get-subtitles get-subtitles
  :make-url make-url
  :download-enabled? false)
