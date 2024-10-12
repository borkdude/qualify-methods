(ns borkdude.method-qualifier
  (:require [rewrite-clj.zip :as rzip]
            [clojure.tools.analyzer.jvm :as jana]
            [clojure.walk :as w]))

(comment
  (def ana (jana/analyze-ns 'borkdude.test))
  ;; TODO: search for :op :instance-call with :method and :tag
  (def calls (atom []))
  (w/postwalk (fn [m]
                (when (= :instance-call (:op m))
                  (swap! calls conj (-> (select-keys m [:method :class :raw-forms])
                                        (merge (select-keys (:env m) [:line :column :file])))))
                m) ana)
  (-> @calls
      last
      )

  (-> (slurp "src/borkdude/test.clj")
      rzip/of-string)
  )

#_(defn qualify-methods [{:keys [namespaces]}]
  )
