@Grab('org.openrdf.sesame:sesame-repository-api:2.6.10')
@Grab('org.openrdf.sesame:sesame-repository-sail:2.6.10')
@Grab('org.openrdf.sesame:sesame-sail-memory:2.6.10')
@Grab('org.openrdf.sesame:sesame-queryparser-sparql:2.6.10')
@Grab('org.openrdf.sesame:sesame-rio-api:2.6.10')
@Grab('org.openrdf.sesame:sesame-rio-rdfxml:2.6.10')
@Grab('org.openrdf.sesame:sesame-rio-turtle:2.6.10')
@Grab('org.openrdf.sesame:sesame-rio-n3:2.6.10')
@Grab('org.slf4j:slf4j-api:1.5.0')
@Grab('org.slf4j:slf4j-jcl:1.5.0')
import org.openrdf.repository.sail.SailRepository
import org.openrdf.sail.memory.MemoryStore
import org.openrdf.query.QueryLanguage
import org.openrdf.rio.RDFFormat
import org.openrdf.rio.RDFParserRegistry
import org.openrdf.rio.turtle.TurtleWriter
import org.openrdf.model.Resource
@Grab('rdfa:rdfa-sesame:0.1.0-SNAPSHOT')
import rdfa.adapter.sesame.RDFaParserFactory

rdfaParserFactory = new RDFaParserFactory()
RDFParserRegistry.getInstance().add(rdfaParserFactory)

void loadRdf(conn, path, inContext=false) {
    def source = (path =~ /^https?:/)? new URL(path) : new File(path)
    def uri = source.toURI()
    def format = RDFFormat.forFileName(path) ?: rdfaParserFactory.getRDFFormat()
    def contexts = inContext?
            [conn.valueFactory.createURI(uri.toString())] : []
    source.withInputStream {
        conn.add(it, uri.toString(), format,
                contexts as Resource[])
    }
}

def runQuery(conn, String query, inferred=false) {
    def prepQuery = conn.prepareGraphQuery(QueryLanguage.SPARQL, query)
    prepQuery.setIncludeInferred(inferred)
    def writer = new TurtleWriter(System.out)
    prepQuery.evaluate(writer)
}

def repo = new SailRepository(new MemoryStore())
repo.initialize()
try {
    def conn = repo.getConnection()
    args[1..args.length-1].eachWithIndex { path, i ->
        loadRdf(conn, path, i>0)
    }
    def query = new File(args[0]).text
    runQuery(conn, query)
    conn.close()
} finally {
    repo.shutDown()
}
