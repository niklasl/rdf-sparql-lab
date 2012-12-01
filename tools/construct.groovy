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
import org.openrdf.rio.turtle.TurtleWriter
import org.openrdf.model.Resource

void loadRdf(conn, source, inContext=false) {
    def file = new File(source)
    def format = RDFFormat.forFileName(file.getName())
    def contexts = inContext?
            [conn.valueFactory.createURI(file.toURI().toString())] : []
    file.withInputStream {
        conn.add(it, file.toURI().toString(), format,
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
    args[1..args.length-1].eachWithIndex { src, i ->
        loadRdf(conn, src, i>0)
    }
    def query = new File(args[0]).text
    runQuery(conn, query)
    conn.close()
} finally {
    repo.shutDown()
}

