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

package fr.cnes.sonar.plugins.shellcheck;

import fr.cnes.sonar.plugins.shellcheck.ShellcheckPlugin;
import org.junit.Assert;
import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;


public class ShellcheckPluginTest {

	private static final Version VERSION_6_7 = Version.create(6, 7);

	@Test
	public void test_extensions_are_all_set() {
		ShellcheckPlugin shellcheckPlugin = new ShellcheckPlugin();
		SonarRuntime runtime = SonarRuntimeImpl.forSonarQube(VERSION_6_7, SonarQubeSide.SERVER);
		Plugin.Context context = new Plugin.Context(runtime);
        shellcheckPlugin.define(context);
		Assert.assertEquals(13, context.getExtensions().size());
	}

}

