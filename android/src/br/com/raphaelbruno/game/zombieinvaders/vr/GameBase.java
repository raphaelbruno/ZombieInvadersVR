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

package br.com.raphaelbruno.game.zombieinvaders.vr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.CardBoardGame;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import br.com.raphaelbruno.game.zombieinvaders.vr.model.ScreenBase;
import br.com.raphaelbruno.game.zombieinvaders.vr.screen.GameOverScreen;
import br.com.raphaelbruno.game.zombieinvaders.vr.screen.MenuScreen;
import br.com.raphaelbruno.game.zombieinvaders.vr.screen.PlayScreen;

public class GameBase extends CardBoardGame {
	public ModelBatch modelBatch;
	public SpriteBatch spriteBatch;
	public ZombieInvadersVR launcher;
	public int highScore;
	
	public GameBase(ZombieInvadersVR launcher){
		this.launcher = launcher;
	}
	
	@Override
	public void create () {
		modelBatch = new ModelBatch();
		spriteBatch = new SpriteBatch();
		
		gotoMenu();
	}
	
	public void gotoMenu() {
		setScreen(new MenuScreen(this));
	}
	
	public void gotoPlay() {
		setScreen(new PlayScreen(this));
	}
	
	public void gotoGameOver(int score) {
		if(score > highScore){
			highScore = score;
			launcher.setHighScore(highScore);
		}
		setScreen(new GameOverScreen(this));
	}
	
    public void vibrate(){
    	launcher.vibrate(100);
    }
    
	public void toast(String message) {
		launcher.toast(message);
	}
    
	@Override public void render () {
		super.render(); 
	}
	
	@Override public void onDrawEye(Eye paramEye) {
		if(screen != null && screen instanceof ScreenBase){
			((ScreenBase) screen).onDrawEye(paramEye);
		}
	}
	
	public void shoot() {
		Gdx.app.postRunnable(new Runnable() {
			
			@Override
			public void run() {
				if(screen != null && screen instanceof ScreenBase){
					((ScreenBase) screen).shoot();
				}
			}
		});
	}
	@Override public void onNewFrame(HeadTransform paramHeadTransform) { }
	@Override public void onFinishFrame(Viewport paramViewport) { }
	@Override public void onRendererShutdown() { }
	@Override public void onCardboardTrigger() { }
}
