(ns com.tekacs.access
  (:require [oops.core :refer [oget+ oset!+ ocall!+ oapply!+] :as o]
            [cljs-bean.core :refer [bean]])
  (:require-macros [com.tekacs.access]))

(defn- make-optional [k]
  (keyword (str "?" (name k))))

(defn- make-forced [k]
  (keyword (str "!" (name k))))

(defn get+
  ([obj k] (oget+ obj k))
  ([obj k not-found] (com.tekacs.access/some-or (oget+ obj (make-optional k)) not-found)))

(defn get-in+
  ([obj ks] (oget+ obj ks))
  ([obj ks not-found] (com.tekacs.access/some-or (oget+ obj (map make-optional ks)) not-found)))

(defn contains?+ [obj k]
  (some? (get+ obj k nil)))

(defn contains-in?+ [obj ks]
  (some? (get-in+ obj ks nil)))

(defn select-keys+ [obj ks]
  (let [to #js {}]
    (doseq [k ks] (oset!+ to k (oget+ obj k)))
    to))

(defn assoc!+ [obj & keyvals]
  (doseq [[k v] (partition 2 keyvals)] (oset!+ obj (make-forced k) v))
  obj)

(defn assoc-in!+ [obj ks v]
  (oset!+ obj (map make-forced ks) v))

(defn update!+ [obj k f & args]
  (oset!+ obj (make-forced k) (apply f (get+ obj k nil) args)))

(defn update-in!+ [obj ks f & args]
  (oset!+ obj (map make-forced ks) (apply f (get-in+ obj ks nil) args)))

(defn call!+ [obj k & args]
  (apply ocall!+ obj k args))

(defn call-in!+ [obj ks & args]
  (apply ocall!+ obj ks args))

(defn apply!+ [obj k arg-array]
  (oapply!+ obj k arg-array))

(defn apply-in!+ [obj ks arg-array]
  (oapply!+ obj ks arg-array))

(defn extend!
  ([obj] obj)
  ([obj x]
   (let [obj (com.tekacs.access/some-or obj #js {})]
     (when (some? x)
       (doseq [k (js-keys x)]
         (oset!+ obj (str "!" k) (oget+ x k)))
       obj)))
  ([obj x & more]
   (reduce extend! (extend! obj x) more)))

(deftype ^:no-doc JSLookup [obj]
  ILookup
  (-lookup [_ k]
    (get+ obj k))
  (-lookup [_ k not-found]
    (get+ obj k not-found))
  IDeref
  (-deref [_] obj)
  ISeqable
  (-seq [_] (bean obj)))

(defn lookup
  [obj]
  (when obj (JSLookup. obj)))
