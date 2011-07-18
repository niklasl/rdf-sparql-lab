#!/usr/bin/env python
from os import path as p, popen
from rdflib import Graph, compare

scriptpath = lambda lpath: p.join(p.dirname(__file__), lpath)

real = Graph().parse(popen("sh %s" % scriptpath("run.sh")), format='n3')
expected = Graph().parse(scriptpath("expected.ttl"), format='n3')

diff = compare.graph_diff(expected, real)
assert not diff[1] and not diff[2], "Expected: %s Got: %s" % tuple(
        g.serialize(format='n3') for g in diff[1:])
print "Ok."

