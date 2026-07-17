#!/usr/bin/env bb
(ns test-social
  (:require [clojure.test :refer [deftest is run-tests testing]]
            [kenchi.cells.social-post.state-machine :as cell]
            [kenchi.methods.social :as social]))

(deftest dry-run-social-projection
  (let [post (social/draft-observation-post
              "region valuation"
              "Independent public authorities disagree within the reported band."
              ["authority-a" "authority-b"]
              "did:web:kenchi.etzhayyim.com")
        state (cell/transition-to-drafted
               {"subject" "region valuation"
                "sources" ["authority-a" "authority-b"]})]
    (is (= ":dry-run" (get post ":post/status")))
    (is (false? (get post ":post/server-held-key")))
    (is (= cell/phase-drafted (get-in state ["cell_state" "phase"])))
    (is (true? (get-in state ["cell_state" "payload" ":post/non-adjudicating-notice"])))))

(deftest publication-gates
  (testing "source provenance"
    (is (thrown? clojure.lang.ExceptionInfo
                 (social/draft-observation-post "subject" "body" ["one"]))))
  (testing "server keys and live requests refuse"
    (is (= cell/phase-refused
           (get-in (cell/transition-to-drafted
                    {"subject" "x" "sources" ["a" "b"] "server_held_key" true})
                   ["cell_state" "phase"])))
    (is (= cell/phase-refused
           (get-in (cell/transition-to-drafted
                    {"subject" "x" "sources" ["a" "b"] "requested_status" "published"})
                   ["cell_state" "phase"])))
    (is (thrown? clojure.lang.ExceptionInfo (social/build-live {})))))

(let [{:keys [fail error]} (run-tests 'test-social)]
  (System/exit (if (pos? (+ fail error)) 1 0)))
