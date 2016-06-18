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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;

import br.com.raphaelbruno.game.zombieinvaders.vr.GameBase;
import br.com.raphaelbruno.game.zombieinvaders.vr.model.Enemy;
import br.com.raphaelbruno.game.zombieinvaders.vr.model.GameObject;
import br.com.raphaelbruno.game.zombieinvaders.vr.model.Ground;
import br.com.raphaelbruno.game.zombieinvaders.vr.model.LaserRenderer;
import br.com.raphaelbruno.game.zombieinvaders.vr.model.ScreenBase;
import br.com.raphaelbruno.game.zombieinvaders.vr.model.Word;
import br.com.raphaelbruno.game.zombieinvaders.vr.util.AssetRepository;
import br.com.raphaelbruno.game.zombieinvaders.vr.util.HudUtils;

public class GameOverScreen extends ScreenBase {
	private Ground level;
	private Enemy deadEnemy;
	private Word uiHighScore;
	private Word uiPlayAgain;
	private Word uiExit;
	private GameObject uiDoor;

	public GameOverScreen(GameBase game) {
		super(game);
	}
	
	@Override
	public void setupScreen() {
		level = new Ground.Builder().build(this, "models/level.g3db");
		instances.add(level);
		
		deadEnemy = new Enemy.Builder().setPosition(0f, 0f, -2f).build(this);
		deadEnemy.enabled = false;
		deadEnemy.changeState(Enemy.State.DYING, 1);
		instances.add(deadEnemy);
		
		uiPlayAgain = new Word.Builder().build(this, "PLAY AGAIN");
		uiPlayAgain.moveTo(0f, 1f, -5f);
		instances.add(uiPlayAgain);
		
		uiHighScore = new Word.Builder().build(this, "HIGH SCORE", HudUtils.formattedScore(game.highScore));
		uiHighScore.enabled = false;
		uiHighScore.moveTo(0f, 3f, -5f);
		instances.add(uiHighScore);
		
		uiExit = new Word.Builder().build(this, "EXIT");
		uiExit.moveTo(0f, 2.5f, 5f);
		uiExit.rotateTo(180);
		instances.add(uiExit);
		
		uiDoor = new GameObject(this, AssetRepository.getInstance().load("models/door.g3db", Model.class));
		uiDoor.moveTo(0,0,5f);
		uiDoor.rotateTo(180f);
		instances.add(uiDoor);
		
		if(!game.soundBg.isPlaying())
			game.playBg();
	}
	
	public void shoot(){
		if(!laser.isPlaying){
			laser.play(new LaserRenderer.OnLaserFinished() {
				@Override public void run() {
			
					GameObject obj = getObject(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
					
					if(obj == uiPlayAgain){
						soundEffectUI.play();
						game.gotoPlay();
					}
					if(obj == uiExit || obj == uiDoor){
						soundEffectUI.play();
						Gdx.app.exit();
					}
					
				}
			});
			soundEffectPlayerShoot.play();
			game.vibrate();
		}
	}
	
	@Override
	public void processInput() {
		if(Gdx.input.justTouched()) shoot();
	}
	
	@Override
	public void render(float delta) {
		uiPlayAgain.animate(delta);
		uiHighScore.animate(delta);
		uiExit.animate(delta);
		super.render(delta);
	}
}
