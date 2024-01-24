/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package oripa.vecmath;

import java.util.Objects;

/**
 * Immutable 2D vector with fluent interface.
 *
 * @author OUCHI Koji
 *
 */
public class Vector2d {

	private final double x;
	private final double y;

	public Vector2d(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	/**
	 *
	 * @param v
	 * @return this + v
	 */
	public Vector2d add(final Vector2d v) {
		return new Vector2d(x + v.x, y + v.y);
	}

	public Vector2d subtract(final Vector2d v) {
		return new Vector2d(x - v.x, y - v.y);
	}

	public Vector2d multiply(final double a) {
		return new Vector2d(a * x, a * y);
	}

	public double length() {
		return Math.sqrt(x * x + y * y);
	}

	public double lengthSquared() {
		return x * x + y * y;
	}

	public Vector2d normalize() {
		return multiply(1.0 / length());
	}

	public double dot(final Vector2d v) {
		return x * v.x + y * v.y;
	}

	public double angle(final Vector2d v) {
		var cos = dot(v) / (length() * v.length());

		if (cos < -1.0) {
			cos = -1.0;
		}
		if (cos > 1.0) {
			cos = 1.0;
		}

		return Math.acos(cos);
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof Vector2d)) {
			return false;
		}

		var v = (Vector2d) o;

		return x == v.x && y == v.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
