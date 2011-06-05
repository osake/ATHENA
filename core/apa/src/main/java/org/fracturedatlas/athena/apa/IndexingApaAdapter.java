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
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.fracturedatlas.athena.client.PTicket;

public abstract class IndexingApaAdapter extends AbstractApaAdapter {
    
    Directory directory;
    IndexWriter indexWriter;
    Analyzer analyzer;
    IndexWriterConfig config;
    
    public final static String DOC_TEXT = "text";
    
    
    public void initializeIndex() {
        directory = new RAMDirectory();
        analyzer = new WhitespaceAnalyzer(Version.LUCENE_32);
    }
    
    public void addToIndex(PTicket record) {
        Document doc = new Document();
        StringBuffer documentText = new StringBuffer();
        doc.add(new Field("_id", record.getIdAsString(), Field.Store.YES, Field.Index.NO));
        
        for(String key : record.getProps().keySet()) {
            List<String> vals = record.getProps().get(key);
            
            for(String val : vals) {
                documentText.append(val).append(" ");
            }
        }
        addToDocument(doc, DOC_TEXT, documentText.toString());
        System.out.println(doc);
        
        try{
            config = new IndexWriterConfig(Version.LUCENE_32, analyzer);
            indexWriter = new IndexWriter(directory, config);
            indexWriter.addDocument(doc);
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void addToDocument(Document doc, String field, String value) {
        doc.add(new Field(field, value, Field.Store.YES, Field.Index.ANALYZED));
    }
    
    public void searchIndex(String term) {
        try {
            int hitsPerPage = 10;
            Term luceneTerm = new Term(DOC_TEXT, term);
            TermQuery q = new TermQuery(luceneTerm);
            System.out.println(q);
            IndexSearcher searcher = new IndexSearcher(directory, true);
            TopDocs topDocs = searcher.search(q, 10);
            System.out.println(topDocs.totalHits);
            ScoreDoc[] hits = topDocs.scoreDocs;
            System.out.println(hits.length);
            for(int i=0;i<hits.length;++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                System.out.println((i + 1) + ". " + d.get("_id"));
                System.out.println(d.get(DOC_TEXT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
