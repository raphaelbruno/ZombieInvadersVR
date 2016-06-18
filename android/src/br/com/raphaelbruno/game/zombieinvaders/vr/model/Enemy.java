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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Timer;

import br.com.raphaelbruno.game.zombieinvaders.vr.tween.GameObjectAccessor;
import br.com.raphaelbruno.game.zombieinvaders.vr.util.AssetRepository;

public class Enemy extends GameObject {
	public final static BoundingBox ENEMY_BOUNDS = new BoundingBox( new Vector3(-.2f, -0f, -.1f), new Vector3(.2f, 1.5f, .5f) );
	public static float TOTAL_GROAN = 2;
	public static float TIME_TO_ATTACK = 3;
	public static float TIME_TO_DIE = 3;
	public Enemy.State state;
	
	public Enemy(ScreenBase context, Model model) {
		this(context, model, ENEMY_BOUNDS);
	}
	public Enemy(ScreenBase context, Model model, BoundingBox bounds) {
		super(context, model, bounds);
	}
	
	private void groans(float delay) {
		if(context.life.isEmpty()) return;
		Timer.schedule(new Timer.Task() {
			@Override
			public void run() {
				if(!context.life.isEmpty() && !state.equals(Enemy.State.DYING)){
					float distance = new Vector3().dst(transform.getTranslation(new Vector3()));
					int randomGroan = (int) Math.floor(context.soundsEffectEnemyGroan.size()*Math.random());
					context.soundsEffectEnemyGroan.get(randomGroan).play(1f - (distance/ScreenBase.Z_FAR));
				}
			}
		}, delay);
	}
	
	public void walkTo(Vector3 position, float time, final GameObject.OnAnimationComplete onAnimationComplete) {
		changeState(Enemy.State.WALKING);
		moveToAnimation(position, time, new GameObject.OnAnimationComplete() {
			@Override public void run(GameObject object) {
				changeState(Enemy.State.STANDING);
				if(onAnimationComplete != null) onAnimationComplete.run(object);
			}
		});
		
		for(float i = 1f; i <= TOTAL_GROAN; i++){
			groans(i * (time/TOTAL_GROAN));
		}
	}
	
	public void setRandomSkin(){
		Texture texture = AssetRepository.getInstance().load(Enemy.Skin.randomSkin().getUrl(), Texture.class);
        for(Material item : materials)
			item.set(TextureAttribute.createDiffuse(texture));
	}
	
	public void setSkin(Enemy.Skin skin){
		Texture texture = AssetRepository.getInstance().load(skin.getUrl(), Texture.class);
        for(Material item : materials)
			item.set(TextureAttribute.createDiffuse(texture));
	}
	
	public void changeState(State state) { changeState(state, -1, null); }
	public void changeState(State state, int loops) { changeState(state, loops, null); }
	public void changeState(State state, final GameObject.OnAnimationComplete onAnimationComplete) { changeState(state, -1, onAnimationComplete); }
	public void changeState(State state, int loops, final GameObject.OnAnimationComplete onAnimationComplete) {
		final Enemy enemy = this;
		this.state = state;
		animations.setAnimation(state.getAction(), loops, new AnimationController.AnimationListener() {
			@Override public void onLoop(AnimationDesc animation) { }
			@Override public void onEnd(AnimationDesc animation) {
				if(onAnimationComplete != null)
					onAnimationComplete.run(enemy);
			}
		});
	}
	
	public void keepAttacking() {
		changeState(Enemy.State.ATTACKING, 1, new GameObject.OnAnimationComplete() {
			@Override public void run(GameObject object) {
				changeState(Enemy.State.STANDING);
			}
		});
		
		context.removeLife();
		if(context.life.isEmpty()) return;
		
		int randomAttack = (int) Math.floor(context.soundsEffectEnemyAttack.size()*Math.random());
		context.soundsEffectEnemyAttack.get(randomAttack).play();
		
		Timer.schedule(new Timer.Task() {
			@Override
			public void run() {
				if(!state.equals(Enemy.State.DYING))
					keepAttacking();
			}
		}, TIME_TO_ATTACK);		
	}
	
	public void die() {
	    final Enemy enemy = this;
	    enabled = false;
	    context.tweenManager.killTarget(this, GameObjectAccessor.XYZ);
	    context.tweenManager.killTarget(this, GameObjectAccessor.ROTATION);
	    
		changeState(Enemy.State.DYING, 1);
		
		Timer.schedule(new Timer.Task() {
			@Override
			public void run() {
				Vector3 position = enemy.transform.getTranslation(new Vector3());
				enemy.moveToAnimation(position.x, -0.5f, position.z, 2, new GameObject.OnAnimationComplete() {
					@Override public void run(GameObject object) {
						context.instances.remove(enemy);
					}
				});
			}
		}, TIME_TO_ATTACK);		
	}
	
	public void showScoreDroped(int score) {
		final Word scoreModel = new Word.Builder().build(context, Integer.valueOf(score).toString());
		Vector3 scorePosition = transform.getTranslation(new Vector3());
		scorePosition.y = 2f;
		
		context.instances.add(scoreModel);
		scoreModel.moveTo(scorePosition);
		scoreModel.lookAt(new Vector3(0, scorePosition.y, 0));
		scoreModel.moveToAnimation(scorePosition.x, scorePosition.y+1, scorePosition.z, 2, new GameObject.OnAnimationComplete() {
			@Override
			public void run(GameObject object) {
				context.instances.remove(scoreModel);
			}
		});
	}
	
	
	
	
	
	
	
	public enum State {
		STANDING("stand"), WALKING("walk"), ATTACKING("attack"), DYING("die");
		
		private String action;
		
		State(String action){
			this.action = action;
		}
		
		public String getAction(){
			return this.action;
		}
	}

	public enum Model3D {
		DEFAULT_ZOMBIE("models/enemy.g3db");
		
		private String url;
		
		Model3D(String url){
			this.url = url;
		}
		
		public String getUrl(){
			return this.url;
		}
	}
	
	public enum Skin {
		DEFAULT_ZOMBIE("models/enemy.jpg"), PALE_ZOMBIE("models/pale_enemy.jpg"), DARK_ZOMBIE("models/dark_enemy.jpg");
		
		private String url;
		
		Skin(String url){
			this.url = url;
		}
		
		public static Skin randomSkin() {
			int random = (int) Math.floor(Math.random() * Skin.values().length);
			return Skin.values()[random];
		}

		public String getUrl(){
			return this.url;
		}
	}
	
	
	
	public static class Builder {
		private Vector3 position;
		private Float rotation;
		private Float alpha;
		private boolean randomSkin;
		private Enemy.State state;
		
		public Builder(){
			this(null, 0f, null);
		}
		
		public Builder(Vector3 position, float rotation, Enemy.State state){
			this.position = position;
			this.rotation = rotation;
			this.state = state;
		}
		
		public Enemy.Builder setPosition(float x, float y, float z){ return setPosition(new Vector3(x, y, z)); }
		public Enemy.Builder setPosition(Vector3 position){
			this.position = position;
			return this;
		}
		
		public Enemy.Builder setRotation(float rotation){
			this.rotation = rotation;
			return this;
		}
		
		public Enemy.Builder setAlpha(float alpha){
			this.alpha = alpha;
			return this;
		}
		
		public Enemy.Builder setRandomSkin(boolean randomSkin){
			this.randomSkin = randomSkin;
			return this;
		}
		
		public Enemy.Builder setState(Enemy.State state){
			this.state = state;
			return this;
		}
		
		public Enemy build(ScreenBase context){ return build(context, Enemy.Model3D.DEFAULT_ZOMBIE); }
		public Enemy build(ScreenBase context, Enemy.Model3D model){
			Enemy enemy;
			
			enemy = new Enemy(context, AssetRepository.getInstance().load(model.getUrl(), Model.class));
			
			if(state != null) enemy.changeState(state);
			if(position != null) enemy.moveTo(position);
			if(rotation != null) enemy.rotateTo(rotation);
			if(alpha != null) enemy.alphaTo(alpha);
			if(randomSkin) enemy.setRandomSkin();
			
			return enemy;
		}
		
	}


}