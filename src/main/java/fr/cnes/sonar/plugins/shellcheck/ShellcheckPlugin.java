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

import fr.cnes.sonar.plugins.shellcheck.check.ShellcheckSensor;
import fr.cnes.sonar.plugins.shellcheck.languages.*;
import fr.cnes.sonar.plugins.shellcheck.rules.ShellcheckRulesDefinition;
import fr.cnes.sonar.plugins.shellcheck.settings.ShellcheckPluginProperties;
import org.sonar.api.Plugin;

/**
 * This class is the entry point for all extensions.
 * 
 * @author lequal
 */
public class ShellcheckPlugin implements Plugin {

	/**
	 * Define all extensions implemented by the plugin.
	 *
	 * @param context SonarQube context.
	 */
	@Override
	public void define(Context context) {
		// Setting plugin Shellcheck
		context.addExtension(ShellLanguage.class);
		context.addExtension(ShellcheckQualityProfiles.class);
		context.addExtensions(ShellcheckPluginProperties.getProperties());

		// Rules definition
		context.addExtension(ShellcheckRulesDefinition.class);
		
		// Sonar scanner extension
		context.addExtension(ShellcheckSensor.class);
	}
}
