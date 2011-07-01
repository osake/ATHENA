/*

ATHENA Project: Management Tools for the Cultural Sector
Copyright (C) 2010, Fractured Atlas

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/

 */
package org.fracturedatlas.athena.apa;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogDocMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NoSuchDirectoryException;
import org.apache.lucene.util.Version;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/*
 * TODO: This should be refactored to use a decorator pattern
 */
public abstract class IndexingApaAdapter extends AbstractApaAdapter {
           
    @Autowired  
    Directory directory;
    
    Analyzer analyzer;
    
    Boolean indexingDisabled = false;
    
    IndexWriterConfig config;
    
    //do not use this directly, call getWriter() instead
    static IndexWriter writer;
    
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public static final Integer DEFAULT_PAGE_SIZE = 10;
    public static final Integer MERGE_FACTOR = 20;
    
    public final static String DOC_TEXT = "text";
    
    //TODO: preserve order of search
    /*
     * Initialize any indexing.  Remember that this method is called BEFORE the
     * directory is injected by spring.  Any operations that depend ont he availability
     * of the index will not work.
     */
    public void initializeIndex() {
        analyzer = new WhitespaceAnalyzer(Version.LUCENE_32);
        config = new IndexWriterConfig(Version.LUCENE_32, analyzer);
        LogMergePolicy mergePolicy = new LogDocMergePolicy();
        mergePolicy.setMergeFactor(MERGE_FACTOR);
        config.setMergePolicy(mergePolicy);

    }
        
    public Boolean rebuildNeeded() {
        Boolean rebuildIndex = false;
        try{
            if(!IndexReader.indexExists(directory)) {
                logger.debug("No index exists");
                rebuildIndex = true;
            }
        } catch (Exception e) {
            logger.debug("Exception while reading index {}", e.getMessage());
            rebuildIndex = true;
        }

        return rebuildIndex;
    }
    
    /*
     * Will not search for record before inserting it into the index.
     * Will create and close its own indexWriter
     */
    public void addAllToIndex(Collection<PTicket> records) {
        if(records == null || records.size() == 0 || indexingDisabled) {
            return;
        }
        
        config = new IndexWriterConfig(Version.LUCENE_32, analyzer);
        
        try{
            IndexWriter indexWriter = getWriter();
            for(PTicket record : records) {
                Document doc = new Document();
                StringBuffer documentText = new StringBuffer();
                doc.add(new Field("_id", record.getIdAsString(), Field.Store.YES, Field.Index.ANALYZED));
                doc.add(new Field("_type", record.getType(), Field.Store.YES, Field.Index.ANALYZED));

                for(String key : record.getProps().keySet()) {
                    List<String> vals = record.getProps().get(key);

                    for(String val : vals) {
                        addToDocument(doc, key, val);
                        documentText.append(val).append(" ");
                    }
                }
                addToDocument(doc, DOC_TEXT, documentText.toString());        
                indexWriter.addDocument(doc);    
            }
        
            indexWriter.optimize();
        } catch (NullPointerException npe) {
            logger.error("Null pointer exception coming.  Did you call initializeIndex() ?");
            npe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /*
     * Will not search for record before inserting it into the index.
     * Will create and close its own indexWriter
     */
    public void addToIndex(PTicket record) {
        if(record == null || record.getId() == null || indexingDisabled) {
            return;
        }
        
        Document doc = new Document();
        StringBuilder documentText = new StringBuilder();
        doc.add(new Field("_id", record.getIdAsString(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("_type", record.getType(), Field.Store.YES, Field.Index.ANALYZED));
        
        for(String key : record.getProps().keySet()) {
            List<String> vals = record.getProps().get(key);
            
            for(String val : vals) {
                addToDocument(doc, key, val);
                documentText.append(val).append(" ");
            }
        }
        addToDocument(doc, DOC_TEXT, documentText.toString());
        
        try{
            IndexWriter indexWriter = getWriter();
            indexWriter.addDocument(doc);
            indexWriter.optimize();
        } catch (NullPointerException npe) {
            logger.error("Null pointer exception coming.  Did you call initializeIndex() ?");
            npe.printStackTrace();
        } catch (IOException e) {
            logger.error("IOException when writing to index {}", e);
            e.printStackTrace();
        }
    }
    
    public void deleteFromIndex(PTicket record) {
        if(record == null || indexingDisabled) {
            return;
        }
        deleteFromIndex(record.getIdAsString());
    }
    
    public void deleteFromIndex(Object oid) {
        if(oid == null || indexingDisabled) {
            return;
        }
        String id = IdAdapter.toString(oid);
        
        IndexWriter indexWriter = null;
        try{
            indexWriter = getWriter();
            indexWriter.deleteDocuments(new Term("_id", id));
            indexWriter.optimize();
        } catch (NullPointerException npe) {
            logger.error("Null pointer exception coming.  Did you call initializeIndex() ?");
            npe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }   
    
    private void cleanup(IndexSearcher searcher, IndexReader reader) {
        if(searcher != null) {
            try{
                searcher.close();
            } catch (IOException ioe) {
                logger.error("Error trying to close index searcher");
                logger.error("{}", ioe.getClass().getName());
                logger.error("{}", ioe.getMessage());
            }
        }
        
        if(reader != null) {
            try{
                reader.close();
            } catch (IOException ioe) {
                logger.error("Error trying to close index writer");
                logger.error("{}", ioe.getClass().getName());
                logger.error("{}", ioe.getMessage());
            }
        }
    }
    
    /*
     * Will search the index for record.id and delete the document if found,
     * then call addtoIndex
     */
    public void updateIndex(PTicket record) {
        if(record == null || record.getId() == null || indexingDisabled) {
            return;
        }
        
        deleteFromIndex(record);
        addToIndex(record);
    }
    
    private void addToDocument(Document doc, String field, String value) {
        doc.add(new Field(field, value, Field.Store.YES, Field.Index.ANALYZED));
    }
    
    /*
     * Returns a set of ids
     * 
     * TODO: Converting from hits to Set<PTicket>
     * isn't preserving order of the results.  
     */
    public Set<Object> searchIndex(AthenaSearch search) {
        Set<Object> ids = new HashSet<Object>();
        
        if(indexingDisabled) {
            return ids;
        }
        
        String query = search.getQuery();
        
        query = query + " AND _type:" + search.getType();
        logger.debug("{}", query);
        
        Integer start = 0;
        if(search.getStart() != null) {
            start = search.getStart();
        };
        
        Integer limit = DEFAULT_PAGE_SIZE;
        if(search.getLimit() != null) {
            limit = search.getLimit();
        }
        Integer numResults = start + limit;
        
        
        try {
            Query q = new QueryParser(Version.LUCENE_32, DOC_TEXT, analyzer).parse(query);
            IndexReader reader = IndexReader.open(getWriter(), false);
            IndexSearcher indexSearcher = new IndexSearcher(reader);
            TopDocs topDocs = indexSearcher.search(q, numResults);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for(int i=start;i<hits.length;++i) {
                int docId = hits[i].doc;
                Document d = indexSearcher.doc(docId);
                ids.add(d.get("_id"));
            }
            reader.close();
            indexSearcher.close();
            return ids;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Directory getDirectory() {
        return directory;
    }
    
    private IndexWriter getWriter() {
        
        if(writer == null) {
            try{
                if(!indexingDisabled) {
                    config = new IndexWriterConfig(Version.LUCENE_32, analyzer);
                    writer = new IndexWriter(directory, config);
                    return writer;
                }
            } catch (NoSuchDirectoryException ioe) {
                logger.error("Could not instantiate an index searcher.  Index does not exist.  Add some items to the index and try again.");
                logger.error(ioe.getMessage());
            } catch (IOException ioe) {
                logger.error("Could not instantiate an index searcher.  Indexing is will now be disabled");
                logger.error(ioe.getMessage());
                logger.error("{}", ioe);
                indexingDisabled = true;
            }
        }
        
        return writer;
    }
    
    private void closeWriter() {
        if(writer != null) {
            try{
                writer.close();
            } catch(IOException ioe) {
                logger.error("Could not close searcher");
                logger.error("{}", ioe);
            }
        }
        
        writer = null;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
        closeWriter();
        if(rebuildNeeded() && !indexingDisabled) {
            logger.info("Rebuilding index");
            Set<String> types = getTypes();
            for(String type : types) {
                AthenaSearch search = new AthenaSearch.Builder().type(type).build();
                Set<PTicket> records = findTickets(search);
                logger.info("Indexing {} records of type {}", records.size(), type);
                long startTime = System.currentTimeMillis();
                addAllToIndex(records);
                long endTime = System.currentTimeMillis();
                logger.info("Done.  Took {} millis", (endTime - startTime));
            }
        } else if (indexingDisabled) {
            logger.info("Indexing is disabled.  Re-enable indexing by setting athena.index.disabled=false in db.properties");
        }
    }
    
    public Boolean getIndexingDisabled() {
        return this.indexingDisabled;
    }

    public void setIndexingDisabled(Boolean indexingDisabled) {
        this.indexingDisabled = indexingDisabled;
    }

    /*
     * This is such a hacky way to do this, but...
     * IF the implementor fat-fingers the value such that is isn't a boolean "yes" or "truee"
     * then Spring is going to choke on the auto-injection.  This method wraps it such that
     * Anything that isn't "true" is going to map to false (adheres to Boolean.parseBoolean())
     */
    public void setIndexingDisabledString(String indexingDisabled) {
        setIndexingDisabled(Boolean.parseBoolean(indexingDisabled));
    }
}
