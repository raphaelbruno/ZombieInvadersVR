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

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.android.CardboardCamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.google.vrtoolkit.cardboard.Eye;

import aurelienribon.tweenengine.TweenManager;
import br.com.raphaelbruno.game.zombieinvaders.vr.GameBase;
import br.com.raphaelbruno.game.zombieinvaders.vr.util.AssetRepository;
import br.com.raphaelbruno.game.zombieinvaders.vr.util.FontUtils;
import br.com.raphaelbruno.game.zombieinvaders.vr.util.HudUtils;

public abstract class ScreenBase implements Screen {
	private Color BACKGROUND_COLOR = new Color(0.675f, 0.675f, 0.7f, 1f);
	private final int MARGIN_SCREEN = 220;
	private final int MARGIN_HEART = 5;
	private final int INITIAL_LIFE = 5;
	private final String TEXTURE_HEART = "sprites/heart.png";
	private final String TEXTURE_TARGET = "sprites/target.png";
	private boolean visibleUI = false;
	public static final float Z_NEAR = 0.1f;
    public static final float Z_FAR = 25.0f;
    public static final float CAMERA_Y = 1.6f;
	
    public final GameBase game;
	
	private CardboardCamera camera;
	private Environment environment;
	private Texture target;
	private Texture heart;
	private BitmapFont score;
	private int scoreValue;
	
	
	public List<Texture> life;
	public TweenManager tweenManager;
	public List<ModelInstance> instances;
	public FadeRenderer endFade;
	public LaserRenderer laser;
	
    public boolean showTarget = true;
	
	public Sound soundEffectPlayerShoot;
	public Sound soundEffectPlayerDying;
	public Sound soundEffectEnemyEating;
	public Sound soundEffectUI;
	public List<Sound> soundsEffectEnemyAttack;
	public List<Sound> soundsEffectEnemyGroan;
	
	
	public abstract void setupScreen();
	public abstract void processInput();
	public abstract void shoot();
	
	
	public ScreenBase(GameBase game){
		this.game = game;
		init();
	}
	
	public void init(){
		tweenManager = new TweenManager();
		instances = new ArrayList<ModelInstance>();
		endFade = new FadeRenderer();
		laser = new LaserRenderer();
		
		setupSound();
		setupEnviroment();
		setupScreen();
		setupUi();
	}
	
	private void setupSound() {
		soundEffectPlayerShoot = Gdx.audio.newSound(Gdx.files.internal("sounds/player_shoot.mp3"));
		soundEffectPlayerDying = Gdx.audio.newSound(Gdx.files.internal("sounds/player_dying.mp3"));
		soundEffectEnemyEating = Gdx.audio.newSound(Gdx.files.internal("sounds/enemy_eating.mp3"));
		soundEffectUI = Gdx.audio.newSound(Gdx.files.internal("sounds/ui.mp3"));;
		
		soundsEffectEnemyAttack = new ArrayList<Sound>();
		soundsEffectEnemyAttack.add(Gdx.audio.newSound(Gdx.files.internal("sounds/enemy_attack1.mp3")));
		soundsEffectEnemyAttack.add(Gdx.audio.newSound(Gdx.files.internal("sounds/enemy_attack2.mp3")));
		soundsEffectEnemyAttack.add(Gdx.audio.newSound(Gdx.files.internal("sounds/enemy_attack3.mp3")));
		soundsEffectEnemyAttack.add(Gdx.audio.newSound(Gdx.files.internal("sounds/enemy_attack4.mp3")));
		
		soundsEffectEnemyGroan = new ArrayList<Sound>();
		soundsEffectEnemyGroan.add(Gdx.audio.newSound(Gdx.files.internal("sounds/enemy_breath1.mp3")));
		soundsEffectEnemyGroan.add(Gdx.audio.newSound(Gdx.files.internal("sounds/enemy_breath2.mp3")));
	}
	
	private void setupEnviroment() {
		camera = new CardboardCamera();
		camera.position.set(0f, CAMERA_Y, 0f);
		camera.lookAt(0f, CAMERA_Y, -1f);
		camera.near = Z_NEAR;
		camera.far = Z_FAR;
		
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 0.5f));
		environment.set(new ColorAttribute(ColorAttribute.Fog, BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, BACKGROUND_COLOR.a));
		environment.add(new DirectionalLight().set(0.6f, 0.6f, 0.4f, -0.5f, -0.6f, -0.5f));
		environment.add(new DirectionalLight().set(0.6f, 0.6f, 0.4f, 0.5f, -0.6f, 0.5f));
	}
	
	private void setupUi(){
		target = AssetRepository.getInstance().load(TEXTURE_TARGET, Texture.class);
		scoreValue = 0;
		score = FontUtils.loadFont("fonts/cartoon_shout.ttf", 42, Color.BLACK);
	}

	public void showUI(){
		life = new ArrayList<Texture>();
		heart = AssetRepository.getInstance().load(TEXTURE_HEART, Texture.class);
		
		for(int i = 0; i < INITIAL_LIFE; i++)
			life.add(heart);
		
		visibleUI = true;
	}
	
	public void hideUI(){
		visibleUI = false;
	}
	
	public void removeLife(){
		if(life == null) return;
		if(!life.isEmpty()) life.remove(0);
		if(life.isEmpty()){
			gameOver();
			return;
		}
	}
	
	public void setBackgroundColor(Color color){
		BACKGROUND_COLOR = color;
	}
	
	public void gameOver(){
		if(!endFade.isPlaying){
			soundEffectPlayerDying.play();
			soundEffectEnemyEating.play();
			
			endFade.stop();
			endFade.start();
			endFade.setOnFinished(new FadeRenderer.OnFinished() {
				@Override
				public void run() {
					game.gotoGameOver(scoreValue);
				}
			});
		}
	}
	
	public void increaseScore(int score) {
		scoreValue += score;
	}
	
	public GameObject getObject (int screenX, int screenY) {
        Ray ray = camera.getPickRay(screenX, screenY);
        GameObject result = null;
        float distance = -1;
        for (ModelInstance item : instances) {
        	if(item instanceof GameObject){
        		GameObject gameObject = (GameObject) item;
        		if(gameObject.enabled){
		        	Vector3 position = gameObject.transform.getTranslation(new Vector3());
		        	gameObject.updateBox();
		            position.add(gameObject.center);
		            float dist2 = ray.origin.dst2(position);
		            if (distance >= 0f && dist2 > distance) continue;
		            if (Intersector.intersectRayBoundsFast(ray, gameObject.bounds)) {
			            result = gameObject;
		                distance = dist2;
		            }
        		}
        	}
        }
        return result;
    }

	
	
	
	
	
	
	private void updateObjects(float delta) {
		tweenManager.update(delta);
		
		for(ModelInstance item : instances) 
			if(item instanceof Enemy) 
				((Enemy) item).animations.update(delta);
	}
	
	public void onDrawEye (Eye paramEye) {
		
		Gdx.gl.glClearColor(BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, BACKGROUND_COLOR.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
        camera.setEyeViewAdjustMatrix(new Matrix4(paramEye.getEyeView()));
        float[] perspective = paramEye.getPerspective(Z_NEAR, Z_FAR);
        camera.setEyeProjection(new Matrix4(perspective));
        camera.update();
        
		game.modelBatch.begin(camera);
		game.modelBatch.render(instances, environment);
		game.modelBatch.end();
		
		game.spriteBatch.begin();
		if(showTarget)
			game.spriteBatch.draw(target, (Gdx.graphics.getWidth()-target.getWidth())/2, (Gdx.graphics.getHeight()-target.getHeight())/2);
		if(visibleUI){
			score.draw(game.spriteBatch, HudUtils.formattedScore(scoreValue), (Gdx.graphics.getWidth()-(score.getSpaceWidth() * HudUtils.formattedScore(scoreValue).length())) / 2, Gdx.graphics.getHeight()-MARGIN_SCREEN);
			if(life.size() > 0){
				int size = (life.size() * life.get(0).getWidth()) + ((life.size()-1) * MARGIN_HEART);
				float center = (Gdx.graphics.getWidth() - size)/2;
				for(int i = 0; i < life.size(); i++){
					Texture item = life.get(i);
					game.spriteBatch.draw(item, center + ((item.getWidth() + MARGIN_HEART)*i), MARGIN_SCREEN);
				}
			}
		}
		renderSprite(Gdx.graphics.getDeltaTime());
		game.spriteBatch.end();
		if(endFade.isPlaying) endFade.shows(Gdx.graphics.getDeltaTime());
		if(laser.isPlaying) laser.shows(Gdx.graphics.getDeltaTime());
	}
	
	public void renderSprite(float delta) { }
	
	@Override
	public void render(float delta) {
		processInput();
		updateObjects(delta);
	}
	
	@Override
	public void dispose() {
		game.modelBatch.dispose();
		game.spriteBatch.dispose();
		heart.dispose();
		target.dispose();
		instances.clear();
		soundEffectEnemyEating.dispose();
		soundEffectPlayerDying.dispose();
		soundEffectPlayerShoot.dispose();
		soundEffectUI.dispose();
		AssetRepository.getInstance().dispose();
	}
	
	@Override public void resize(int width, int height) { }
	@Override public void show() { }
	@Override public void pause() { }
	@Override public void resume() { }
	@Override public void hide() { }
}
