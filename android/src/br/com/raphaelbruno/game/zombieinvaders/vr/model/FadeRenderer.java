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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Timer;

public class FadeRenderer extends ShapeRenderer {
	
	private final float ALPHA_VELOCITY = 0.15f;
	private final float DELAY_TO_FINISH = 1f;
	private float alpha = 0f;
	private float red = 1f;
	private OnFinished onFinished;
	
	public boolean isPlaying = false;
	private boolean isRunning = false;
	
	public void setOnFinished(OnFinished onFinished){
		this.onFinished = onFinished;
	}
	
	public void shows(float delta){
		if(!isPlaying) return;
		
		if(isRunning){
			alpha += ALPHA_VELOCITY*delta;
			if(alpha > 1f){
				alpha = 1f;
				if(isRunning && onFinished != null){ 
					Timer.schedule(new Timer.Task() {
						@Override public void run() {
							onFinished.run();
						}
					}, DELAY_TO_FINISH);
					isRunning = false;
				}
			}
			red = 1f-alpha;
		}
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		begin(ShapeType.Filled);
		setColor(red, 0f, 0f, alpha);
		rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	public void start(){
		isPlaying = true;
		isRunning = true;
	}
	
	public void stop(){
		alpha = 0;
		isPlaying = false;
		isRunning = false;
	}
	
	public void pause(){
		isPlaying = false;
	}
	
	interface OnFinished {
		public void run();
	}
}