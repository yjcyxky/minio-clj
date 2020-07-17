# minio-clj
Thin Clojure wrapper arround minio-java client library

Minio docs  : #https://docs.minio.io
minio-java API docs : #https://docs.minio.io/docs/java-client-api-reference

## Usage

```
(def conn (connect))  ;;; returns demo connection to public minio server https://play.minio.io:9000 

(make-bucket conn "my.bucket" )  ;; creates a bucket, returns bucket name

(put-object conn "my.bucket" "photo.jpg") ; uploads a file, returns map of {:keys [bucket name]}

(def photo (get-objet conn "my.bucket" "photo.jpg")) ; returns Clojure  IBufferedReader.

;; so you can use it with spit/copy and other Clojure functions that take readers.

(spit "myDownloadedPhoto.jpg" photo)

```

## License

Copyright © 2020 Jingcheng Yang

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
