//    Text Hunter: User friendly concept extraction from text
//
//    Copyright (C) 2014  Richard Jackson (richgjackson@yahoo.co.uk)
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package uk.ac.kcl.texthunter.core;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author rjackson1
 */
public class DirectoryFilter extends FileFilter {
 
    //Accept curret dir only directories
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
    return false;        
    }

    //The description of this filter
    @Override
    public String getDescription() {
        return "Text Hunter Projects";
    }
}
