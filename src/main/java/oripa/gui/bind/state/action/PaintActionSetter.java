package oripa.gui.bind.state.action;

import oripa.domain.paint.PaintContext;
import oripa.gui.presenter.creasepattern.GraphicMouseAction;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.creasepattern.ScreenUpdater;

/**
 * Add this listener to Button object or something for selecting paint action.
 *
 * @author koji
 *
 */
public class PaintActionSetter implements Runnable {

	private final GraphicMouseAction mouseAction;
	private final MouseActionHolder actionHolder;
	private final ScreenUpdater screenUpdater;
	private final PaintContext context;

	public PaintActionSetter(final MouseActionHolder anActionHolder,
			final GraphicMouseAction thisMouseAction,
			final ScreenUpdater screenUpdater,
			final PaintContext aContext) {
		actionHolder = anActionHolder;
		mouseAction = thisMouseAction;
		this.screenUpdater = screenUpdater;
		context = aContext;
	}

	@Override
	public void run() {

		GraphicMouseAction currentAction = actionHolder
				.getMouseAction();
		if (currentAction != null) {
			currentAction.destroy(context);
		}
		mouseAction.recover(context);

		actionHolder.setMouseAction(mouseAction);

		screenUpdater.updateScreen();
	}

}
