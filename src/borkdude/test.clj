(ns borkdude.test)

(defn foo [^String x]
  (. x length))
