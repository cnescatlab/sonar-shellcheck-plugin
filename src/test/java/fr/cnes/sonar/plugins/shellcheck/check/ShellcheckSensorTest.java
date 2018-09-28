/* This file is part of sonar-shellcheck-cnes-plugin.
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
 * along with sonar-shellcheck-cnes-plugin.  If not, see <http://www.gnu.org/licenses/>.*/


package fr.cnes.sonar.plugins.shellcheck.check;

import fr.cnes.sonar.plugins.shellcheck.check.ShellcheckSensor;
import fr.cnes.sonar.plugins.shellcheck.model.CheckstyleError;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.internal.MapSettings;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;

public class ShellcheckSensorTest {

    private DefaultFileSystem fs;
    private SensorContextTester context;
    private Map<String, InputFile> files;

    private DefaultInputFile bash_sh;
    private CheckstyleError rule;

    @Before
    public void prepare() throws URISyntaxException {
        fs = new DefaultFileSystem(new File(getClass().getResource("/project").toURI()));

        bash_sh = TestInputFileBuilder.create(
                "ProjectKey",
                fs.resolvePath("bourne.bash").getPath())
                .setLanguage("shellcheck")
                .setType(InputFile.Type.MAIN)
                .setLines(10)
                .setOriginalLineOffsets(new int[]{0,0,0,0,0,0,0,0,0,0})
                .setLastValidOffset(100)
                .setContents("blablabla\nblablabla\nblablabla\nblablabla\nblablabla\nblablabla\nblablabla\nblablabla\nblablabla\n")
                .build();
        fs.add(bash_sh);

        context = SensorContextTester.create(fs.baseDir());
        files = new HashMap<>();
        rule = new CheckstyleError();

        files.put("bourne.bash", bash_sh);
        files.put("clanhb.f", clanhb_f);

        context = SensorContextTester.create(fs.baseDir());
        context.setFileSystem(fs);
        MapSettings settings = new MapSettings();
        settings.setProperty("sonar.shellcheck.reports.path", "");
        context.setSettings(settings);
    }

	@Test
	public void test_given_sensorDescriptor_when_describe_then_callSensorDescriptorName() {
		SensorDescriptor sensorDescriptor = Mockito.mock(SensorDescriptor.class);
		ShellcheckSensor shellcheckMetricsSensor = new ShellcheckSensor();
		shellcheckMetricsSensor.describe(sensorDescriptor);
		verify(sensorDescriptor).name(ShellcheckSensor.class.getName());
	}

	@Test
	public void test_normal_work() {
		final ShellcheckSensor sensor = new ShellcheckSensor();

		sensor.execute(context);

		Assert.assertNotNull(sensor);
		Assert.assertTrue(context.config().hasKey("sonar.shellcheck.reports.path"));
	}

	@Test
    public void test_save_issue() {
        rule.result = new CheckstyleError();
        rule.analysisRuleId = "SH.ERR.Help";
        rule.result.fileName = "bourne.bash";
        rule.result.resultValue = "3";
        rule.result.resultLine = "4";
        rule.result.resultTypePlace = "class";
        rule.result.resultNamePlace = "yolo";
        rule.result.resultId = "11";
        rule.result.resultMessage = "Small file";

        ShellcheckSensor.saveIssue(context, files, rule);
        Assert.assertEquals(1, context.allIssues().size());
    }

    @Test
    public void test_save_issue_with_unknown_file() {
        rule.result = new CheckstyleError();
        rule.analysisRuleId = "SH.ERR.Help";
        rule.result.fileName = "lalalalalala.sh";
        rule.result.resultValue = "3";
        rule.result.resultLine = "110";
        rule.result.resultTypePlace = "class";
        rule.result.resultNamePlace = "yolo";
        rule.result.resultId = "11";
        rule.result.resultMessage = "Small file";

        ShellcheckSensor.saveIssue(context, files, rule);
        Assert.assertEquals(0, context.allIssues().size());
    }

}
*/
