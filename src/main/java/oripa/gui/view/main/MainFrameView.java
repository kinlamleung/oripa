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
package oripa.gui.view.main;

import java.awt.Color;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import oripa.appstate.StateManager;
import oripa.doc.Doc;
import oripa.domain.paint.PaintContext;
import oripa.geom.RectangleDomain;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.view.FrameView;
import oripa.gui.viewsetting.main.MainFrameSetting;
import oripa.gui.viewsetting.main.MainScreenSetting;
import oripa.gui.viewsetting.main.MainScreenUpdater;
import oripa.gui.viewsetting.main.uipanel.UIPanelSetting;

/**
 * @author OUCHI Koji
 *
 */
public interface MainFrameView extends FrameView {
	MainFrameSetting getMainFrameSetting();

	UIPanelView getUIPanelView();

	UIPanelSetting getUIPanelSetting();

	PainterScreenView getPainterScreenView();

	MainScreenSetting getMainScreenSetting();

	MainScreenUpdater getScreenUpdater();

	void addClearButtonListener(Runnable listener);

	void addOpenButtonListener(Runnable listener);

	void addImportButtonListener(Runnable listener);

	void addSaveButtonLisetener(Runnable listener);

	void addSaveAsButtonListener(Runnable listener);

	void addSaveAsImageButtonListener(Runnable listener);

	void addExportFOLDButtonListener(Runnable listener);

	void addExportDXFButtonListener(Runnable listener);

	void addExportCPButtonListener(Runnable listener);

	void addExportSVGButtonListener(Runnable listener);

	void addPropertyButtonListener(Runnable listener);

	void addChangeOutlineButtonListener(Runnable listener);

	void addSelectAllButtonListener(Runnable listener);

	void addCopyAndPasteButtonListener(Runnable listener);

	void addCutAndPasteButtonListener(Runnable listener);

	void addUndoButtonListener(Runnable listener);

	void addRedoButtonListener(Runnable listener);

	void addRepeatCopyButtonListener(Runnable listener);

	void addCircleCopyButtonListener(Runnable listener);

	void addUnselectAllButtonListener(Runnable listener);

	void addDeleteSelectedLinesButtonListener(Runnable listener);

	void addAboutButtonListener(Runnable listener);

	void addExitButtonListener(Runnable listener);

	void setEstimationResultSaveColorsListener(BiConsumer<Color, Color> listener);

	void setPaperDomainOfModelChangeListener(Consumer<RectangleDomain> listener);

//	void setPaperDomainOfModel(RectangleDomain domain);

	/**
	 * invoked when MRU file menu item is clicked.
	 *
	 * @param listener
	 *            the parameter of this listener is the file path.
	 */
	void addMRUFileButtonListener(Consumer<String> listener);

	/**
	 * invoked when the MRU file menu item changed.
	 *
	 * @param listener
	 *            the parameter of this listener is the index of MRU list.
	 */
	void addMRUFilesMenuItemUpdateListener(Consumer<Integer> listener);

	void addWindowClosingListener(Runnable listener);

	// --------------------------------------------------------

	void setMRUFilesMenuItem(int index, String path);

	void setFileNameToTitle(String fileName);

	void buildFileMenu();

	@Deprecated
	PaintContext getPaintContext();

	@Deprecated
	CreasePatternViewContext getCreasePattenViewContext();

	@Deprecated
	MouseActionHolder getActionHolder();

	@Deprecated
	Doc getDocument();

	@Deprecated
	StateManager<EditMode> getStateManager();
}
