# qualify-methods

Rewrite your instance calls to use fully qualified methods

<img src="https://files.mastodon.social/media_attachments/files/113/305/219/646/283/320/original/68294791bdce3a8c.png">

## What does this tool do?

It rewrites instance method calls in the form `(.length x)` to Clojure 1.12's
fully qualified `(String/.length x)` form, whenever the class of the method can
be inferred using `clojure.tools.analyzer.jvm`. Reasons to do this may include
better IDE support (auto-completions), e.g. when you're using `clj-kondo` via
`clojure-lsp`.

## Status

Scrappy fiddle. Use at own risk. Improvements welcome via issue and PRs.

## Usage

In `deps.edn` add this library in `:aliases`. You will need to use `:extra-deps`
since this tool runs in the context of your application dependencies.

``` clojure
:aliases {:qualify-methods
           {:extra-deps {io.github.borkdude/qualify-methods
                         {:git/sha "<newest-sha>"}}
            :exec-fn borkdude.qualify-methods/qualify-methods}}
```

Then run with `clj -X:qualify-methods :paths '["src"]'`

## TODO

- [ ] Add tests via cognitect test runner
- [ ] Take into account default imports like `String` vs. `java.lang.String` and
      namespace specific imports

## License

MIT
