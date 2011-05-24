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

package org.fracturedatlas.athena.reports.model;


public class GrossComped {
    Integer gross;
    Integer comped;

    public GrossComped(Integer gross, Integer comped) {
        this.gross = gross;
        this.comped = comped;
    }

    public Integer getComped() {
        return comped;
    }

    public void setComped(Integer comped) {
        this.comped = comped;
    }

    public Integer getGross() {
        return gross;
    }

    public void setGross(Integer gross) {
        this.gross = gross;
    }


}
