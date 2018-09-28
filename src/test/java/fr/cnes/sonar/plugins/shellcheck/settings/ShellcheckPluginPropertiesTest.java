/*

*/
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
 *//*

package fr.cnes.sonar.plugins.shellcheck.settings;

import fr.cnes.sonar.plugins.shellcheck.settings.ShellcheckPluginProperties;
import org.junit.Assert;
import org.junit.Test;
import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ShellcheckPluginPropertiesTest {

	@Test
	public void test_plugin_properties_definition() {
		List<PropertyDefinition> actual = ShellcheckPluginProperties.getProperties();
		assertEquals(6, actual.size());
		PropertyDefinition codeSuffix = actual.get(2);
		Assert.assertEquals(ShellcheckPluginProperties.SHELLCHECK_NAME, codeSuffix.category());
		assertEquals(ShellcheckPluginProperties.SHELL_SUFFIX_KEY, codeSuffix.key());
		assertEquals(ShellcheckPluginProperties.SHELL_SUFFIX_DEFAULT, codeSuffix.defaultValue());
		PropertyDefinition reportPath = actual.get(5);
		assertEquals(ShellcheckPluginProperties.SHELLCHECK_NAME, reportPath.category());
		assertEquals(ShellcheckPluginProperties.REPORT_REGEX_KEY, reportPath.key());
		assertEquals(ShellcheckPluginProperties.REPORT_REGEX_DEFAULT, reportPath.defaultValue());
	}

}
*/
