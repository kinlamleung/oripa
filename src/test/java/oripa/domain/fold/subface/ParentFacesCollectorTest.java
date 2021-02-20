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
package oripa.domain.fold.subface;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class ParentFacesCollectorTest {
	@InjectMocks
	private ParentFacesCollector collector;

	/**
	 * Test method for
	 * {@link oripa.domain.fold.subface.ParentFacesCollector#collect(java.util.List, oripa.domain.fold.subface.SubFace, double)}.
	 */
	@Test
	void testCollect() {
		var face1 = OriFaceFactoryForTest.create10PxSquareFace(0, 0);
		var face2 = OriFaceFactoryForTest.create10PxSquareFace(10, 0);
		var face3 = OriFaceFactoryForTest.create10PxSquareFace(0, 10);
		var face4 = OriFaceFactoryForTest.create10PxSquareFace(5, 5); // overlap

		var faces = List.of(face1, face2, face3, face4);

		var splitFace = OriFaceFactoryForTest.createRectangle(6, 6, 10, 10);

		var subface = new SubFace(splitFace);

		var parents = collector.collect(faces, subface, 20);

		assertEquals(2, parents.size());
		assertTrue(parents.contains(face1));
		assertTrue(parents.contains(face4));
	}

}