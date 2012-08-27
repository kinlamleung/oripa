package oripa.paint.vertical;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2d;

import oripa.geom.OriLine;
import oripa.paint.GraphicMouseAction;
import oripa.paint.MouseContext;

public class VerticalLineAction extends GraphicMouseAction {


	public VerticalLineAction(){
		setActionState(new SelectingVertexForVertical());
	}



	private OriLine closeLine = null;

	@Override
	public Vector2d onMove(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		Vector2d result = super.onMove(context, affine, event);

		if(context.getVertexCount() == 1){
			if(closeLine != null){
				closeLine.selected = false;
			}
			
			closeLine = context.pickCandidateL;
	
			if(closeLine != null){
				closeLine.selected = true;
			}
		}		
		return result;
	}




	@Override
	public void onDragged(MouseContext context, AffineTransform affine, MouseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReleased(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onDraw(Graphics2D g2d, MouseContext context) {

		super.onDraw(g2d, context);


		if(context.getVertexCount() == 0){

			drawPickCandidateVertex(g2d, context);
		}
		else if(context.getVertexCount() == 1){
			drawPickCandidateLine(g2d, context);
			
		}
	}




	@Override
	public void onPressed(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

}
