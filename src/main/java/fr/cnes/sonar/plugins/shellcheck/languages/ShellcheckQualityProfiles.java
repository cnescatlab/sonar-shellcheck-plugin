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
package fr.cnes.sonar.plugins.shellcheck.languages;

import fr.cnes.sonar.plugins.shellcheck.model.Rule;
import fr.cnes.sonar.plugins.shellcheck.model.RulesDefinition;
import fr.cnes.sonar.plugins.shellcheck.model.XmlHandler;
import fr.cnes.sonar.plugins.shellcheck.rules.ShellcheckRulesDefinition;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import javax.xml.bind.JAXBException;
import java.io.InputStream;

/**
 * Built-in quality profile format since SonarQube 6.6.
 *
 * @author lequal
 */
public final class ShellcheckQualityProfiles implements BuiltInQualityProfilesDefinition {

    /** Logger for this class. **/
    private static final Logger LOGGER = Loggers.get(ShellcheckQualityProfiles.class);

    /** Display name for the built-in quality profile. **/
    private static final String SHELLCHECK_RULES_PROFILE_NAME = "Sonar way";

    /**
     * Allow to create a plugin.
     *
     * @param context Context of the plugin.
     */
    @Override
    public void define(Context context) {
        createBuiltInProfile(context, ShellLanguage.KEY, ShellcheckRulesDefinition.PATH_TO_SHELL_RULES_XML);
    }

    /**
     * Create a built in quality profile for a specific language.
     *
     * @param context SonarQube context in which create the profile.
     * @param language Language key of the associated profile.
     * @param path Path to the xml definition of all rules.
     */
    private void createBuiltInProfile(final Context context, final String language, final String path) {
        // Create a builder for the rules' repository.
        final NewBuiltInQualityProfile defaultProfile =
                context.createBuiltInQualityProfile(SHELLCHECK_RULES_PROFILE_NAME, language);
        try {
            // Retrieve all defined rules.
            final InputStream stream = getClass().getResourceAsStream(path);
            final RulesDefinition rules = (RulesDefinition) XmlHandler.unmarshal(stream, RulesDefinition.class);
            // Activate all ShellCheck rules.
            for(final Rule rule : rules.getRules()) {
                defaultProfile.activateRule(ShellcheckRulesDefinition.getRepositoryKeyForLanguage(language), rule.key);
            }
        } catch (JAXBException e) {
            LOGGER.warn(e.getLocalizedMessage(), e);
        }
        // Save the default profile.
        defaultProfile.setDefault(true);
        defaultProfile.done();
    }
}