(ns borkdude.qualify-methods
  (:require [clojure.tools.analyzer.jvm :as jana]
            [clojure.walk :as w]
            [clojure.tools.analyzer.utils]
            [rewrite-clj.zip :as z]
            [clojure.string :as str]
            [rewrite-clj.node :as node]))

(defn analyze-ns [ns calls]
  (let [ana (jana/analyze-ns ns)]
    (w/postwalk
     (fn [m]
       (when (= :instance-call (:op m))
         (swap! calls conj
                (-> (select-keys m [:method :class])
                    (merge (select-keys (clojure.tools.analyzer.utils/source-info (:env m))
                                        [:line :column])))))
       m) ana)))

(defn qualify-methods-of-string [code-string]
  (let [calls (atom [])]
    (loop [loc (-> code-string
                   z/of-string)]
      (if (z/end? loc)
        (str (z/root loc))
        (if (z/list? loc)
          (let [in-list (z/down loc)
                node (-> in-list
                         z/node
                         )
                v (-> node :value)]
            (if (symbol? v)
              (cond
                (= 'ns v)
                (let [ns-name (-> (z/right in-list)
                                  z/node :value)]
                  (try
                    (println "[qualify-methods] Analyzing namespace" (str ns-name))
                    (when-not (= 'borkdude.qualify-methods ns-name)
                      ;; This somehow ends up in a loop?
                      (analyze-ns ns-name calls))
                    (catch Exception _ nil))
                  (recur (z/next loc)))
                (and (str/starts-with? (str v) "." )
                     (> (count (str v)) 1)
                     (not= '.. v))
                (let [{:keys [row col]} (meta (z/node loc))]
                  (if-let [{:keys [method class]}
                             (some (fn [{:keys [line column] :as m}]
                                     (when (and (= line row)
                                                (= column col))
                                       m))
                                   @calls)]
                    (recur (-> (z/replace in-list
                                          (node/coerce (symbol (.getName class)
                                                               (str "." method))))
                               (z/up)))
                    (recur (z/next loc))))
                :else (recur (z/next loc)))
              (recur (z/next loc))))
          (recur (z/next loc)))))))

(defn qualify-methods [{:keys [paths]}]
  (let [paths (map str paths)]
    (doseq [path paths
            file (file-seq (java.io.File. path))]
      (when (str/ends-with? (str file) ".clj")
        (binding [*out* *err*]
          (println "[qualify-methods] Processing file: " (str file)))
        (let [qualified (qualify-methods-of-string (slurp file))]
          (spit file qualified))))
    (binding [*out* *err*]
      (println "[qualify-methods] Done"))))

(comment
  (qualify-methods-of-string (slurp "src/borkdude/test.clj"))
  (qualify-methods {:paths ["src/borkdude/test.clj"]})
  )
