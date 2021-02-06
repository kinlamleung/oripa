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
package oripa.persistent.doc;

import oripa.doc.Doc;
import oripa.persistent.filetool.Exporter;
import oripa.persistent.filetool.FileTypeProperty;
import oripa.persistent.filetool.Loader;
import oripa.persistent.doc.exporter.*;

/**
 * @author OUCHI Koji
 *
 */
public enum FoldedModelFileTypeKey implements FileTypeProperty<Doc> {
	SVG_FOLDED_MODEL("svg_folded_model", 1, null, ExporterSVGFactory.createFoldedModelExporter(false), ".svg"),
	SVG_FOLDED_MODEL_FLIP("svg_folded_model_flip", 1, null, ExporterSVGFactory.createFoldedModelExporter(true), ".svg"),
	ORMAT_FOLDED_MODEL("ormat", 2, null, new ExporterORmat(), ".ormat");

	private final String keyText;
	private final Integer order;
	private final String[] extensions;
	
	private final Loader<Doc> loader;
	private final Exporter<Doc> exporter;


	/**
	 *
	 * Constructor
	 *
	 * @param key
	 *            key string
	 * @param order
	 *            defines the order of members.
	 * @param extensions
	 *            which should be managed as that file type.
	 * @param loadingAction
	 * @param savingAction
	 */
	private FoldedModelFileTypeKey(final String key, final Integer order,
			Loader<Doc> loader,
			Exporter<Doc> exporter,			
			final String... extensions) {
		this.keyText = key;
		this.order = order;
		this.loader = loader;
		this.exporter = exporter;
		this.extensions = extensions;
	}

	@Override
	public String getKeyText() {
		return keyText;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.persistent.doc.FileTypeProperty#getExtensions()
	 */
	@Override
	public String[] getExtensions() {
		return extensions;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.persistent.filetool.FileTypeProperty#getOrder()
	 */
	@Override
	public Integer getOrder() {
		return order;
	}
	
	@Override
	public Loader<Doc> getLoader() {
		return loader;
	}
	
	@Override
	public Exporter<Doc> getExporter() {
		return exporter;
	}
}
