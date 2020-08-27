(defproject minio-clj "0.2.0"
  :description "Clojure Wrapper around Minio-java client"
  :url "https://github.com/clinico-omics/minio-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [io.minio/minio "7.1.0"]
                 [clojure.java-time "0.3.2"]
                 [org.clojure/data.json "0.2.6"]]
  :repl-options {:init-ns minio-clj.core}

  :repositories [["central" "https://maven.aliyun.com/repository/central"]
                 ["jcenter" "https://maven.aliyun.com/repository/jcenter"]
                 ["clojars" "https://mirrors.tuna.tsinghua.edu.cn/clojars/"]]

  :plugin-repositories [["central" "https://maven.aliyun.com/repository/central"]
                        ["jcenter" "https://maven.aliyun.com/repository/jcenter"]
                        ["clojars" "https://mirrors.tuna.tsinghua.edu.cn/clojars/"]]

  :deploy-repositories [["clojars" {:url "https://clojars.org/repo"
                                    :username :gpg
                                    :password :gpg}]])
