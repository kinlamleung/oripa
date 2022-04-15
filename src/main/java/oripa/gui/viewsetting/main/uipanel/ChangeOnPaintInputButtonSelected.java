package oripa.gui.viewsetting.main.uipanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.gui.viewsetting.ChangeViewSetting;

public class ChangeOnPaintInputButtonSelected implements ChangeViewSetting {
	private static final Logger logger = LoggerFactory.getLogger(ChangeOnPaintInputButtonSelected.class);
	private final UIPanelSetting setting;

	/**
	 * UIPanel view settings for lineInput edit mode selected
	 */
	public ChangeOnPaintInputButtonSelected(final UIPanelSetting uiPanelSetting) {
		setting = uiPanelSetting;
	}

	@Override
	public void changeViewSetting() {
		logger.debug("change UI to input mode");

		setting.selectInputMode();

		setting.setByValuePanelVisible(false);

		setting.setLineSelectionPanelVisible(false);
		setting.setLineInputPanelVisible(true);

		setting.setAngleStepPanelVisible(false);

		setting.setAlterLineTypePanelVisible(false);
	}

}
