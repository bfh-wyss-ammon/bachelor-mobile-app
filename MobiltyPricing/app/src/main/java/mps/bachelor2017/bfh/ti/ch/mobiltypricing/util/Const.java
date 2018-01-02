/**
 * Copyright 2018 Pascal Ammon, Gabriel Wyss
 * <p>
 * Implementation eines anonymen Mobility Pricing Systems auf Basis eines Gruppensignaturschemas
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package mps.bachelor2017.bfh.ti.ch.mobiltypricing.util;


public class Const {
    public static final String AuthorityApiUrl = "https://mobilitypricing.ti.bfh.ch:10000/api";
    public static final String ProviderUrl = "https://mobilitypricing.ti.bfh.ch:10001/api";
    public static final String TokenHeader = "x-custom-token";
    public static final String PreferenceKey = "PreferenceKey";
    public static final String HasUserKey = "hasuser";
    public static final String DbTupleStatusField = "status";
    public static final String DbTupleCreatedField = "created";
}
