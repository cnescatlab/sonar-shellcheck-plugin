/*
 * This file is part of sonar-shellcheck-cnes-plugin.
 *
 * sonar-shellcheck-cnes-plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sonar-shellcheck-cnes-plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with sonar-shellcheck-cnes-plugin.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.cnes.sonar.plugins.shellcheck.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Class used to unmarshal shellcheck xml file.
 *
 * It contains an issue or a metric.
 *
 * @author lequal
 */
public class CheckstyleError {

    @XmlAttribute
    public String line;
    @XmlAttribute
    public String column;
    @XmlAttribute
    public String severity;
    @XmlAttribute
    public String message;
    @XmlAttribute
    public String source;

}
