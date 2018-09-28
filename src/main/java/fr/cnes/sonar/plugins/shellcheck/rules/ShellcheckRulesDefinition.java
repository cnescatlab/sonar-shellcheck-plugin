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
package fr.cnes.sonar.plugins.shellcheck.rules;

import fr.cnes.sonar.plugins.shellcheck.languages.ShellLanguage;
import fr.cnes.sonar.plugins.shellcheck.settings.ShellcheckPluginProperties;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Specific ShellCheck rules definition provided by resource file.
 *
 * @author lequal
 */
public class ShellcheckRulesDefinition implements RulesDefinition {

	/** Partial key for repository. **/
	private static final String REPO_KEY_SUFFIX = "-rules";

	/** Path to xml file in resources tree (shell rules). **/
	public static final String PATH_TO_SHELL_RULES_XML = "/rules/shellcheck-rules.xml";

	/**
	 * Define ShellCheck rules in SonarQube thanks to xml configuration files.
	 *
	 * @param context SonarQube context.
	 */
	@Override
	public void define(Context context) {
		createRepository(context, ShellLanguage.KEY);
	}
	/**
	 * Create repositories for each language.
	 *
	 * @param context SonarQube context.
	 * @param language Key of the language.
	 */
	protected void createRepository(final Context context, final String language) {
		// Create a repository to put rules inside.
		final NewRepository repository = context
                .createRepository(getRepositoryKeyForLanguage(language), language)
                .setName(getRepositoryName());

		// Get XML file describing rules for language.
		final InputStream rulesXml = this.getClass().getResourceAsStream(rulesDefinitionFilePath(language));
		// Add rules in repository.
		if (rulesXml != null) {
			final RulesDefinitionXmlLoader rulesLoader = new RulesDefinitionXmlLoader();
			rulesLoader.load(repository, rulesXml, StandardCharsets.UTF_8.name());
		}
		repository.done();
	}

	/**
     * Getter for repository key.
	 *
	 * @param language Key of the related language.
     * @return A string "language-key".
     */
    public static String getRepositoryKeyForLanguage(final String language) {
        return language + REPO_KEY_SUFFIX;
    }

    /**
     * Getter for repository name.
     *
     * @return A string.
     */
    public static String getRepositoryName() {
        return ShellcheckPluginProperties.SHELLCHECK_NAME;
    }

    /**
     * Getter for the path to rules file.
     *
	 * @param language Key of the language.
     * @return A path in String format.
     */
	public String rulesDefinitionFilePath(final String language) {
		String path = "bad_file";
		switch (language) {
			case ShellLanguage.KEY:
				path = PATH_TO_SHELL_RULES_XML;
				break;
			default:
				break;
		}
		return path;
	}
}

