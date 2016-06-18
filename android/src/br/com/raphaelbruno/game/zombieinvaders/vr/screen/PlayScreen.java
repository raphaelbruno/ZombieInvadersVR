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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;

import br.com.raphaelbruno.game.zombieinvaders.vr.GameBase;
import br.com.raphaelbruno.game.zombieinvaders.vr.model.Enemy;
import br.com.raphaelbruno.game.zombieinvaders.vr.model.GameObject;
import br.com.raphaelbruno.game.zombieinvaders.vr.model.Ground;
import br.com.raphaelbruno.game.zombieinvaders.vr.model.ScreenBase;
import br.com.raphaelbruno.game.zombieinvaders.vr.util.MathUtils;

public class PlayScreen extends ScreenBase {
	public static final float ENEMY_MAX_RADIUS_DISTANCE = 25f;
	public static final float ENEMY_MIN_RADIUS_DISTANCE = 1f;
	public static final float ENEMY_TIME_WALK = 20f;
	public static final float TIME_MIN_TO_CREATE = 1f;
	public static final float TIME_MAX_TO_CREATE = 5f;
	public static final float TIME_TO_DECREMENT = .1f;
	
	private Ground level;
	private float currentTimeToCreate;
    
	public PlayScreen(GameBase game) {
		super(game);
	}
	
	@Override
	public void setupScreen() {
		currentTimeToCreate = TIME_MAX_TO_CREATE;
		
		level = new Ground.Builder().build(this, "models/level.g3db");
		instances.add(level);
		
		game.stopBg();
		
		showUI();
		createEnemy();
	}
	
	public void shoot(){
		if(life.isEmpty()) return;
		
		GameObject gameObject = getObject(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		
		soundEffectPlayerShoot.play();
		game.vibrate();
		
		if(gameObject != null && gameObject instanceof Enemy)
			killEnemy((Enemy) gameObject);
	}
	
	private void killEnemy(Enemy enemy) {
		Float distance = new Vector3().dst(enemy.transform.getTranslation(new Vector3()));
		increaseScore(distance.intValue());
		enemy.showScoreDroped(distance.intValue());
		enemy.die();
	}

	@Override
	public void processInput() {
		if(Gdx.input.justTouched()) shoot();
	}
	
	private void createEnemy() {
		double angle = MathUtils.aleatoryRadianAngle();
		Vector3 from = MathUtils.positionFromAngle(angle, ENEMY_MAX_RADIUS_DISTANCE);
		Vector3 until = MathUtils.positionFromAngle(angle, ENEMY_MIN_RADIUS_DISTANCE);
		Enemy aleatoryEnemy = new Enemy.Builder()
				.setPosition(from.x, 0, from.y)
				.setRandomSkin(true)
				.setState(Enemy.State.STANDING)
				.build(this);
		
		Vector3 to = new Vector3(until.x, 0, until.y);
		aleatoryEnemy.lookAt(to);
		aleatoryEnemy.walkTo(to, ENEMY_TIME_WALK, new Enemy.OnAnimationComplete() {
			@Override public void run(GameObject enemy) {
				if(enemy instanceof Enemy)
					((Enemy) enemy).keepAttacking();				
			}
		});
		instances.add(aleatoryEnemy);
		
		if(life != null && life.isEmpty()) return;
		
		Timer.schedule(new Timer.Task() {
			@Override
			public void run() {
				if(currentTimeToCreate > TIME_MIN_TO_CREATE)
					currentTimeToCreate -= TIME_TO_DECREMENT;
				
				createEnemy();
			}
		}, currentTimeToCreate);
	}
	
}
