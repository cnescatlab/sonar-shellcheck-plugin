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

import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

/**
 * Declares language Shell.
 *
 * @author lequal
 */
public final class ShellLanguage extends AbstractLanguage {

	public final static String KEY = "shell";
	public final static String NAME = "Shells (Bourne, Bourne-Again, Korn, Debian Almquist)";
	public final static String[] INTERPRETERS = {"sh","dash","bash","ksh"};

	/**
	 * Injected SonarQube configuration.
	 */
	private final Configuration configuration;

	/**
	 * ShellCheck extension
	 *
	 * @param configuration Inject SonarQube configuration into this extension.
	 */
	public ShellLanguage(final Configuration configuration) {
		super(ShellLanguage.KEY, ShellLanguage.NAME);
		this.configuration = configuration;
	}

	/**
	 * Returns the list of suffixes which should be associated to this language.
	 *
	 * @return A strings' array with file's suffixes.
	 */
	@Override
	public String[] getFileSuffixes() {
		return ShellLanguage.INTERPRETERS;
	}

	/**
	 * Assert obj is the same object as this.
	 *
	 * @param obj Object to compare with this.
	 * @return True if obj is this.
	 */
	@Override
	public boolean equals(Object obj) {
		return obj==this;
	}

	/**
	 * Override hashcode because equals is overridden.
	 *
	 * @return An integer hashcode.
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
