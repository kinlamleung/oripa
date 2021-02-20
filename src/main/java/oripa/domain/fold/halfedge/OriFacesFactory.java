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
package oripa.domain.fold.halfedge;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class OriFacesFactory {
	private static final Logger logger = LoggerFactory.getLogger(OriFacesFactory.class);

	/**
	 * Creates and sets new face objects to given {@code faces}. Since ORIPA
	 * tries to output failure model data, we can't make create() method.
	 *
	 * @param vertices
	 * @param faces
	 * @return
	 */
	public boolean buildFaces(final Collection<OriVertex> vertices,
			final Collection<OriFace> faces) {
		var outlineEdges = new ArrayList<OriEdge>();

		// Construct the faces
		for (OriVertex v : vertices) {

			for (OriEdge e : v.edges) {

				if (e.type == OriLine.Type.CUT.toInt()) {
					outlineEdges.add(e);
					continue;
				}

				if (v == e.sv) {
					if (e.left != null) {
						continue;
					}
				} else {
					if (e.right != null) {
						continue;
					}
				}

				OriFace face = makeFace(v, e);
				if (face == null) {
					return false;
				}
				faces.add(face);
			}
		}
		if (faces.isEmpty()) { // happens when there is no crease
			OriEdge outlineEdge = outlineEdges.get(0);
			OriVertex v = outlineEdge.sv;

			OriFace face = makeFace(v, outlineEdge);
			if (face == null) {
				return false;
			}
			faces.add(face);
		}

		return true;
	}

	private OriFace makeFace(final OriVertex startingVertex, final OriEdge startingEdge) {
		OriFace face = new OriFace();
		OriVertex walkV = startingVertex;
		OriEdge walkE = startingEdge;
		int debugCount = 0;
		do {
			if (debugCount++ > 100) {
				logger.error("invalid input for making faces.");
//						throw new UnfoldableModelException("algorithmic error");
				return null;
			}
			OriHalfedge he = new OriHalfedge(walkV, face);
			face.halfedges.add(he);
			he.tmpInt = walkE.type;
			if (walkE.sv == walkV) {
				walkE.left = he;
			} else {
				walkE.right = he;
			}
			walkV = walkE.oppositeVertex(walkV);
			walkE = walkV.getPrevEdge(walkE);
		} while (walkV != startingVertex);
		face.makeHalfedgeLoop();
		face.setOutline();
		face.setPreOutline();
		return face;
	}

}