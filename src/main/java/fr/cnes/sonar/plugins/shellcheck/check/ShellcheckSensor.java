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
package fr.cnes.sonar.plugins.shellcheck.check;

import fr.cnes.sonar.plugins.shellcheck.languages.*;
import fr.cnes.sonar.plugins.shellcheck.model.CheckstyleError;
import fr.cnes.sonar.plugins.shellcheck.model.CheckstyleFile;
import fr.cnes.sonar.plugins.shellcheck.model.CheckstyleReport;
import fr.cnes.sonar.plugins.shellcheck.rules.ShellcheckRulesDefinition;
import fr.cnes.sonar.plugins.shellcheck.settings.ShellcheckPluginProperties;
import fr.cnes.sonar.plugins.shellcheck.model.XmlHandler;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.config.Configuration;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Executed during sonar-scanner call.
 * Execute shellcheck analysis if autolaunch enabled.
 * Import Shellcheck reports (checkstyle formatted) into SonarQube.
 *
 * @author lequal
 */
public class ShellcheckSensor implements Sensor {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Loggers.get(ShellcheckSensor.class);

    /**
     * Give information about this sensor.
     *
     * @param sensorDescriptor Descriptor injected to set the sensor.
     */
    @Override
    public void describe(SensorDescriptor sensorDescriptor) {
        // Prevents sensor to be run during all analysis.
        sensorDescriptor.onlyOnLanguages(ShellLanguage.KEY);

        // Defines sensor name
        sensorDescriptor.name(getClass().getName());

        // Only main files are concerned, not tests.
        sensorDescriptor.onlyOnFileType(InputFile.Type.MAIN);

        // This sensor is activated only if a rule from the following repo is activated.
        sensorDescriptor.createIssuesForRuleRepositories(
                ShellcheckRulesDefinition.getRepositoryKeyForLanguage(ShellLanguage.KEY));
    }

    /**
     * Execute the analysis.
     *
     * @param sensorContext Provide SonarQube services to register results.
     */
    @Override
    public void execute(SensorContext sensorContext) {

        // Represent the file system used for the analysis.
        final FileSystem fileSystem = sensorContext.fileSystem();
        // Represent the configuration used for the analysis.
        final Configuration config = sensorContext.config();
        // Represent the active rules used for the analysis.
        final ActiveRules activeRules = sensorContext.activeRules();
        // run sellcheck execution
        boolean autolaunch = config.getBoolean(ShellcheckPluginProperties.AUTOLAUNCH_PROP_KEY).orElse(Boolean.getBoolean(ShellcheckPluginProperties.AUTOLAUNCH_PROP_DEFAULT));
        if(autolaunch) {
            LOGGER.info("Shellcheck auto-launch enabled.");
            for(final String interpreter : Arrays.asList(ShellLanguage.INTERPRETERS))
                analyse(sensorContext, interpreter);
        }else{
            LOGGER.info("Shellcheck auto-launch disabled.");
        }

        // Report files found in file system and corresponding to SQ property.
        final List<String> reportFiles = getReportFiles(config, fileSystem, autolaunch);

        // If exists, unmarshal each xml result file.
        for(final String reportPath : reportFiles) {
            try {
                // Unmarshall the xml.
                final File file = new File(reportPath);
                final CheckstyleReport checkstyleReport = (CheckstyleReport) XmlHandler.unmarshal(file, CheckstyleReport.class);
                // Retrieve file in a SonarQube format.
                final Map<String, InputFile> scannedFiles = getScannedFiles(fileSystem, checkstyleReport);

                // Handles issues.
                for (final CheckstyleFile checkstyleFile : checkstyleReport.getCheckstyleFiles()) {
                    for(final CheckstyleError checkstyleError : checkstyleFile.getChecktyleErrors()) {

                        if (isRuleActive(activeRules, checkstyleError.source)) { // manage active rules
                            saveIssue(sensorContext, scannedFiles, checkstyleError, checkstyleFile);
                        } else { // log ignored data
                            LOGGER.info(String.format(
                                    "An issue for rule '%s' was detected by Shellcheck but this rule is deactivated in current analysis.",
                                    checkstyleError.source));
                        }
                    }
                }
            } catch (JAXBException e) {
                LOGGER.error(e.getMessage(), e);
                sensorContext.newAnalysisError().message(e.getMessage()).save();
            }
        }

    }

    /**
     * Running an analysis (auto-launch) of files in the directory analyzed and generating results files.
     * @param sensorContext
     * @param interpreter
     */
    private void analyse(SensorContext sensorContext, String interpreter) {
        final Configuration config = sensorContext.config();
        LOGGER.info("Running shellcheck for "+ interpreter);
        //Filtering files
        List<String> projectFiles = new LinkedList<>(Arrays.asList(sensorContext.fileSystem().baseDir().list()));
        for(Iterator<String> iterator = projectFiles.iterator(); iterator.hasNext();){
            final String filePath = iterator.next();
            final String absolutePath = Paths.get(sensorContext.fileSystem().baseDir().getPath(),filePath).toAbsolutePath().toString();
            String fileInterpreter = "";
            LOGGER.debug("Defining interpreter for "+filePath );

            try {
                if(new File(absolutePath).isFile()) {
                    BufferedReader bfr = new BufferedReader(new FileReader(absolutePath));
                    final String shebang = "#!/bin/";
                    boolean stop = false;
                    //Reading file beginning to detect shebang
                    while (bfr.ready() && !stop) {
                        String lineContent = bfr.readLine();
                        LOGGER.debug("Line being read is : " + lineContent);
                        if (!lineContent.isEmpty()) {
                            stop = true;
                            if (lineContent.startsWith(shebang)) {
                                fileInterpreter = lineContent.replace(shebang, "");
                                LOGGER.debug("interpreter found in file : " + fileInterpreter);

                            }
                        }
                    }
                    bfr.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
                sensorContext.newAnalysisError().message(e.getMessage()).save();
            }
            if(fileInterpreter == null || !fileInterpreter.equals(interpreter)){
                iterator.remove();
            }
        }
        if(!projectFiles.isEmpty()) {
            final String executable = "shellcheck";
            final String outputFile = ShellcheckPluginProperties.REPORT_REGEX_DEFAULT.replace(".*",interpreter);
            final String outputPath = Paths.get(sensorContext.fileSystem().baseDir().toString(), outputFile).toString();
            final String outputOption = "-f checkstyle";
            final String interpreterOption = "--shell=" + interpreter;

            projectFiles.replaceAll(filePath -> Paths.get(sensorContext.fileSystem().baseDir().toString(), filePath).toAbsolutePath().toString());

            final String command = String.join(" ", executable, String.join(" ", projectFiles), interpreterOption, outputOption);
            LOGGER.info("running Shellcheck and generating results to " + outputPath);
            LOGGER.debug("command : " + command);
            try {
                final Process shellcheck = Runtime.getRuntime().exec(command);
                int success = shellcheck.waitFor();
                if (success == 0 || success == 1) {
                    LOGGER.info("Auto-launch successfully executed shellcheck");
                    BufferedReader result = new BufferedReader(new InputStreamReader(shellcheck.getInputStream()));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputPath)));
                    String line = "";
                    while ((line = result.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                        LOGGER.debug(line);
                    }
                    writer.close();
                    result.close();
                } else {
                    String line = "";
                    BufferedReader stdError = new BufferedReader(new InputStreamReader(shellcheck.getErrorStream()));
                    while ((line = stdError.readLine()) != null)
                        LOGGER.debug(line);
                    LOGGER.error("Shellcheck auto-launch analysis failed with exit code " + shellcheck.exitValue());
                    stdError.close();
                }
            } catch (InterruptedException | IOException e) {
                LOGGER.error(e.getMessage(), e);
                sensorContext.newAnalysisError().message(e.getMessage()).save();
            }
        }else{
            LOGGER.info("No file using "+interpreter+" interpreter found in project.");
        }


    }




    /**
     * This method save an issue into the SonarQube service.
     *
     * @param context A SensorContext to reach SonarQube services.
     * @param files Map containing files in SQ format.
     * @param issue A AnalysisRule with the convenient format for Shellcheck.
     */
    static void saveIssue(final SensorContext context, final Map<String, InputFile> files, final CheckstyleError issue, final CheckstyleFile file) {

        // Retrieve the file containing the issue.
        final InputFile inputFile = files.getOrDefault(file.name, null);

        if(inputFile!=null) {
            // Retrieve the ruleKey if it exists.
            final RuleKey ruleKey = RuleKey.of(ShellcheckRulesDefinition.getRepositoryKeyForLanguage(inputFile.language()), issue.source);

            // Create a new issue for SonarQube, but it must be saved using NewIssue.save().
            final NewIssue newIssue = context.newIssue();
            // Create a new location for this issue.
            final NewIssueLocation newIssueLocation = newIssue.newLocation();


            // Set trivial issue's attributes from AnalysisRule fields.
            newIssueLocation.on(inputFile);
            newIssueLocation.at(inputFile.selectLine(Integer.parseInt(issue.line)));
            newIssueLocation.message(issue.message);
            newIssue.forRule(ruleKey);
            newIssue.at(newIssueLocation);
            newIssue.save();
        } else {
            LOGGER.error(String.format(
                    "Issue '%s' on file '%s' has not been saved because source file was not found.",
                    issue.source, file.name
            ));
        }

    }

    /**
     * Construct a map with all found source files.
     *
     * @param fileSystem The file system on which the analysis is running.
     * @param checkstyleReport The checkstyle report
     * @return A possibly empty Map of InputFile.
     */
    private Map<String, InputFile> getScannedFiles(final FileSystem fileSystem, final CheckstyleReport checkstyleReport) {
        // Contains the result to be returned.
        final Map<String, InputFile> result = new HashMap<>();
        final List<CheckstyleFile> files = checkstyleReport.getCheckstyleFiles();

        // Looks for each file in file system, print an error if not found.
        for(final CheckstyleFile file : files) {
            // Checks if the file system contains a file with corresponding path (relative or absolute).
            FilePredicate predicate = fileSystem.predicates().hasPath(file.name);
            InputFile inputFile = fileSystem.inputFile(predicate);
            if(inputFile!=null) {
                result.put(file.name, inputFile);
            } else {
                LOGGER.error(String.format(
                        "The source file '%s' was not found.",
                        file.name
                ));
            }
        }

        return result;
    }

    /**
     * Returns a list of processable result file's path.
     *
     * @param config Configuration of the analysis where properties are put.
     * @param fileSystem The current file system.
     * @param autolaunch auto-launch activation
     * @return Return a list of path 'findable' in the file system.
     */
    private List<String> getReportFiles(final Configuration config, final FileSystem fileSystem, final boolean autolaunch) {
        // Contains the result to be returned.
        final List<String> result = new ArrayList<>();

        // Retrieves the non-verified path list from the SonarQube property.
        final String regex = config.get(ShellcheckPluginProperties.REPORT_REGEX_KEY).orElse(ShellcheckPluginProperties.REPORT_REGEX_DEFAULT);
        // Check if each path is known by the file system and add it to the processable path list,
        // otherwise print a warning and ignore this result file.
        // Add files matching user's defined regex.
        result.addAll(Arrays.asList(fileSystem.baseDir().list(
                (fileDir, fileName) -> Pattern.matches(regex, fileName)
        )));
        // If auto-launch is enabled, add default results (generated by the auto-launch)
        if(autolaunch && !regex.equals(ShellcheckPluginProperties.REPORT_REGEX_DEFAULT)){
            LOGGER.info("files detection (autolaunch enabled)");
            result.addAll(Arrays.asList(fileSystem.baseDir().list(
                    (fileDir, fileName) -> Pattern.matches(ShellcheckPluginProperties.REPORT_REGEX_DEFAULT, fileName)
            )));
        }
        if(result.isEmpty()){
                LOGGER.info("No shellcheck result file has been found.");
        }

        return result;
    }

    /**
     * Check if a rule is activated in current analysis.
     *
     * @param activeRules Set of active rules during an analysis.
     * @param rule Key (ShellCheck) of the rule to check.
     * @return True if the rule is active and false if not or not exists.
     */
    private boolean isRuleActive(final ActiveRules activeRules, final String rule) {
        final RuleKey ruleKeyShell = RuleKey.of(ShellcheckRulesDefinition.getRepositoryKeyForLanguage(ShellLanguage.KEY), rule);
        return activeRules.find(ruleKeyShell)!=null;
    }

}


