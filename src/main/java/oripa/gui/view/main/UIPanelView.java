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
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.event.DocumentListener;

import oripa.domain.paint.AngleStep;
import oripa.gui.view.View;
import oripa.gui.viewsetting.main.uipanel.UIPanelSetting;

/**
 * @author OUCHI Koji
 *
 */
public interface UIPanelView extends View {
	default JPanel asPanel() {
		return (JPanel) this;
	}

	UIPanelSetting getUIPanelSetting();

	void initializeButtonSelection();

	void addGridSmallButtonListener(Runnable listener);

	void addGridLargeButtonListener(Runnable listener);

	void addGridChangeButtonListener(Consumer<Integer> listener);

	void setGridDivNum(int gridDivNum);

	void setEstimationResultColors(Color front, Color back);

	void setBuildButtonEnabled(boolean enabled);

	boolean getFullEstimation();

	Color getEstimationResultFrontColor();

	Color getEstimationResultBackColor();

	void addEditModeInputLineButtonListener(ActionListener listener, KeyListener keyListener);

	void addEditModeLineSelectionButtonListener(ActionListener listener, KeyListener keyListener);

	void addEditModeDeleteLineButtonListener(ActionListener listener, KeyListener keyListener);

	void addEditModeLineTypeButtonListener(ActionListener listener, KeyListener keyListener);

	void addAlterLineComboFromListener(ItemListener listener);

	void addAlterLineComboToListener(ItemListener listener);

	void addEditModeAddVertexButtonListener(ActionListener listener, KeyListener keyListener);

	void addEditModeDeleteVertexButtonListener(ActionListener listener, KeyListener keyListener);

	void addSelectionButtonListener(ActionListener listener, KeyListener keyListener);

	void addEnlargementButtonListener(ActionListener listener, KeyListener keyListener);

	void addLineInputDirectVButtonListener(ActionListener listener, KeyListener keyListener);

	void addLineInputOnVButtonListener(ActionListener listener, KeyListener keyListener);

	void addLineInputVerticalLineButtonListener(ActionListener listener, KeyListener keyListener);

	void addLineInputAngleBisectorButtonListener(ActionListener listener, KeyListener keyListener);

	void addLineInputTriangleSplitButtonListener(ActionListener listener, KeyListener keyListener);

	void addLineInputSymmetricButtonListener(ActionListener listener, KeyListener keyListener);

	void addLineInputMirrorButtonListener(ActionListener listener, KeyListener keyListener);

	void addLineInputByValueButtonListener(ActionListener listener, KeyListener keyListener);

	void addLengthButtonListener(ActionListener listener);

	void addAngleButtonListener(ActionListener listener);

	void addLengthTextFieldListener(DocumentListener listener);

	void addAngleTextFieldListener(DocumentListener listener);

	void addLineInputPBisectorButtonListener(ActionListener listener, KeyListener keyListener);

	void addLineInputAngleSnapButtonListener(ActionListener listener, KeyListener keyListener);

	void addAngleStepComboListener(Consumer<AngleStep> listener);

	void addLineTypeMountainButtonListener(Runnable listener);

	void addLineTypeValleyButtonListener(Runnable listener);

	void addLineTypeAuxButtonListener(Runnable listener);

	void addDispGridCheckBoxListener(Consumer<Boolean> listener);

	void addDispVertexCheckBoxListener(Consumer<Boolean> listener);

	void addDispMVLinesCheckBoxListener(Consumer<Boolean> listener);

	void addDispAuxLinesCheckBoxListener(Consumer<Boolean> listener);

	void addZeroLineWidthCheckBoxListener(Consumer<Boolean> listener);

	void addCheckWindowButtonListener(Runnable listener);

	void addBuildButtonListener(Runnable listener);

	BiConsumer<Color, Color> getEstimationResultSaveColorsListener();

	PropertyChangeListener getPaperDomainOfModelChangeListener();

}
