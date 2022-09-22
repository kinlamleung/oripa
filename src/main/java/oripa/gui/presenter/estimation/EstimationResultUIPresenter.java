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
package oripa.gui.presenter.estimation;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.estimation.EstimationResultFileAccess;
import oripa.exception.UserCanceledException;
import oripa.gui.presenter.file.FileAccessPresenter;
import oripa.gui.view.FrameView;
import oripa.gui.view.estimation.EstimationResultUIView;
import oripa.gui.view.file.FileChooserFactory;
import oripa.persistence.entity.FoldedModelDAO;
import oripa.persistence.entity.FoldedModelFileAccessSupportSelector;
import oripa.persistence.entity.exporter.FoldedModelEntity;

/**
 * @author OUCHI Koji
 *
 */
public class EstimationResultUIPresenter {
	private static final Logger logger = LoggerFactory.getLogger(EstimationResultUIPresenter.class);

	private final EstimationResultUIView view;

	final FileChooserFactory fileChooserFactory;

	private String lastFilePath;
	private final Consumer<String> lastFilePathChangeListener;

	public EstimationResultUIPresenter(
			final EstimationResultUIView view,
			final FileChooserFactory fileChooserFactory,
			final String lastFilePath,
			final Consumer<String> lastFilePathChangeListener) {
		this.view = view;

		this.fileChooserFactory = fileChooserFactory;

		this.lastFilePath = lastFilePath;
		this.lastFilePathChangeListener = lastFilePathChangeListener;

		addListener();
	}

	private void addListener() {
		view.addExportButtonListener(this::export);
	}

	/**
	 * open export dialog for current folded estimation
	 */
	private void export() {
		try {
			var supportSelector = new FoldedModelFileAccessSupportSelector(view.isFaceOrderFlipped());
			var dao = new FoldedModelDAO(supportSelector);
			var fileAccessService = new EstimationResultFileAccess(dao);

			var foldedModel = view.getModel();
			var overlapRelation = view.getOverlapRelation();

			var entity = new FoldedModelEntity(foldedModel.getOrigamiModel(), overlapRelation);

			var presenter = new FileAccessPresenter<FoldedModelEntity>((FrameView) view.getTopLevelView(),
					fileChooserFactory, fileAccessService);

			lastFilePath = presenter.saveUsingGUI(entity, lastFilePath).get();

			lastFilePathChangeListener.accept(lastFilePath);
		} catch (UserCanceledException e) {

		} catch (Exception ex) {
			logger.error("error: ", ex);
			view.showExportErrorMessage(ex);
		}
	}
}