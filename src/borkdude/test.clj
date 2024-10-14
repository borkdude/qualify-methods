(ns borkdude.test)

(defn foo [^String x]
  (java.lang.String/.length x))
