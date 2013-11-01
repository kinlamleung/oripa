/**
 * ORIPA - Origami Pattern Editor 
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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

package oripa.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.cutmodel.CutModelOutlineFactory;
import oripa.fold.FoldedModelInfo;
import oripa.fold.OrigamiModel;
import oripa.paint.CreasePatternFactory;
import oripa.paint.CreasePatternInterface;
import oripa.paint.CreasePatternUndoManager;
import oripa.paint.history.CreasePatternUndoFactory;
import oripa.resource.Constants;
import oripa.util.history.AbstractUndoManager;
import oripa.util.history.UndoInfo;
import oripa.value.OriLine;


public class Doc {

	
	private double paperSize;

	// Crease Pattern

	private CreasePatternInterface creasePattern = null;
	private List<OriLine> sheetCutLines = new ArrayList<OriLine>();


	// Origami Model for Estimation
	private OrigamiModel origamiModel = null;

		
	// Folded Model Information (Result of Estimation)

	private FoldedModelInfo foldedModelInfo = null;
	

	// Project data

	private String dataFilePath = "";
	private String title;
	private String editorName;
	private String originalAuthorName;
	private String reference;
	private String memo;
	private AbstractUndoManager<Collection<OriLine>> undoManager =
			new CreasePatternUndoManager(30);



	int debugCount = 0;


	public Doc(){
		initialize(Constants.DEFAULT_PAPER_SIZE);
	}   

	public Doc(double size) {
		initialize(size);
	}

	public void set(Doc doc) {
		setCreasePattern(doc.getCreasePattern());
		setOrigamiModel(doc.getOrigamiModel());
		setFoldedModelInfo(doc.getFoldedModelInfo());
		sheetCutLines = doc.getSheetCutOutlines();
		setPaperSize(doc.getPaperSize());
		setDataFilePath(doc.getDataFilePath());
		setTitle(doc.getTitle());
		setEditorName(doc.getEditorName());
		setOriginalAuthorName(doc.getOriginalAuthorName());
		setReference(doc.getReference());
		setMemo(doc.getMemo());
		undoManager = doc.undoManager;
	}
	
	private void initialize(double size){

		this.paperSize = size;
		creasePattern = (new CreasePatternFactory()).createCreasePattern(size);


		
		origamiModel  = new OrigamiModel(size);
		foldedModelInfo = new FoldedModelInfo();
	}

	public void setDataFilePath(String path){
		this.dataFilePath = path;
	}

	public String getDataFilePath(){
		return dataFilePath;
	}

	public String getDataFileName(){
		File file = new File(ORIPA.doc.dataFilePath);
		String fileName = file.getName();

		return fileName;

	}


	CreasePatternUndoFactory factory = new CreasePatternUndoFactory();

	public UndoInfo<Collection<OriLine>> createUndoInfo(){
		UndoInfo<Collection<OriLine>> undoInfo = factory.create(creasePattern);
		return undoInfo;
	}

	public void cacheUndoInfo(){
		undoManager.setCache(creasePattern);
	}

	public void pushCachedUndoInfo(){
		undoManager.pushCachedInfo();
	}

	public void pushUndoInfo() {
		undoManager.push(creasePattern);
	}

	public void pushUndoInfo(UndoInfo<Collection<OriLine>> uinfo){
		undoManager.push(uinfo);
	}

	public void loadUndoInfo() {
		UndoInfo<Collection<OriLine>> info = undoManager.pop();

		if(info == null){
			return;
		}

		creasePattern.clear();
		creasePattern.addAll(info.getInfo());
	}

	public boolean canUndo(){
		return undoManager.canUndo();
	}

	public boolean isChanged(){
		return undoManager.isChanged();
	}

	public void clearChanged(){
		undoManager.clearChanged();
	}


	/**
	 * make lines that composes the outline of a shape
	 * obtained by cutting the folded model.
	 * @param scissorLine
	 */
	public void updateSheetCutOutlines(OriLine scissorLine) {
		CutModelOutlineFactory factory = new CutModelOutlineFactory();

		sheetCutLines.clear();
		sheetCutLines.addAll(
				factory.createLines(scissorLine, origamiModel));
	}



		
	public Collection<Vector2d> getVerticesAround(Vector2d v){
		return creasePattern.getVerticesAround(v);
	}
	
	public Collection<Collection<Vector2d>> getVerticesArea(
			double x, double y, double distance){
		
		return creasePattern.getVerticesInArea(x, y, distance);
	}
	
	public CreasePatternInterface getCreasePattern(){
		return creasePattern;
	}

	public void setCreasePattern(CreasePatternInterface cp){
		creasePattern = cp;
	}
	/**
	 * @return origamiModel
	 */
	public OrigamiModel getOrigamiModel() {
		return origamiModel;
	}
	
	/**
	 * @param origamiModel origamiModel is set to this instance.
	 */
	public void setOrigamiModel(OrigamiModel origamiModel) {
		this.origamiModel = origamiModel;
	}
	
	

	/**
	 * @return foldedModelInfo
	 */
	public FoldedModelInfo getFoldedModelInfo() {
		return foldedModelInfo;
	}

	/**
	 * @param foldedModelInfo foldedModelInfo is set to this instance.
	 */
	public void setFoldedModelInfo(FoldedModelInfo foldedModelInfo) {
		this.foldedModelInfo = foldedModelInfo;
	}

	//======================================================================
	// Getter/Setter eventually unnecessary
	

	/**
	 * @return crossLines
	 */
	public List<OriLine> getSheetCutOutlines() {
		return sheetCutLines;
	}

//	/**
//	 * @param crossLines crossLines is set to this instance.
//	 */
//	public void setCrossLines(List<OriLine> sheetCutOutlines) {
//		this.sheetCutLines = sheetCutOutlines;
//	}



	/**
	 * @param size size is set to this instance.
	 */
	public void setPaperSize(double size) {
		this.paperSize = size;
//		origamiModel.setPaperSize(size);
		creasePattern.changePaperSize(size);
	}
	/**
	 * @return size
	 */
	public double getPaperSize() {
		return paperSize;
	}

	/**
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title title is set to this instance.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return editorName
	 */
	public String getEditorName() {
		return editorName;
	}

	/**
	 * @param editorName editorName is set to this instance.
	 */
	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}

	/**
	 * @return originalAuthorName
	 */
	public String getOriginalAuthorName() {
		return originalAuthorName;
	}

	/**
	 * @param originalAuthorName originalAuthorName is set to this instance.
	 */
	public void setOriginalAuthorName(String originalAuthorName) {
		this.originalAuthorName = originalAuthorName;
	}

	/**
	 * @return memo
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * @param memo memo is set to this instance.
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * @return reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference reference is set to this instance.
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	
	
	
}