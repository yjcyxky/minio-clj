(ns minio-clj.core
  ^{:author "Martynas Drobulis, Jingcheng Yang<yjcyxky@163.com>"
    :description "Clojure Wrapper around Minio-java client"}
  (:require [java-time :as t]
            [clojure.walk :refer [keywordize-keys]]
            [clojure.data.json :as json]
            [clojure.java.io :as io])
  (:import [io.minio MinioClient]
           [io.minio.messages Item]
           [io.minio.errors ErrorResponseException]))

(defn connect
  [^String url ^String access-key ^String secret-key]
  (new MinioClient url access-key secret-key))

(defn make-bucket
  "Creates a bucket with a name. Does nothing if one exists. Returns nil
   https://docs.minio.io/docs/java-client-api-reference#makeBucket
  "
  [^MinioClient conn ^String name]
  (try
    (.makeBucket conn name)
    (catch ErrorResponseException ex nil))
  name)

(defn list-buckets
  "returns maps "
  [conn]
  (->> (. conn listBuckets)
       (map (fn [bucket] {"CreationDate" (str (.creationDate bucket))
                          "Name" (.name bucket)}))))

(defn UUID []
  (java.util.UUID/randomUUID))

(defn NOW []
  (t/format "yMMd-HHmm"  (t/local-date-time)))

(defn put-object
  "Uploads a file object to the bucket. 
   Returns a map of bucket name and file name
  "
  ([conn ^String bucket ^String file-name]
   (let [upload-name (str  (NOW) "_" (UUID) "_" file-name)]
     (put-object conn bucket upload-name file-name)
     {:bucket bucket
      :name upload-name}))
  ([conn ^String bucket ^String upload-name ^String source-file-name]
   (. conn putObject bucket upload-name source-file-name nil)
   {:bucket bucket
    :name upload-name}))

(defn get-object
  "Takes connection and a map of [bucket name] keys as returned by (put-object) or explicit arguments 
   returns java.io.BufferedReader.
   Use clojure.java.io/copy to stream the bucket data files, or HTTP responses
  "
  ([conn {:keys [bucket name]}]
   (io/reader (.getObject conn bucket name)))
  ([conn bucket name]
   (io/reader (.getObject conn bucket name))))

(defn- objectStat->map
  "helper function for datatype conversion"
  [stat]
  {:bucket (.bucketName stat)
   :name (.name stat)
   :created-time (.createdTime stat)
   :length (.length stat)
   :etag (.etag stat)
   :content-type (.contentType stat)
   :encryption-key nil
   :http-headers (into {} (.httpHeaders stat))})

(defn get-object-meta
  "Returns object metadata as clojure hash-map"
  ([conn bucket name]
   (-> (.statObject conn bucket name)
       objectStat->map
       (assoc  :key name)))
  ([conn {:keys [bucket name]}]
   (-> (.statObject conn bucket name)
       objectStat->map
       (assoc :key name))))

(defn- objectItem->map
  "Helper function for datatye conversion."
  [item]
  {:etag (.etag item)
   :last-modified (.lastModified item)
   :key (.objectName item)
   :owner (.owner item)
   :size (.size item)
   :storage-class (.storageClass item)
   :user-metadata (.userMetadata item)
   :version-id (.versionId item)})

(defn- item->map [^Item item]
  (->> (.get item)
       (objectItem->map)))

(defn list-objects
  ([conn bucket]
   (list-objects conn bucket "" true))
  ([conn bucket filter]
   (list-objects conn bucket filter true))
  ([conn bucket filter recursive]
   (map item->map (.listObjects conn bucket filter recursive false))))

(defn remove-bucket!
  "removes the bucket form the storage"
  [conn bucket-name]
  (.removeBucket conn bucket-name))

(defn remove-object! [conn bucket object]
  (.removeObject conn bucket object))

(defn get-upload-url
  "returns presigned and named upload url for direct upload from the client 
   see docs: https://docs.minio.io/docs/java-client-api-reference#presignedPutObject
  "
  [conn bucket name]
  (.presignedPutObject conn bucket name))

(defn get-download-url
  "returns a temporary download url for this object with 7day expiration
   see docs: https://docs.minio.io/docs/java-client-api-reference#presignedGetObject
  "
  ([conn bucket name]
   (.presignedGetObject conn bucket name))
  ([conn bucket name timeout]
   (.presignedGetObject conn bucket name timeout)))

(defn set-bucket-policy
  "sets bucket policy map, takes Clojure persistant map, serializes it to json
   See JAVA example: https://github.com/minio/minio-java/blob/master/examples/SetBucketPolicy.java
   bucket policy examples: https://docs.aws.amazon.com/AmazonS3/latest/dev/example-bucket-policies.html
  "
  [conn ^clojure.lang.IPersistentMap policy]
  (.setBucketPolicy conn ((json/write-str policy))))
