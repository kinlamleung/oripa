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

import java.awt.Component;

import javax.swing.JOptionPane;

import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

/**
 * @author OUCHI Koji
 *
 */
public class MainDialogService {
	private final ResourceHolder resources;

	public MainDialogService(final ResourceHolder resources) {
		this.resources = resources;
	}

	public int showCleaningUpDuplicationDialog(final Component ownerView) {
		return JOptionPane.showConfirmDialog(
				ownerView,
				resources.getString(
						ResourceKey.WARNING,
						StringID.Warning.FOLD_FAILED_DUPLICATION_ID),
				resources.getString(
						ResourceKey.WARNING,
						StringID.Warning.FAILED_TITLE_ID),
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);
	}

	public void showCleaningUpMessage(final Component ownerView) {
		JOptionPane.showMessageDialog(
				ownerView,
				resources.getString(ResourceKey.INFO,
						StringID.Information.SIMPLIFYING_CP_ID),
				resources.getString(ResourceKey.INFO,
						StringID.Information.SIMPLIFYING_CP_TITLE_ID),
				JOptionPane.INFORMATION_MESSAGE);
	}

	public void showFoldFailureMessage(final Component ownerView) {
		JOptionPane.showMessageDialog(
				ownerView,
				resources.getString(
						ResourceKey.WARNING,
						StringID.Warning.FOLD_FAILED_WRONG_STRUCTURE_ID),
				resources.getString(
						ResourceKey.WARNING,
						StringID.Warning.FAILED_TITLE_ID),
				JOptionPane.WARNING_MESSAGE);
	}

	public void showNoAnswerMessage(final Component ownerView) {
		JOptionPane.showMessageDialog(
				ownerView,
				resources.getString(ResourceKey.INFO, StringID.Information.NO_ANSWER_ID),
				resources.getString(ResourceKey.INFO,
						StringID.Information.FOLD_ALGORITHM_TITLE_ID),
				JOptionPane.INFORMATION_MESSAGE);
	}
}
