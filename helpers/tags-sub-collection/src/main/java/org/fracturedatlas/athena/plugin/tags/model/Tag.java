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
package org.fracturedatlas.athena.plugin.tags.model;

public class Tag {
    String text;
    Integer occurrences;

    public Tag() {
        
    }
    
    public Tag(String tagText, Integer occurrences) {
        this.text = tagText;
        this.occurrences = occurrences;
    }

    public Integer getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(Integer occurrences) {
        this.occurrences = occurrences;
    }

    public String getTagText() {
        return text;
    }

    public void setTagText(String tagText) {
        this.text = tagText;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tag other = (Tag) obj;
        if ((this.text == null) ? (other.text != null) : !this.text.equals(other.text)) {
            return false;
        }
        if (this.occurrences != other.occurrences && (this.occurrences == null || !this.occurrences.equals(other.occurrences))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.text != null ? this.text.hashCode() : 0);
        hash = 29 * hash + (this.occurrences != null ? this.occurrences.hashCode() : 0);
        return hash;
    }
    
    
}
