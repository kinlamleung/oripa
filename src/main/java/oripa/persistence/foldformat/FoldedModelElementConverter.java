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
package oripa.persistence.foldformat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import oripa.domain.fold.halfedge.OriEdge;
import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.geom.GeomUtil;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class FoldedModelElementConverter {

	private final AssignmentConverter assignmentConverter = new AssignmentConverter();

	/**
	 * Call this method first of all. This method sets the ID of each vertex.
	 *
	 * @param origamiModel
	 */
	public void setVertexIDs(final OrigamiModel origamiModel) {
		var vertices = origamiModel.getVertices();

		IntStream.range(0, vertices.size())
				.forEach(i -> vertices.get(i).setVertexID(i));
	}

	public List<List<Double>> toVerticesCoords(final OrigamiModel origamiModel) {
		var vertices = origamiModel.getVertices();

		var coords = vertices.stream()
				.map(vertex -> {
					var position = vertex.getPosition();
					return List.of(position.getX(), position.getY());
				})
				.collect(Collectors.toList());

		return coords;
	}

	/**
	 * This method assumes that each vertex has its ID.
	 *
	 * @param origamiModel
	 * @return
	 */
	public List<List<Integer>> toEdgesVertices(final OrigamiModel origamiModel) {
		var edges = origamiModel.getEdges();

		var vertexIndices = edges.stream()
				.map(edge -> List.of(edge.getStartVertex().getVertexID(), edge.getEndVertex().getVertexID()))
				.collect(Collectors.toList());

		return vertexIndices;
	}

	/**
	 * This method assumes that each vertex has its ID.
	 *
	 * @param origamiModel
	 * @return
	 */
	public List<String> toEdgesAssignment(final OrigamiModel origamiModel) {
		var edges = origamiModel.getEdges();

		return edges.stream()
				.map(edge -> assignmentConverter.toFOLD(OriLine.Type.fromInt(edge.getType())))
				.collect(Collectors.toList());
	}

	/**
	 * This method assumes that each vertex has its ID.
	 *
	 * @param origamiModel
	 * @return
	 */
	public List<List<Integer>> toFacesVertices(final OrigamiModel origamiModel) {
		var faces = origamiModel.getFaces();

		return faces.stream()
				.map(face -> face.halfedgeStream()
						.map(he -> he.getVertex().getVertexID())
						.collect(Collectors.toList()))
				.collect(Collectors.toList());
	}

	public List<List<Integer>> toFaceOrders(final OrigamiModel origamiModel, final OverlapRelation overlapRelation) {
		var faces = origamiModel.getFaces();

		var orders = new ArrayList<List<Integer>>(faces.size());

		for (int i = 0; i < faces.size(); i++) {
			for (int j = i + 1; j < faces.size(); j++) {
				var g = faces.get(j);

				if (g.isFaceFront()) {
					if (overlapRelation.isUpper(i, j)) {
						orders.add(List.of(i, j, 1));
					} else if (overlapRelation.isLower(i, j)) {
						orders.add(List.of(i, j, -1));
					}
				} else {
					if (overlapRelation.isUpper(i, j)) {
						orders.add(List.of(i, j, -1));
					} else if (overlapRelation.isLower(i, j)) {
						orders.add(List.of(i, j, 1));
					}
				}
			}
		}

		return orders;
	}

	public List<OriVertex> fromVerticesCoords(final List<List<Double>> verticesCoords) {
		var vertices = new ArrayList<OriVertex>();

		verticesCoords.forEach(coords -> {
			vertices.add(new OriVertex(coords.get(0), coords.get(1)));
		});

		return vertices;
	}

	public List<OriEdge> fromEdges(final List<List<Integer>> edgesVertices, final List<String> edgesAssignment,
			final List<OriVertex> vertices) {
		var edges = new ArrayList<OriEdge>();

		for (int i = 0; i < edgesVertices.size(); i++) {
			var edgeVertices = edgesVertices.get(i);
			var edge = new OriEdge(
					vertices.get(edgeVertices.get(0)),
					vertices.get(edgeVertices.get(1)),
					assignmentConverter.fromFOLD(edgesAssignment.get(i)).toInt());
			edges.add(edge);
		}

		return edges;
	}

	public List<OriFace> fromFacesVertices(final List<List<Integer>> facesVertices, final List<OriVertex> vertices) {
		var faces = new ArrayList<OriFace>();

		for (int i = 0; i < facesVertices.size(); i++) {
			var face = new OriFace();

			var faceVertices = facesVertices.get(i);

			faceVertices.forEach(v -> {
				var halfedge = new OriHalfedge(vertices.get(v), face);
				face.addHalfedge(halfedge);
			});
			face.makeHalfedgeLoop();

			if (!GeomUtil.isCCW(
					face.getHalfedge(0).getPosition(),
					face.getHalfedge(1).getPosition(),
					face.getHalfedge(2).getPosition())) {
				face.invertFaceFront();
			}

			faces.add(face);
		}

		return faces;
	}

	public OverlapRelation fromFaceOrders(final List<List<Integer>> faceOrders, final List<OriFace> faces) {
		var overlapRelation = new OverlapRelation(faces.size());

		faceOrders.forEach(order -> {
			var i = order.get(0);
			var j = order.get(1);
			var g = faces.get(j);
			var direction = order.get(2);
			if (g.isFaceFront()) {
				if (direction == 1) {
					overlapRelation.setUpper(i, j);
				} else if (direction == -1) {
					overlapRelation.setLower(i, j);
				}
			} else {
				if (direction == 1) {
					overlapRelation.setLower(i, j);
				} else if (direction == -1) {
					overlapRelation.setUpper(i, j);
				}

			}
		});

		return overlapRelation;
	}
}
