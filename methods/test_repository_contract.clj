#!/usr/bin/env bb
(ns test-repository-contract
  (:require [clojure.edn :as edn]
            [clojure.test :refer [deftest is run-tests]]
            [clojure.java.io :as io]))

(def root (.. (io/file *file*) getCanonicalFile getParentFile getParentFile))

(deftest canonical-repository-shape
  (doseq [path ["manifest.edn" "identity.edn" "dependencies.edn"
                "repository-contracts.edn"]]
    (is (map? (edn/read-string (slurp (io/file root path)))) path))
  (is (not (.exists (io/file root "manifest.jsonld"))))
  (is (map? (edn/read-string (slurp (io/file root "valuation/v1-sources.edn")))))
  (is (= 4 (count (filter #(and (.isFile %) (.endsWith (.getName %) ".edn"))
                          (file-seq (io/file root "lex")))))))

(let [result (run-tests 'test-repository-contract)]
  (System/exit (if (zero? (+ (:fail result) (:error result))) 0 1)))
