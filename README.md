# qualify-methods

## What does this tool do?

It rewrites instance method calls in the form `(.length x)` to Clojure 1.12's
fully qualified `(String/.length x)` form, whenever the class of the method can
be inferred using `clojure.tools.analyzer.jvm`.

## Status

Scrappy fiddle. Use at own risk. Improvements welcome via issue and PRs.

## Usage

In `deps.edn` add this library in `:aliases`. You will need to use `:extra-deps`
since this tool runs in the context of your application dependencies.

``` clojure
:aliases {:qualify-methods
           {:extra-deps {io.github.borkdude/qualify-methods
                         {:git/sha "dcffde2ec1c3c4c3b5d7f7ee6d8e17df618ac5a3"}}
            :exec-fn borkdude.qualify-methods/qualify-methods}}
```

Then run with `clj -X:qualify-methods :paths '["src"]'`

## License

MIT
