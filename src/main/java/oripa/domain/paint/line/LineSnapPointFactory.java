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
package oripa.domain.paint.line;

import java.util.ArrayList;
import java.util.Collection;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.SnapPointFactory;
import oripa.geom.Line;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class LineSnapPointFactory {
	public Collection<Vector2d> createSnapPoints(final PaintContext context) {
		Vector2d p0, p1;
		p0 = context.getVertex(0);
		p1 = context.getVertex(1);

		var creasePattern = context.getCreasePattern();

		var snapPointFactory = new SnapPointFactory();

		Collection<Vector2d> snapPoints = new ArrayList<>(
				snapPointFactory.createSnapPoints(creasePattern, new Line(p0, p0.subtract(p1)),
						context.getPointEps()));

		snapPoints.add(p0);
		snapPoints.add(p1);

		return snapPoints.stream().distinct().toList();
	}

}
