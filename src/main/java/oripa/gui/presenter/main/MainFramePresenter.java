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

import java.awt.Color;
import java.io.IOException;
import java.util.function.Supplier;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.main.DataFileAccess;
import oripa.application.main.IniFileAccess;
import oripa.application.main.PaintContextModification;
import oripa.appstate.StateManager;
import oripa.appstate.StatePopper;
import oripa.doc.Doc;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.PaintDomainContext;
import oripa.file.FileHistory;
import oripa.gui.bind.state.PaintBoundStateFactory;
import oripa.gui.bind.state.action.PaintActionSetterFactory;
import oripa.gui.presenter.creasepattern.CreasePatternPresentationContext;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.DeleteSelectedLinesActionListener;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.creasepattern.SelectAllLineActionListener;
import oripa.gui.presenter.creasepattern.UnselectAllItemsActionListener;
import oripa.gui.view.main.MainFrameDialogFactory;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.MainViewSetting;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.util.ChildFrameManager;
import oripa.gui.viewsetting.ViewScreenUpdater;
import oripa.gui.viewsetting.ViewUpdateSupport;
import oripa.persistence.dao.AbstractFilterSelector;
import oripa.persistence.doc.CreasePatternFileTypeKey;
import oripa.persistence.doc.DocFilterSelector;
import oripa.persistence.filetool.AbstractSavingAction;
import oripa.persistence.filetool.FileAccessSupportFilter;
import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.WrongDataFormatException;
import oripa.resource.Constants;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

/**
 * @author OUCHI Koji
 *
 */
public class MainFramePresenter {
	private static final Logger logger = LoggerFactory.getLogger(MainFramePresenter.class);

	private final MainFrameView view;
	private final MainFrameDialogFactory dialogFactory;

	private final PainterScreenPresenter screenPresenter;
	private final UIPanelPresenter uiPanelPresenter;

	// shared objects
	private final ResourceHolder resourceHolder = ResourceHolder.getInstance();

	private final StateManager<EditMode> stateManager;

	private final ViewScreenUpdater screenUpdater;
	private final PainterScreenSetting screenSetting;

	private final PaintBoundStateFactory stateFactory;

	private final ChildFrameManager childFrameManager = new ChildFrameManager();

	private final Doc document;

	private final PaintContext paintContext;
	private final CreasePatternViewContext viewContext;
	private final MouseActionHolder actionHolder;

	// data access
	private final IniFileAccess iniFileAccess;
	private final DataFileAccess dataFileAccess;
	private final FileHistory fileHistory;
	private final AbstractFilterSelector<Doc> filterSelector = new DocFilterSelector();

	// services
	private final PaintContextModification paintContextModification = new PaintContextModification();

	public MainFramePresenter(final MainFrameView view,
			final ViewUpdateSupport viewUpdateSupport,
			final MainFrameDialogFactory dialogFactory,
			final MainViewSetting viewSetting,
			final Doc document,
			final PaintDomainContext domainContext,
			final CreasePatternPresentationContext presentationContext,
			final StateManager<EditMode> stateManager,
			final FileHistory fileHistory,
			final IniFileAccess iniFileAccess,
			final DataFileAccess dataFileAccess) {
		this.view = view;
		this.dialogFactory = dialogFactory;

		this.document = document;
		this.paintContext = domainContext.getPaintContext();
		this.viewContext = presentationContext.getViewContext();
		this.actionHolder = presentationContext.getActionHolder();
		this.stateManager = stateManager;
		this.fileHistory = fileHistory;
		this.iniFileAccess = iniFileAccess;
		this.dataFileAccess = dataFileAccess;

		this.screenSetting = viewSetting.getPainterScreenSetting();

		document.setCreasePattern(paintContext.getCreasePattern());

		var screen = view.getPainterScreenView();
		this.screenUpdater = viewUpdateSupport.getViewScreenUpdater();

		var uiPanel = view.getUIPanelView();

		var setterFactory = new PaintActionSetterFactory(
				actionHolder, screenUpdater::updateScreen, paintContext);

		stateFactory = new PaintBoundStateFactory(
				stateManager,
				setterFactory,
				viewSetting,
				presentationContext,
				domainContext);

		screenPresenter = new PainterScreenPresenter(
				screen,
				viewUpdateSupport,
				presentationContext,
				paintContext,
				document);

		uiPanelPresenter = new UIPanelPresenter(
				uiPanel,
				stateManager,
				viewUpdateSupport,
				presentationContext,
				domainContext,
				document,
				setterFactory,
				stateFactory,
				screenSetting);

		uiPanelPresenter.setChildFrameManager(childFrameManager);

		loadIniFile();

		modifySavingActions();

		addListeners();

		view.buildFileMenu();
		updateTitleText();
	}

	public void setViewVisible(final boolean visible) {
		view.setViewVisible(visible);
	}

	private void addListeners() {
		view.addOpenButtonListener(() -> {
			String path = loadFile(null);
			screenUpdater.updateScreen();
			updateMenu(path);
			updateTitleText();
		});

		addImportActionListener();

		view.addSaveButtonListener(() -> {
			var filePath = document.getDataFilePath();
			if (CreasePatternFileTypeKey.OPX.extensionsMatch(filePath)) {
				saveProjectFile(document, filePath, CreasePatternFileTypeKey.OPX);
			} else if (CreasePatternFileTypeKey.FOLD.extensionsMatch(filePath)) {
				saveProjectFile(document, filePath, CreasePatternFileTypeKey.FOLD);
			} else {
				saveAnyTypeUsingGUI();
			}
		});

		view.addSaveAsButtonListener(() -> saveAnyTypeUsingGUI());

		view.addExportFOLDButtonListener(() -> {
			String lastDirectory = fileHistory.getLastDirectory();
			saveFile(lastDirectory, document.getDataFileName(),
					filterSelector.getFilter(CreasePatternFileTypeKey.FOLD));
		});

		view.addSaveAsImageButtonListener(() -> {
			String lastDirectory = fileHistory.getLastDirectory();
			saveFile(lastDirectory, document.getDataFileName(),
					filterSelector.getFilter(CreasePatternFileTypeKey.PICT));
		});

		view.addExitButtonListener(() -> exit());

		view.addUndoButtonListener(() -> {
			try {
				actionHolder.getMouseAction().undo(paintContext);
			} catch (NullPointerException ex) {
				if (actionHolder.getMouseAction() == null) {
					logger.error("mouseAction should not be null.", ex);
				} else {
					logger.error("Wrong implementation.", ex);
				}
			}
			screenUpdater.updateScreen();
		});

		view.addRedoButtonListener(() -> {
			try {
				actionHolder.getMouseAction().redo(paintContext);
			} catch (NullPointerException ex) {
				if (actionHolder.getMouseAction() == null) {
					logger.error("mouseAction should not be null.", ex);
				} else {
					logger.error("Wrong implementation.", ex);
				}
			}
			screenUpdater.updateScreen();
		});

		view.addClearButtonListener(() -> clear());

		view.addAboutButtonListener(view::showAboutAppMessage);

		view.addExportDXFButtonListener(() -> saveFileWithModelCheck(CreasePatternFileTypeKey.DXF));
		view.addExportCPButtonListener(() -> saveFileWithModelCheck(CreasePatternFileTypeKey.CP));
		view.addExportSVGButtonListener(() -> saveFileWithModelCheck(CreasePatternFileTypeKey.SVG));

		view.addPropertyButtonListener(() -> showPropertyDialog());
		view.addRepeatCopyButtonListener(() -> showArrayCopyDialog());
		view.addCircleCopyButtonListener(() -> showCircleCopyDialog());

		addPaintMenuItemsListener();

		view.addMRUFileButtonListener(this::loadFileFromMRUFileMenuItem);
		view.addMRUFilesMenuItemUpdateListener(this::updateMRUFilesMenuItem);

		view.setEstimationResultSaveColorsListener((front, back) -> {
			var property = document.getProperty();
			property.putFrontColorCode(convertColorToCode(front));
			property.putBackColorCode(convertColorToCode(back));

		});

		view.setPaperDomainOfModelChangeListener(screenPresenter::setPaperDomainOfModel);

		view.addWindowClosingListener(this::windowClosing);
	}

	/**
	 * Ensure the execution order as loading file comes first.
	 */
	private void addImportActionListener() {
		var state = stateFactory.create(StringID.IMPORT_CP_ID,
				null,
				null);
		view.addImportButtonListener(() -> {
			try {
				dataFileAccess
						.loadFile(null, fileHistory.getLastPath(), (JFrame) view, filterSelector.getLoadables())
						.ifPresent(otherDoc -> {
							paintContext.getPainter().resetSelectedOriLines();
							var otherCreasePattern = otherDoc.getCreasePattern();
							otherCreasePattern.forEach(l -> l.selected = true);
							paintContext.getCreasePattern().addAll(otherCreasePattern);
						});
				state.performActions();
			} catch (IllegalArgumentException | FileVersionError | WrongDataFormatException | IOException ex) {
				logger.error("failed to load (import)", ex);
				view.showLoadFailureErrorMessage(ex);
			}
		});
	}

	private void addPaintMenuItemsListener() {
		/*
		 * For changing outline
		 */
		var changeOutlineState = stateFactory.create(StringID.EDIT_CONTOUR_ID,
				null, null);
		view.addChangeOutlineButtonListener(changeOutlineState::performActions);

		/*
		 * For selecting all lines
		 */
		var selectAllState = stateFactory.create(StringID.SELECT_ALL_LINE_ID,
				null, null);
		view.addSelectAllButtonListener(selectAllState::performActions);
		var selectAllListener = new SelectAllLineActionListener(paintContext);
		view.addSelectAllButtonListener(selectAllListener);

		/*
		 * For starting copy-and-paste
		 */
		Supplier<Boolean> detectCopyPasteError = () -> paintContext.getPainter().countSelectedLines() == 0;
		var copyPasteState = stateFactory.create(StringID.COPY_PASTE_ID,
				detectCopyPasteError, view::showCopyPasteErrorMessage);
		view.addCopyAndPasteButtonListener(copyPasteState::performActions);

		/*
		 * For starting cut-and-paste
		 */
		var cutPasteState = stateFactory.create(StringID.CUT_PASTE_ID,
				detectCopyPasteError, view::showCopyPasteErrorMessage);
		view.addCutAndPasteButtonListener(cutPasteState::performActions);

		var statePopper = new StatePopper<EditMode>(stateManager);
		var unselectListener = new UnselectAllItemsActionListener(actionHolder, paintContext, statePopper,
				screenUpdater::updateScreen);
		view.addUnselectAllButtonListener(unselectListener);

		var deleteLinesListener = new DeleteSelectedLinesActionListener(paintContext, screenUpdater::updateScreen);
		view.addDeleteSelectedLinesButtonListener(deleteLinesListener);

	}

	private void modifySavingActions() {
		// overwrite the action to update GUI after saving.
		setProjectSavingAction(CreasePatternFileTypeKey.OPX);
		setProjectSavingAction(CreasePatternFileTypeKey.FOLD);
	}

	private void setProjectSavingAction(final CreasePatternFileTypeKey fileType) {
		filterSelector.getFilter(fileType).setSavingAction(
				new AbstractSavingAction<Doc>() {

					@Override
					public boolean save(final Doc data) {
						try {
							saveProjectFile(data, getPath(), fileType);
						} catch (Exception e) {
							logger.error("Failed to save file " + getPath(), e);
							return false;
						}
						return true;
					}
				});

	}

	private void updateMRUFilesMenuItem(final int index) {
		var histories = fileHistory.getHistory();
		if (index < histories.size()) {
			view.setMRUFilesMenuItem(index, histories.get(index));
		} else {
			view.setMRUFilesMenuItem(index, "");
		}
	}

	private void loadFileFromMRUFileMenuItem(final String filePath) {

		try {
			loadFile(filePath);
			updateTitleText();
		} catch (Exception ex) {
			logger.error("error when loading: ", ex);
			view.showLoadFailureErrorMessage(ex);
		}
		screenUpdater.updateScreen();
	}

	private void saveAnyTypeUsingGUI() {
		String lastDirectory = fileHistory.getLastDirectory();

		String path = saveFile(lastDirectory, document.getDataFileName(),
				filterSelector.getSavables());

		updateMenu(path);
		updateTitleText();
	}

	private void exit() {
		saveIniFile();
		System.exit(0);
	}

	private void clear() {
		document.set(new Doc(Constants.DEFAULT_PAPER_SIZE));

		paintContextModification
				.setCreasePatternToPaintContext(document.getCreasePattern(), paintContext);

		screenSetting.setGridVisible(true);

		childFrameManager.closeAll(view);

		screenUpdater.updateScreen();
		updateTitleText();
	}

	private void showPropertyDialog() {
		var dialog = dialogFactory.createPropertyDialog(view);

		var presenter = new PropertyDialogPresenter(dialog, document);

		presenter.setViewVisible(true);
	}

	private void showArrayCopyDialog() {
		if (paintContext.getPainter().countSelectedLines() == 0) {
			view.showNoSelectionMessageForArrayCopy();
			return;
		}

		var dialog = dialogFactory.createArrayCopyDialog(view);

		var presenter = new ArrayCopyDialogPresenter(dialog, paintContext, screenUpdater);

		presenter.setViewVisible(true);
	}

	private void showCircleCopyDialog() {
		if (paintContext.getPainter().countSelectedLines() == 0) {
			view.showNoSelectionMessageForCircleCopy();
			return;
		}

		var dialog = dialogFactory.createCircleCopyDialog(view);

		var presenter = new CircleCopyDialogPresenter(dialog, paintContext, screenUpdater);

		presenter.setViewVisible(true);
	}

	private void updateTitleText() {
		String fileName;
		if (document.getDataFilePath().isEmpty()) {
			fileName = resourceHolder.getString(ResourceKey.DEFAULT, StringID.Default.FILE_NAME_ID);
		} else {
			fileName = document.getDataFileName();
		}

		view.setFileNameToTitle(fileName);
	}

	/**
	 * saves project without opening a dialog
	 */
	private void saveProjectFile(final Doc doc, final String filePath,
			final CreasePatternFileTypeKey fileType) {
		try {
			dataFileAccess.saveProjectFile(doc, filePath, fileType);
		} catch (IOException | IllegalArgumentException e) {
			logger.error("Failed to save", e);
			view.showSaveFailureErrorMessage(e);
		}

		paintContext.creasePatternUndo().clearChanged();

		updateMenu(filePath);
		updateTitleText();
	}

	/**
	 * save file without origami model check
	 */
	@SafeVarargs
	private String saveFile(final String directory, final String fileName,
			final FileAccessSupportFilter<Doc>... filters) {

		try {
			return dataFileAccess.saveFile(
					document, directory, fileName, (JFrame) view, filters)
					.map(path -> {
						paintContext.creasePatternUndo().clearChanged();
						return path;
					})
					.orElse(document.getDataFilePath());
		} catch (IOException | IllegalArgumentException e) {
			logger.error("failed to save", e);
			view.showSaveFailureErrorMessage(e);
			return document.getDataFilePath();
		}
	}

	/**
	 * Open Save File As Dialogue for specific file types {@code type}. Runs a
	 * model check before saving.
	 */
	private void saveFileWithModelCheck(final CreasePatternFileTypeKey type) {
		var frame = (JFrame) view;
		try {
			dataFileAccess.saveFileWithModelCheck(document, fileHistory.getLastDirectory(),
					filterSelector.getFilter(type), frame,
					view::showModelBuildFailureDialog);
		} catch (IOException e) {
			logger.error("IO trouble", e);
			view.showSaveFailureErrorMessage(e);
		} catch (IllegalArgumentException e) {
			logger.error("Maybe data is not appropriate.", e);
			view.showSaveFailureErrorMessage(e);
		}
	}

	/**
	 * Update file menu. Do nothing if the given {@code filePath} is null or
	 * wrong.
	 *
	 * @param filePath
	 */
	private void updateMenu(final String filePath) {
		if (filePath == null) {
			return;
		}
		try {
			filterSelector.getLoadableFilterOf(filePath);
		} catch (IllegalArgumentException e) {
			logger.debug("updating menu is canceled.", e);
			return;
		}
		fileHistory.useFile(filePath);

		view.buildFileMenu();
	}

	/**
	 * if filePath is null, this method opens a dialog to select the target.
	 * otherwise, it tries to read data from the path.
	 *
	 * @param filePath
	 * @return file path for loaded file. {@code null} if loading is not done.
	 */
	private String loadFile(final String filePath) {
		var frame = (JFrame) view;

		childFrameManager.closeAll(view);

		try {
			return dataFileAccess.loadFile(
					filePath, fileHistory.getLastPath(), frame, filterSelector.getLoadables())
					.map(doc -> {
						// we can't substitute a loaded object because
						// the document object is referred by screen and UI
						// panel as a Holder.
						document.set(doc);

						var property = document.getProperty();
						view.getUIPanelView().setEstimationResultColors(
								convertCodeToColor(property.extractFrontColorCode()),
								convertCodeToColor(property.extractBackColorCode()));

						screenSetting.setGridVisible(false);
						paintContextModification
								.setCreasePatternToPaintContext(
										document.getCreasePattern(), paintContext);
						return document.getDataFilePath();
					}).orElse(null);
		} catch (FileVersionError | IllegalArgumentException | WrongDataFormatException
				| IOException e) {
			logger.error("failed to load", e);
			view.showLoadFailureErrorMessage(e);
			return document.getDataFilePath();
		}
	}

	private String convertColorToCode(final Color color) {
		return String.format("#%06X", color.getRGB() & 0x00FFFFFF);
	}

	private Color convertCodeToColor(final String code) {
		if (code == null) {
			return null;
		}

		try {
			return new Color(Integer.decode(code));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private void saveIniFile() {
		try {
			iniFileAccess.save(fileHistory, viewContext);
		} catch (IllegalStateException e) {
			logger.error("error when building ini file data", e);
			view.showSaveIniFileFailureErrorMessage(e);
		}
	}

	private void loadIniFile() {
		var ini = iniFileAccess.load();

		fileHistory.loadFromInitData(ini);
		screenSetting.setZeroLineWidth(ini.isZeroLineWidth());

		logger.debug("loaded ini.mvLineVisible: " + ini.isMvLineVisible());
		screenSetting.setMVLineVisible(ini.isMvLineVisible());

		logger.debug("loaded ini.auxLineVisible: " + ini.isAuxLineVisible());
		screenSetting.setAuxLineVisible(ini.isAuxLineVisible());

		logger.debug("loaded ini.vertexVisible: " + ini.isVertexVisible());
		screenSetting.setVertexVisible(ini.isVertexVisible());
	}

	private void windowClosing() {

		if (paintContext.creasePatternUndo().changeExists()) {
			// confirm saving edited opx
			if (view.showSaveOnCloseDialog()) {

				document.setCreasePattern(paintContext.getCreasePattern());

				String path = saveFile(fileHistory.getLastDirectory(),
						document.getDataFileName(), filterSelector.getSavables());
				if (path == null) {

				}
			}
		}

		saveIniFile();
	}

}
