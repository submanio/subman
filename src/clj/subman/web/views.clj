(ns subman.web.views
  (:require [clojure.string :refer [blank?]]
            [hiccup.page :refer [html5 include-css include-js xml-declaration]]
            [hiccup.core :refer [html]]
            [cemerick.url :refer [url-encode]]
            [environ.core :refer [env]]
            [subman.helpers :refer [as-static make-static]]
            [subman.models :refer [unique-show-season-episode]]))

(def debug-js
  ["components/es5-shim/es5-shim.js"
   "components/es5-shim/es5-sham.js"
   "components/jquery/dist/jquery.js"
   "components/typeahead.js/dist/typeahead.jquery.js"
   "components/bootstrap-sass-official/assets/javascripts/bootstrap.js"
   "components/react/react.js"
   "cljs-target/goog/base.js"
   "main.js"])

(def production-js
  ["components/es5-shim/es5-shim.js"
   "components/es5-shim/es5-sham.js"
   "components/jquery/dist/jquery.min.js"
   "components/bootstrap-sass-official/assets/javascripts/bootstrap.js"
   "components/typeahead.js/dist/typeahead.jquery.min.js"
   "components/react/react.min.js"
   "main.js"])

(def debug-css
  ["components/typeahead.js-bootstrap3.less/typeahead.css"
   "main.css"])

(def production-css
  ["components/typeahead.js-bootstrap3.less/typeahead.css"
   "main.css"])

(defn get-ga-code
  [ga-id]
  (str "(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
                                    (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
                   m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
        })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

        ga('create', '" ga-id "', 'auto');
        ga('send', 'pageview');"))

(defn is-debug?
  []
  (let [val (env :is-debug)]
    (if (= "false" val)
      false
      val)))

(defn index-page []
  (let [is-debug (is-debug?)
        ga-id (env :ga-id)]
    (html5 [:head
            [:link {:rel "icon"
                    :type "image/png"
                    :href (first (make-static "favicon.png"))}]
            [:link {:type "application/opensearchdescription+xml"
                    :rel "search"
                    :href (first (make-static "opensearch.xml"))}]
            [:meta {:name "fragment"
                    :content "!"}]
            [:title "Subman - subtitle search service"]
            (apply as-static include-css (if is-debug
                                           debug-css
                                           production-css))
            (apply as-static include-js (if is-debug
                                          debug-js
                                          production-js))
            (when is-debug
              [:script "goog.require('subman.core');"])]
           [:body [:div#main]
            [:script "subman.core.run();"]
            [:script (get-ga-code ga-id)]])))

(defn sitemap-page [n]
  (html (xml-declaration "utf-8")
        [:urlset {:xmlns "http://www.sitemaps.org/schemas/sitemap/0.9"}
         (for [[show season episode] (nth @unique-show-season-episode n [])]
           [:url [:loc (str (env :site-url) "search/"
                            (url-encode (str show
                                             (if (not (and (blank? season)
                                                           (blank? episode)))
                                               (str " "
                                                    (if (not (blank? season))
                                                      (str "S" season) "")
                                                    (if (not (blank? episode))
                                                      (str "E" episode) ""))
                                               ""))))]])]))

(defn robots-page []
  (apply str
         "User-agent: *\n"
         "Allow: /\n"
         (for [n (range (count @unique-show-season-episode))]
           (str "Sitemap: " (env :site-url) "sitemap." n ".xml\n"))))
