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
package oripa.gui.presenter.main;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.Folder;
import oripa.domain.fold.FolderFactory;
import oripa.domain.fold.foldability.FoldabilityChecker;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.halfedge.OrigamiModelFactory;

/**
 * @author OUCHI Koji
 *
 */
public class ModelComputationFacade {
	private static final Logger logger = LoggerFactory.getLogger(ModelComputationFacade.class);

	public class ComputationResult {
		private final List<OrigamiModel> origamiModels;
		private final List<FoldedModel> foldedModels;

		public ComputationResult(final List<OrigamiModel> origamiModels, final List<FoldedModel> foldedModels) {
			this.origamiModels = origamiModels;
			this.foldedModels = foldedModels;
		}

		public List<OrigamiModel> getOrigamiModels() {
			return Collections.unmodifiableList(origamiModels);
		}

		public List<FoldedModel> getFoldedModels() {
			if (foldedModels == null) {
				return List.of();
			}
			return Collections.unmodifiableList(foldedModels);
		}

		/**
		 *
		 * @return the sum of the number of the foldable patterns for each
		 *         crease pattern. -1 if nothing is foldable.
		 */
		public int countFoldablePatterns() {
			if (foldedModels == null) {
				return -1;
			}
			return foldedModels.stream().mapToInt(m -> m.getFoldablePatternCount()).sum();
		}

	}

	private final Supplier<Boolean> needCleaningUpDuplication;
	private final Runnable showCleaningUpMessage;
	private final Runnable showFailureMessage;

	public ModelComputationFacade(
			final Supplier<Boolean> needCleaningUpDuplication,
			final Runnable showCleaningUpMessage,
			final Runnable showFailureMessage) {
		this.needCleaningUpDuplication = needCleaningUpDuplication;
		this.showCleaningUpMessage = showCleaningUpMessage;
		this.showFailureMessage = showFailureMessage;
	}

	public ComputationResult computeModels(
			final List<OrigamiModel> origamiModels,
			final boolean fullEstimation) {

		var folderFactory = new FolderFactory();
		Folder folder = folderFactory.create();

		List<FoldedModel> foldedModels = null;

		var checker = new FoldabilityChecker();

		if (origamiModels.stream().anyMatch(Predicate.not(checker::testLocalFlatFoldability))) {
			origamiModels.forEach(folder::foldWithoutLineType);
		} else {
			foldedModels = origamiModels.stream()
					.map(model -> folder.fold(model, fullEstimation))
					.collect(Collectors.toList());
		}

		return new ComputationResult(origamiModels, foldedModels);
	}

	/**
	 * try building the crease pattern and ask for additional measures to help
	 * clean it
	 *
	 * @return folded Origami model
	 */
	public List<OrigamiModel> buildOrigamiModels(final CreasePattern creasePattern) {
		OrigamiModelFactory modelFactory = new OrigamiModelFactory();
		OrigamiModel wholeModel = modelFactory.createOrigamiModel(
				creasePattern);

		List<OrigamiModel> origamiModels = modelFactory.createOrigamiModels(creasePattern);

		var checker = new FoldabilityChecker();

		logger.debug("Building origami model.");

		if (checker.testLocalFlatFoldability(wholeModel)) {
			logger.debug("No modification is needed.");
			return origamiModels;
		}

		// ask if ORIPA should try to remove duplication.
		if (!needCleaningUpDuplication.get()) {
			// the answer is "no."
			return origamiModels;
		}

		// clean up the crease pattern
		if (creasePattern.cleanDuplicatedLines()) {
			showCleaningUpMessage.run();
		}
		// re-create the model data for simplified crease pattern
		wholeModel = modelFactory
				.createOrigamiModel(creasePattern);

		if (checker.testLocalFlatFoldability(wholeModel)) {
			return modelFactory.createOrigamiModels(creasePattern);
		}

		showFailureMessage.run();

		return origamiModels;
	}

}