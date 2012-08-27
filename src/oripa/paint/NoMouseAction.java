package oripa.paint;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2d;

public class NoMouseAction extends GraphicMouseAction {

	@Override
	public GraphicMouseAction onLeftClick(MouseContext context, AffineTransform affine,
			MouseEvent event) {

		return this;
	}

	@Override
	public void onRightClick(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public Vector2d onMove(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDragged(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReleased(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDraw(Graphics2D g2d, MouseContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPressed(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

}
