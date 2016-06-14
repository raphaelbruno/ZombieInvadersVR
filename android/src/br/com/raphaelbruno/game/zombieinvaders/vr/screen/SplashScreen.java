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

package br.com.raphaelbruno.game.zombieinvaders.vr.screen;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import br.com.raphaelbruno.game.zombieinvaders.vr.GameBase;
import br.com.raphaelbruno.game.zombieinvaders.vr.model.ScreenBase;

public class SplashScreen extends ScreenBase {
	
	private final int TIME_BY_LOGO = 5000;
	
	private GameBase game;
	private List<Texture> logos;
	private int index;
	
	public SplashScreen(GameBase game){
		super(game);
		this.game = game;
		
		logos = new ArrayList<Texture>();
		logos.add(new Texture("sprites/logo.png"));
		
		setBackgroundColor(new Color(0f, 0f, 0f, 1f));
		showTarget = false;
		
		showNewLogo();
	}
	
	Thread proccess;
	
	private void showNewLogo() {
		if(proccess != null){
			proccess.interrupt();
			index++;
			if(index >= logos.size()){
				game.gotoMenu();
				return;
			}

		}
		proccess = new Thread(new Runnable() {
			@Override public void run() {
				try {
					Thread.sleep(TIME_BY_LOGO);
					
					Gdx.app.postRunnable(new Runnable() {
						@Override public void run() {
							showNewLogo();
						}
					});
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				
			}
		});
		proccess.start();
	}

	public void processInput(){
		if(Gdx.input.justTouched()){
			showNewLogo();
		}
	}
	
	@Override
	public void renderSprite(float delta) {
		if(index < logos.size()){
			Texture logo = logos.get(index);
			game.spriteBatch.draw(logo, (Gdx.graphics.getWidth()-logo.getWidth())/2, (Gdx.graphics.getHeight()-logo.getHeight())/2);
		}
	}

	@Override
	public void dispose() {
		game.spriteBatch.dispose();
		game.modelBatch.dispose();
		for(Texture item : logos) item.dispose();
	}
	
	@Override public void setupScreen() { }
	@Override public void shoot() { }
	
}
