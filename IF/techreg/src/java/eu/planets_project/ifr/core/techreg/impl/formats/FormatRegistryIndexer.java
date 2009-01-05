/**
 * 
 */
package eu.planets_project.ifr.core.techreg.impl.formats;

import java.net.URI;
import java.util.Set;
import java.util.List;
import java.io.IOException;

import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import eu.planets_project.ifr.core.techreg.api.formats.Format;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class FormatRegistryIndexer {
    
    Directory directory;
    
    Analyzer analyzer;
    
    /**
     * @param args
     * @throws IOException
     * @throws ParseException
     */
    public static void main(String[] args) throws IOException, ParseException {
    }
    
    /**
     * no arg constructor, initialisation
     */
    public FormatRegistryIndexer() {
        // Init the index:
        // Store the index in memory:
        directory = new RAMDirectory();
        // To store an index on disk, use this instead:
        //Directory directory = FSDirectory.getDirectory("/tmp/testindex");
    }

    /**
     * 
     * @param fmt
     */
    public void add( Format fmt ) {
        // Add a Format to the index.
        try {
            IndexWriter iwriter = openWriter();
            iwriter.addDocument( formatToDocument(fmt) );
            closeWriter(iwriter);
        } catch ( IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * @param fmts
     */
    public void add( Set<Format> fmts ) {
        
    }

    /**
     * 
     * @param fmt
     * @return
     */
    private Document formatToDocument( Format fmt ) {
        Document doc = new Document();
        String text = "This is the text to be indexed.";
        doc.add(new Field("fieldname", text, Field.Store.YES,
                Field.Index.TOKENIZED));
        return doc;
    }
    
    private IndexWriter openWriter() throws IOException {
        analyzer = new StandardAnalyzer();
        IndexWriter iwriter = new IndexWriter(directory, analyzer, true);
        iwriter.setMaxFieldLength(25000);
        return iwriter;
    }
    
    private void closeWriter( IndexWriter iwriter ) throws IOException {
        iwriter.optimize();
        iwriter.close();
    }

    /**
     * 
     * @param query
     * @return a List of matching format URIs
     */
    public List<URI> search( String query ) {
        // Return the matching Format URIs:
        try {
            // Now search the index:
            IndexSearcher isearcher = new IndexSearcher(directory);
            // Parse a simple query that searches for "text":
            QueryParser parser = new QueryParser("fieldname", analyzer);
            Query lquery = parser.parse(query);
            Hits hits = isearcher.search(lquery);
            // assertEquals(1, hits.length());
            // Iterate through the results:
            for (int i = 0; i < hits.length(); i++) {
                Document hitDoc = hits.doc(i);
                System.out.println("HIT "+hitDoc.get("fieldname"));
                // assertEquals("This is the text to be indexed.", hitDoc.get("fieldname"));
            }
            isearcher.close();
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( ParseException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 
     */
    public void closeIndex() {
        // As this is a in-memory directory, closing it will also forget it forever.
        try {
            directory.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
