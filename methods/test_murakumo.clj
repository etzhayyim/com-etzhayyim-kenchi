#!/usr/bin/env bb
(require '[clojure.test :as t]
         'kenchi.murakumo-test)

(let [result (t/run-tests 'kenchi.murakumo-test)]
  (System/exit (if (zero? (+ (:fail result) (:error result))) 0 1)))
