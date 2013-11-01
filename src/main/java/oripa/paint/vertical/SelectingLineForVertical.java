package oripa.paint.vertical;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.geom.GeomUtil;
import oripa.paint.CreasePatternInterface;
import oripa.paint.PaintContextInterface;
import oripa.paint.core.PaintConfig;
import oripa.paint.core.PickingLine;
import oripa.paint.cptool.Painter;
import oripa.value.OriLine;

public class SelectingLineForVertical extends PickingLine {

	@Override
	protected void initialize() {
		setPreviousClass(SelectingVertexForVertical.class);
		setNextClass(SelectingVertexForVertical.class);

	}

	
	
	@Override
	protected void undoAction(PaintContextInterface context) {
		context.clear(false);
	
	}

	

	@Override
	protected void onResult(PaintContextInterface context) {
		if(context.getLineCount() != 1 || 
				context.getVertexCount() != 1){
			throw new RuntimeException();
		}
		
        OriLine vl = GeomUtil.getVerticalLine(
        		context.getVertex(0), context.getLine(0), PaintConfig.inputLineType);

        Doc document = ORIPA.doc;
		CreasePatternInterface creasePattern = document.getCreasePattern();

        document.pushUndoInfo();

        Painter painter = new Painter();
        painter.addLine(vl, creasePattern);

        context.clear(false);
	}

}