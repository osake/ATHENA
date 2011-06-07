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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.id.IdAdapter;
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
    
    IndexWriter indexWriter;
    IndexWriterConfig config;
    
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    
    public final static String DOC_TEXT = "text";
    
    //TODO: add index directory to skeleton structure. Benchmark against old search    
    
    public void initializeIndex() {
//        if(StringUtils.isBlank(indexDirectory)) {
//            logger.error("Index directory was not provided in db.properties:athena.index.directory");
//            logger.error("Indexing is disabled, searches with _q will return []");
//            indexingDisabled = true;
//        } else {
//        
//            try {
//                File f = new File(indexDirectory);
//                directory = new NIOFSDirectory(f);
                analyzer = new WhitespaceAnalyzer(Version.LUCENE_32);
//                logger.debug("Index initialization complete");
//                logger.debug("Index will be kept in {}", indexDirectory);
//            } catch (IOException ioe) {
//                logger.error("Could not open index directory provided in db.properties:athena.index.directory");
//                logger.error("Indexing is disabled, searches with _q will return []");
//                indexingDisabled = true;
//            }
//        }
    }
    
    /*
     * Will not search for record before inserting it into the index
     */
    public void addToIndex(PTicket record) {
        if(record == null || record.getId() == null || indexingDisabled) {
            return;
        }
        
        Document doc = new Document();
        StringBuffer documentText = new StringBuffer();
        doc.add(new Field("_id", record.getIdAsString(), Field.Store.YES, Field.Index.ANALYZED));
        
        for(String key : record.getProps().keySet()) {
            List<String> vals = record.getProps().get(key);
            
            for(String val : vals) {
                addToDocument(doc, key, val);
                documentText.append(val).append(" ");
            }
        }
        addToDocument(doc, DOC_TEXT, documentText.toString());
        
        try{
            config = new IndexWriterConfig(Version.LUCENE_32, analyzer);
            indexWriter = new IndexWriter(directory, config);
            indexWriter.addDocument(doc);
            indexWriter.close();
        } catch (NullPointerException npe) {
            logger.error("Null pointer exception coming.  Did you call initializeIndex() ?");
            npe.printStackTrace();
        } catch (IOException e) {
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
        
        try{
            config = new IndexWriterConfig(Version.LUCENE_32, analyzer);
            indexWriter = new IndexWriter(directory, config);
            indexWriter.deleteDocuments(new Term("_id", id));
            indexWriter.close();
        } catch (NullPointerException npe) {
            logger.error("Null pointer exception coming.  Did you call initializeIndex() ?");
            npe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
     */
    public Set<Object> searchIndex(String queryString) {
        Set<Object> ids = new HashSet<Object>();
        
        if(indexingDisabled) {
            return ids;
        }
        
        try {
            int hitsPerPage = 10;
            Query q = new QueryParser(Version.LUCENE_32, DOC_TEXT, analyzer).parse(queryString);
            IndexSearcher searcher = new IndexSearcher(directory, true);
            TopDocs topDocs = searcher.search(q, 10);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for(int i=0;i<hits.length;++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                ids.add(d.get("_id"));
            }
            return ids;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }
    
    public Boolean getIndexingDisabled() {
        return indexingDisabled;
    }

    public void setIndexingDisabled(Boolean indexingDisabled) {
        this.indexingDisabled = indexingDisabled;
    }
}
