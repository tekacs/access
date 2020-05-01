(ns com.tekacs.access
  (:refer-clojure :exclude [get get-in contains? select-keys assoc!])
  (:require [cljs.core :as cljs]
            [oops.core :refer [oget oget+ oset! oset!+ ocall! oapply!]]))

(defn- make-optional [k]
  (keyword (str "?" (name k))))

(defn- make-forced [k]
  (keyword (str "!" (name k))))

(defmacro some-or [x y]
  `(let [x# ~x] (if (some? x#) x# ~y)))

(defmacro get
  ([obj k] `(oget ~obj ~k))
  ([obj k not-found] `(some-or (oget ~obj ~(make-optional k)) ~not-found)))

(defmacro get-in
  ([obj ks] `(oget ~obj ~ks))
  ([obj ks not-found] `(some-or (oget ~obj ~(map make-optional ks)) ~not-found)))

(defmacro contains? [obj k]
  `(some? (get ~obj ~k nil)))

(defmacro contains-in? [obj ks]
  `(some? (get-in ~obj ~ks nil)))

(defmacro select-keys [obj ks]
  (let [from (gensym "from")
        to (gensym "to")
        assign-syntax (fn [k] `(oset! ~to ~(make-forced k) (oget ~from ~k)))
        assigns (map assign-syntax ks)]
    `(let [~from ~obj
           ~to (cljs/js-obj)]
       ~@assigns)))

(defmacro assoc! [obj & keyvals]
  (let [to (gensym "to")
        assign-syntax (fn [k v] `(oset! ~to ~k ~v))
        assigns (map (fn [[k v]] (assign-syntax (make-forced k) v)) (partition 2 keyvals))]
    `(let [~to ~obj] ~@assigns ~to)))

(defmacro assoc-in! [obj ks v]
  `(let [to# ~obj] (oset! to# ~(map make-forced ks) ~v)))

(defmacro update! [obj k f & args]
  `(let [to# ~obj] (oset! to# ~(make-forced k) (apply ~f (get to# ~k nil) ~args))))

(defmacro update-in! [obj ks f & args]
  `(let [to# ~obj] (oset! to# ~(map make-forced ks) (apply ~f (get-in to# ~ks nil) ~args))))

(defmacro call! [obj k & args]
  `(ocall! ~obj ~k ~@args))

(defmacro call-in! [obj ks & args]
  `(ocall! ~obj ~ks ~@args))

(defmacro apply! [obj k arg-array]
  `(oapply! ~obj ~k ~arg-array))

(defmacro apply-in! [obj ks & arg-array]
  `(oapply! ~obj ~ks ~arg-array))

(defmacro window! [k & args]
  `(call! js/window ~k ~@args))

(defmacro document! [k & args]
  `(call! js/document ~k ~@args))

(defmacro window
  ([ks] `(window ~ks nil))
  ([ks not-found] `(get-in js/window ~ks ~not-found)))

(defmacro document
  ([ks] `(document ~ks nil))
  ([ks not-found] `(get-in js/document ~ks ~not-found)))
