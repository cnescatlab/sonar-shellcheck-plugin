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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;

public class ModelTest {

    private CheckstyleReport report;
    private CheckstyleFile file;
    private CheckstyleError error;
        private RulesDefinition rulesDefinition;
    private Rule check;

    @Before
    public void before() {
        report = new CheckstyleReport();

        report.version = "1.0.0";

        file = new CheckstyleFile();
        file.name = "foo.sh";

        error = new CheckstyleError();
        error.source = "WOW";
        error.column = "14";
        error.line = "12";
        error.message = "Test message";

        check = new Rule();
        check.key = "a";
        check.cardinality = "a";
        check.description = "a";
        check.internalKey = "a";
        check.name = "a";
        check.remediationFunction = "a";
        check.remediationFunctionBaseEffort = "a";
        check.severity = "a";
        check.status = "a";
        check.tag = "a";
        check.type = "a";

        rulesDefinition = new RulesDefinition();
    }

    @Test
    public void test_getters() {
        Assert.assertEquals(0, report.getCheckstyleFiles().size());
        Assert.assertEquals(0, file.getChecktyleErrors().size());
        Assert.assertEquals(0, rulesDefinition.getRules().size());
        rulesDefinition.shellcheckRules = new Rule[]{check};
        Assert.assertEquals(1, rulesDefinition.getRules().size());
    }

    @Test
    public void test_unmarshal_from_file() throws JAXBException, URISyntaxException {
        File file = new File(this.getClass().getResource("/rules/shellcheck-rules.xml").toURI());
        RulesDefinition def = (RulesDefinition) XmlHandler.unmarshal(file, RulesDefinition.class);
        Assert.assertEquals(39, def.getRules().size());
    }

    @Test
    public void test_unmarshal_from_stream() throws JAXBException {
        InputStream stream = this.getClass().getResourceAsStream("/rules/shellcheck-rules.xml");
        RulesDefinition def = (RulesDefinition) XmlHandler.unmarshal(stream, RulesDefinition.class);
        Assert.assertEquals(39, def.getRules().size());
    }
}
