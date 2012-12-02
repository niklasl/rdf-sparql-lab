import rdflib
from sys import argv

graph = rdflib.ConjunctiveGraph()
formats = {'ttl': 'turtle',
    'xhtml': 'rdfa', 'html': 'rdfa',
    'rdf': 'xml', 'rdfs': 'xml', 'owl': 'xml'}
query_source, sources = argv[1], argv[2:]
if not sources:
    query_source, sources = None, [query_source]
for i, source in enumerate(sources):
    ext = source.rsplit('.', 1)[-1] if '.' in source else None
    fmt = formats.get(ext) or 'rdfa'
    if i:
        graph.parse(source, rdflib.URIRef(source), format=fmt)
    else:
        graph.default_context.parse(source, format=fmt)
query = open(query_source).read()
print graph.query(query).graph.serialize(format='turtle')
