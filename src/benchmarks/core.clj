(ns benchmarks.core
  (:require [uncomplicate.neanderthal
             [core :refer :all]
             [real :refer :all]
             [math :refer :all]]
            [clojure.core.matrix :as cm]
            [clojure.core.matrix.operators :as cmop]
            [criterium.core :as criterium])
  (:import [org.jblas DoubleMatrix]
           [mikera.matrixx Matrix]
           [mikera.matrixx.algo Multiplications]
           [org.nd4j.linalg.factory Nd4j]
           [org.nd4j.linalg.api.ndarray INDArray]))

(defn rnd ^double [^double x]
  (Math/random))

(defn benchmark-neanderthal-mm [n]
  (let [as (map #(fmap! rnd (dge (pow 2 %) (pow 2 %))) (range 1 n))
        bs (map #(fmap! rnd (dge (pow 2 %) (pow 2 %))) (range 1 n))
        cs (map #(dge (pow 2 %) (pow 2 %)) (range 1 n))]
    (mapv #((:sample-mean (criterium/quick-benchmark (mm! %1 1.0 %2 %3 0.0) {})) 0) cs as bs)))

(defn benchmark-array-sum [n]
  (let [arrays (map #(double-array (* (pow 2 %) (* (pow 2 %)))) (range 1 n))
        sum (fn [a]
              (let [len (alength a)]
                (loop [i 0 sum 0]
                  (if (< i len)
                    (recur (inc i) (+ sum (aget a i)))
                    sum))))]
    (mapv #((:sample-mean (criterium/quick-benchmark (sum %) {})) 0) arrays)))

(defn benchmark-jblas-mmuli [n]
  (let [as (map #(DoubleMatrix/randn (pow 2 %) (pow 2 %)) (range 1 n))
        bs (map #(DoubleMatrix/randn (pow 2 %) (pow 2 %)) (range 1 n))
        cs (map #(DoubleMatrix/zeros (pow 2 %) (pow 2 %)) (range 1 n))]
    (mapv #((:sample-mean (criterium/quick-benchmark (.mmuli ^DoubleMatrix %2 %3 %1) {})) 0)
         as bs cs)))

(defn benchmark-vectorz-* [n]
  (let [as (map #(Matrix/createRandom (pow 2 %) (pow 2 %)) (range 1 n))
        bs (map #(Matrix/createRandom (pow 2 %) (pow 2 %)) (range 1 n))]
    (mapv #((:sample-mean (criterium/quick-benchmark
                          (Multiplications/multiply ^Matrix %1 ^Matrix %2) {}))
           0)
         as bs)))

(defn benchmark-nd4j-mmul [n]
  (let [as (map #(Nd4j/randn (int (pow 2 %)) (int (pow 2 %))) (range 1 n))
        bs (map #(Nd4j/randn (int (pow 2 %)) (int (pow 2 %))) (range 1 n))]
    (mapv #((:sample-mean (criterium/quick-benchmark
                          (.mmul ^INDArray %1 ^INDArray %2) {}))
           0)
         as bs)))

(defn format-scaled [t]
  (let [[s u] (criterium/scale-time t)]
    (format "%.5f %s" (* s t) u)))

;; TODO: Make this take a map and an order of keys
(defn generate-markdown-report [neanderthal-results jblas-results vectorz-results nd4j-results]
  (apply str
         "| Matrix Dimentions | Neanderthal | jBLAS | Vectorz | ND4J | Neanderthal vs ND4J | Neanderthal vs Vectorz |\n"
         "| :-: | :-: | :-: | :-: | :-: | :-: | :-: |\n"
         (map #(format "| %s | %s | %s | %s | %s | %.2f | %.2f |\n" %1 %2 %3 %4 %5 %6 %7)
              (map #(str (long (pow 2 %)) \x (long (pow 2 %))) (range 1 (inc (count neanderthal-results))))
              (map format-scaled neanderthal-results)
              (map format-scaled jblas-results)
              (map format-scaled vectorz-results)
              (map format-scaled nd4j-results)
              (map / nd4j-results neanderthal-results)
              (map / vectorz-results neanderthal-results))))

(defn bench
  ([]
   (bench 13))
  ([n]
   (bench n nil))
  ([n file-str]
   (let [results {:neanderthal (benchmark-neanderthal-mm n)
                  :jblas (benchmark-jblas-mmuli n)
                  :vectorz (benchmark-vectorz-* n)
                  :nd4j (benchmark-nd4j-mmul n)}]
     (when file-str
       (spit file-str
             (generate-markdown-report (:neanderthal results)
                                       (:jblas results)
                                       (:vectorz results)
                                       (:nd4j results))))
     results)))

(defn -main [& args]
  (println "Starting the bench...")
  (bench 8 "results.md")
  (println "\nDone. Results in `results.md`"))

