/**
 * Mogwai ERDesigner. Copyright (C) 2002 The Mogwai Project.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package de.erdesignerng.util;

/**
 * @author $Author: dr-death2 $
 * @version $Date: 2014-01-12 00:52:46 $
 */
public class VersionString implements Comparable<VersionString> {

	private final String version;

	public VersionString(final String version) {
		if (version.isEmpty()) {
			throw new IllegalArgumentException("\"version\" may not be zero length");
		}

		final String[] temp = version.split("\\.");
		for (final String s : temp) {
			if (s == null) {
				throw new IllegalArgumentException("\"version\" contains a null version sub-string");
			}
			if (s.isEmpty()) {
				throw new IllegalArgumentException("\"version\" contains a zero lenght version sub-string");
			}
			Integer.parseInt(s);
		}

		this.version = version;
	}

	@Override
	public String toString() {
		return this.version;
	}

	public int compareTo(final String otherVersion) {
		if (otherVersion.equals(this.version)) {
			return 0;
		}

		final String[] otherVersionList = otherVersion.split("\\.");
		final String[] thisVersionList = this.version.split("\\.");
		int thisIndex = 0;

		for (final String otherVersionFragment : otherVersionList) {
			if (thisIndex >= thisVersionList.length) {
				break;
			}

			final int thisVersionNum = Integer.parseInt(thisVersionList[thisIndex]);
			final int otherVersionNum = Integer.parseInt(otherVersionFragment);
			if (thisVersionNum != otherVersionNum) {
				return (thisVersionNum < otherVersionNum) ? -1 : 1;
			}

			thisIndex++;
		}

		if (thisVersionList.length != otherVersionList.length) {
			return (thisVersionList.length < otherVersionList.length) ? -1 : 1;
		} else {
			return 0;
		}
	}

	@Override
	public int compareTo(final VersionString other) {
		if (other == this) {
			return 0;
		} else if(other == null) {
			return 1;
		}

		return compareTo(other.version);
	}

	public boolean isEqual(final String other) {
		return (compareTo(other) == 0);
	}

	public boolean isHigherThan(final String other) {
		return (compareTo(other) > 0);
	}

	public boolean isHigherThanOrEqual(final String other) {
		return (compareTo(other) >= 0);
	}

	public boolean isLowerThan(final String other) {
		return (compareTo(other) < 0);
	}

	public boolean isLowerThanOrEqual(final String other) {
		return (compareTo(other) <= 0);
	}

}