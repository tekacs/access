# access [(Clojars)](https://clojars.org/com.tekacs/access)

I loved the Clojure-esque API of [`applied-science.js-interop`](https://github.com/appliedsciencestudio/js-interop)
but I missed the error messages from [`cljs-oops`](https://github.com/binaryage/cljs-oops) and so this came to exist,
a library which provides the API of `js-interop` on top of the `cljs-oops` implementation.

The API is largely the same as that of `js-interop` with one main tweak, which is that it uses some of cljs-oops' nullability behaviour -- if the /target object/ is null, that will cause an exception.

I'll bump the minor version on any changes to the behaviour of these functions.

There are also a few extra methods, such as `(a/document! ...)` (to call methods on `js/document`). Those are not finalized and may be removed in future minor versions.
