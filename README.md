# Matrix and Linear Algebra benchmarks

This is an adaptation of [Neaderthal's benching code](https://github.com/uncomplicate/neanderthal/tree/master/examples/benchmarks).

A quick project to bench matrix multiplication across
various Java and Clojure matrix libraries.

**THIS IS A VERY BAD BENCHMARK** - it doesn't factor in real world scenarios,
and ignores the impact of ecosystem.  This is a micro-benchmark about the
speed of a common individual matrix operation

## Usage

`lein bench` and then view the [results](./results.md).

## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

