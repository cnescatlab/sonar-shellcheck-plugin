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

package fr.cnes.sonar.plugins.shellcheck.languages;

import fr.cnes.sonar.plugins.shellcheck.languages.ShellcheckQualityProfiles;
import org.junit.Assert;
import org.junit.Test;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

public class ShellcheckQualityProfilesTest {

    @Test
    public void test_should_create_sonar_way_profile() {
        ShellcheckQualityProfiles profileDef = new ShellcheckQualityProfiles();
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        profileDef.define(context);
        Assert.assertNotNull(profileDef);
        Assert.assertEquals(3, context.profilesByLanguageAndName().keySet().size());
    }

}
*/
