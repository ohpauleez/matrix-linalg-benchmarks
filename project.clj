(let [nar-classifier (str (System/getProperty "os.arch") "-"
                          (System/getProperty "os.name") "-gpp-jni")]
  (defproject benchmarks "0.2.0"
    :description "Benchmarks and comparisons between Neanderthal and other Java matrix libraries."
    :url "https://github.com/uncomplicate/neanderthal/tree/master/examples/benchmarks"
    :license {:name "Eclipse Public License"
              :url "http://www.eclipse.org/legal/epl-v10.html"}

    :jvm-opts ^:replace ["-XX:+UnlockCommercialFeatures" ;"-XX:+FlightRecorder"
                         "-XX:MaxDirectMemorySize=4g"
                         "-XX:+UseLargePages"
                         "-XX:+UseG1GC"
                         "-XX:+AggressiveOpts"];;also replaces lein's default JVM argument TieredStopAtLevel=1
    :aliases {"dumbrepl" ["trampoline" "run" "-m" "clojure.main/main"]
              "bench" ["trampoline" "run" "-m" "benchmarks.core/-main"]}
    :dependencies [[org.clojure/clojure "1.7.0"]
                   [criterium "0.4.3"]
                   [primitive-math "0.1.4"]
                   [net.mikera/core.matrix "0.32.1"]
                   [clatrix "0.4.0"]
                   [net.mikera/vectorz-clj "0.28.0"]
                   [org.jblas/jblas "1.2.3"]
                   [org.nd4j/nd4j-api "0.0.3.5.5.5"]
                   [org.nd4j/nd4j-java "0.0.3.5.5.5"]
                   [uncomplicate/neanderthal "0.2.0"]
                   ;[uncomplicate/neanderthal-atlas "0.1.0" :classifier ~nar-classifier]
                   ;[uncomplicate/neanderthal-atlas "0.1.0" :classifier "amd64-Linux-gpp-jni"]
                   ;; Follow OSX directions here: http://neanderthal.uncomplicate.org/articles/getting_started.html#with-leiningen
                   [uncomplicate/neanderthal-atlas "0.1.0" :classifier "x86_64-MacOSX-gpp-jni"]]))
