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
package oripa.domain.cptool;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import oripa.value.OriLine;
import oripa.value.OriLine.Type;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
class RoratedLineFactoryTest {
	RotatedLineFactory factory = new RotatedLineFactory();
	static final double EPS = 1e-8;

	@Test
	void test() {
		var creasePattern = new DefaultPaperFactory().create();

		// a line on 45 degree steps touching the boundary.
		var p0 = new Vector2d(-200, -100);
		var p1 = new Vector2d(-100, -200);
		var line = new OriLine(p0, p1, Type.MOUNTAIN);

		creasePattern.add(line);

		// rotates around the end point
		var center = p1;
		var rotatedLines = factory.createRotatedLines(
				center.getX(), center.getY(), 45, 8,
				List.of(line), creasePattern,
				EPS);

		assertEquals(3, rotatedLines.stream()
				.filter(rl -> rl.length() > EPS)
				.filter(rl -> rl.pointStream().anyMatch(p -> center.equals(p, EPS)))
				.count());
	}

}
