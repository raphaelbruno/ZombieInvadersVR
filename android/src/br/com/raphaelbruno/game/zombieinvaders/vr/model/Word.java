/*******************************************************************************
 * Copyright 2016 Raphael Bruno Alves de Sá (raphaelbruno2.0@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package br.com.raphaelbruno.game.zombieinvaders.vr.model;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import br.com.raphaelbruno.game.zombieinvaders.vr.util.AssetRepository;

public class Word extends GameObject {
	static public final float ANIMATION_VELOCITY = .15f;
	static public final float ANIMATION_LIMIT = .025f;
	static public final float FONT_WIDTH = .5f;
	static public final float FONT_HEIGHT = 0.65f;
	static public final float FONT_DEPTH = 0.25f;
	public final String[] translateFrom = new String[]{".", "=", "+", "*", "/", "%", "!", "?", "@", "#", "&"};
	public final String[] translateTo = new String[]{"__10", "__9", "__8", "__7", "__6", "__5", "__4", "__3", "__2", "__1", "__0"};
	public final String[] words;
	private Array<Vector3> nodesPosition;
	private float currentAnimationPosition = 0;
	private float currentAnimationOrientation = 1;
	
	public Word(ScreenBase context, Model model) {
		this(context, model, new String[]{});
	}

	public Word(ScreenBase context, Model model, String... word) {
		super(context, model);
		this.words = word;
		init();
	}

	private void init() {
		float centerX = 0f;
		float centerY = ((((float) words.length) * FONT_HEIGHT) - FONT_HEIGHT) / 2f;
		
		nodes.clear();
		nodesPosition = new Array<Vector3>();
		if(words != null && words.length > 0){
			for(int y = 0; y < words.length; y++){
				String[] word = translateSpecialCharacters(words[y]);
				centerX = (((float) word.length) * FONT_WIDTH) / 2f;
				for(int x = 0; x < word.length; x++){
					String letter = String.valueOf(word[x]);
					
					Node node = model.getNode(letter);
					if(node != null){
						Vector3 position = new Vector3((FONT_WIDTH*x) - centerX,(-FONT_HEIGHT*y) + centerY,0);
						node = node.copy();
						node.globalTransform.setTranslation(position);
						nodes.add(node);
						nodesPosition.add(position.cpy());
					}
				}
			}
		}
	}

	private String[] translateSpecialCharacters(String word) { return translateSpecialCharacters(word.split("")); }
	private String[] translateSpecialCharacters(String[] word) {
		String[] returns = new String[word.length];
		for(int i = 0; i < word.length; i ++){
			String letterToSend = word[i];
			for(int j = 0; j < translateFrom.length; j++)
				if(translateFrom[j].equals(word[i]))
					letterToSend = translateTo[j];
			returns[i] = letterToSend;
		}
		return returns;
	}

	public static class Builder {
		public Builder(){
		}
		
		public Word build(ScreenBase context, String... words){
			Word word;
			if(words != null) word = new Word(context, AssetRepository.getInstance().load("fonts/font.g3db", Model.class), words);
			else word = new Word(context, AssetRepository.getInstance().load("fonts/font.g3db", Model.class));
			return word;
		}
		
	}
	
	public void animate(float delta) {
		if(nodes.size > 0){
			currentAnimationPosition += (ANIMATION_VELOCITY*delta) * currentAnimationOrientation;
			
			int invert = 1;
			for (int i = 0; i < nodes.size; i++) {
				Vector3 nodePosition = nodesPosition.get(i);
				nodes.get(i).globalTransform.setTranslation(nodePosition.x, nodePosition.y + (invert*currentAnimationPosition), nodePosition.z);
				invert = -invert;
			}
			
			if(currentAnimationPosition >= ANIMATION_LIMIT) currentAnimationOrientation = -1;
			if(currentAnimationPosition <= -ANIMATION_LIMIT) currentAnimationOrientation = 1;
		}
	}

}