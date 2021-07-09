package oripa.bind.state.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.domain.paint.PaintContextInterface;
import oripa.gui.presenter.creasepattern.GraphicMouseActionInterface;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.creasepattern.ScreenUpdaterInterface;

/**
 * Add this listener to Button object or something for selecting paint action.
 *
 * @author koji
 *
 */
public class PaintActionSetter implements ActionListener {

	private final GraphicMouseActionInterface mouseAction;
	private final MouseActionHolder actionHolder;
	private final ScreenUpdaterInterface screenUpdater;
	private final PaintContextInterface context;

	public PaintActionSetter(final MouseActionHolder anActionHolder,
			final GraphicMouseActionInterface thisMouseAction,
			final ScreenUpdaterInterface screenUpdater,
			final PaintContextInterface aContext) {
		actionHolder = anActionHolder;
		mouseAction = thisMouseAction;
		this.screenUpdater = screenUpdater;
		context = aContext;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {

		GraphicMouseActionInterface currentAction = actionHolder
				.getMouseAction();
		if (currentAction != null) {
			currentAction.destroy(context);
		}
		mouseAction.recover(context);

		actionHolder.setMouseAction(mouseAction);

		screenUpdater.updateScreen();
	}

}
