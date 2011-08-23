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
package org.fracturedatlas.athena.apa.indexing;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.apache.lucene.util.Version;

public final class WhitespaceLowerCaseAnalyzer extends Analyzer {
    
    public TokenStream tokenStream(String fieldName, Reader reader) {
        WhitespaceTokenizer whitespaceTokenizer = new WhitespaceTokenizer(Version.LUCENE_32, reader);
        LowerCaseFilter lowerCaseFilter = new LowerCaseFilter(Version.LUCENE_32, whitespaceTokenizer);
        return lowerCaseFilter;
    }
}
