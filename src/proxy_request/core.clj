(ns proxy-request.core
  (:require [ring.util.time :as time]))

(defn- header
  "Returns an updated Ring response with the specified header added."
  [resp name value]
  (assoc-in resp [:headers name] (str value)))

(defn- connection-content-length
  "Backport from ring 1.3.1"
  [resp ^java.net.URLConnection conn]
  (let [content-length (.getContentLength conn)]
    (if (neg? content-length)
      resp
      (header resp "Content-Length" content-length))))

(defn- connection-last-modified
  "Backport from ring 1.3.1"
  [resp ^java.net.URLConnection conn]
  (let [last-modified (.getLastModified conn)]
    (if (zero? last-modified)
      resp
      (header resp "Last-Modified" (time/format-date (java.util.Date. last-modified))))))

(defn- url-response
  "Backport from ring 1.3.1"
  [url]
  (let [conn (.openConnection url)]
    (if-let [stream (.getInputStream conn)]
      (-> {:status  200
           :headers {}
           :body stream}
          (connection-content-length conn)
          (connection-last-modified conn)))))

(defn proxy-request
  [request & [path content-type]]
  (let [index-html-url (:index-html-url config/env)
        uri (:uri request)]
    (try
      (-> index-html-url
          (clojure.java.io/as-url)
          (url-response)
          (#(if (or content-type
                    (not (re-find #"\.([^./\\]+)$" uri)))
              (resp/content-type % (or content-type "text/html"))
              (content-type-response % request))))
      (catch java.io.FileNotFoundException e
        (log/error e "proxy page:" (or path index-html-url))
        {:status 403
         :headers {}}))))