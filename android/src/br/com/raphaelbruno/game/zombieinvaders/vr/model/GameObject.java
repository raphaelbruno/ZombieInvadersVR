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

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import br.com.raphaelbruno.game.zombieinvaders.vr.tween.GameObjectAccessor;
import br.com.raphaelbruno.game.zombieinvaders.vr.util.AssetRepository;

public class GameObject extends ModelInstance {
	public final Vector3 center;
	public final AnimationController animations;
    public BlendingAttribute blending;
    public boolean enabled;

    private BoundingBox customBounds;
    public BoundingBox bounds;
    public ScreenBase context;

	public GameObject(ScreenBase context, String url) {
		this(context, AssetRepository.getInstance().load(url, Model.class));
	}
	
	public GameObject(ScreenBase context, String url, BoundingBox bounds) {
		this(context, AssetRepository.getInstance().load(url, Model.class), bounds);
	}
	
	public GameObject(ScreenBase context, Model model) {
		this(context, model, null);
	}
	
	public GameObject(ScreenBase context, Model model, BoundingBox bounds) {
		super(model);
		this.context = context;
		this.customBounds = bounds;
		
        this.bounds = this.customBounds != null ? this.customBounds : new BoundingBox();
        this.center = new Vector3();
        this.enabled = true;
        updateBox();
        
        this.animations = new AnimationController(this);
        this.blending = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        for(Material item : materials){
        	item.set(new DepthTestAttribute(GL20.GL_LEQUAL, 0.01f, 25f, true));
			item.set(FloatAttribute.createAlphaTest(0.01f));
        	item.set(blending);
        }
    }
	
	public void setCustomBounds(BoundingBox bounds){
		this.customBounds = bounds;
		updateBox();
	}
	
	public void updateBox(){
		Vector3 position = transform.getTranslation(new Vector3());
        if(customBounds != null) bounds = new BoundingBox(customBounds);
        else calculateBoundingBox(bounds);
        bounds.getCenter(center);
        bounds.set(bounds.min.add(position.x,position.y, position.z), bounds.max.add(position.x, position.y, position.z));
    }
	
	public void moveTo(float x, float y, float z) { moveTo(new Vector3(x, y, z)); }
	public void moveTo(Vector3 position) {
		transform.setTranslation(position);
	}
	
	public void moveToAnimation(float x, float y, float z, float time) { moveToAnimation(new Vector3(x, y, z), time, null); }
	public void moveToAnimation(float x, float y, float z, float time, OnAnimationComplete onAnimationComplete) { moveToAnimation(new Vector3(x, y, z), time, onAnimationComplete); }
	public void moveToAnimation(Vector3 position, float time) { moveToAnimation(position, time, null); }
	public void moveToAnimation(Vector3 position, float time, final GameObject.OnAnimationComplete onAnimationComplete) {
	    final GameObject enemy = this;
	    Tween.registerAccessor(ModelInstance.class, new GameObjectAccessor());
	    
	    context.tweenManager.killTarget(this, GameObjectAccessor.XYZ);
		Tween.to(this, GameObjectAccessor.XYZ, time)
			.target(position.x, position.y, position.z)
			.ease(TweenEquations.easeNone)
			.start(context.tweenManager)
			.setCallback(new TweenCallback() {
				@Override public void onEvent(int arg0, BaseTween<?> arg1) {
					if(onAnimationComplete != null) onAnimationComplete.run(enemy);
				}
			});
	}

	public void lookAt (float x, float y, float z) { lookAt(new Vector3(x, y, z));}
	public void lookAt (Vector3 point) {
		Vector3 from = transform.getTranslation(new Vector3()).cpy();
		Vector3 to = point.cpy();
		Vector3 direction = to.sub(from).nor();
		direction.set(-direction.x, -direction.y, -direction.z);

	    Quaternion quaternion = new Quaternion();
	    Matrix4 instanceRotation = transform.cpy().mul(transform);

	    instanceRotation.setToLookAt(direction, new Vector3(0,-1,0));
	    instanceRotation.rotate(0, 0, 1, 180);
	    instanceRotation.getRotation(quaternion);

	    transform.set(from, quaternion);
	}
	
	public void rotateTo(float angle) {
		Vector3 position = transform.getTranslation(new Vector3());
		transform.setToRotation(Vector3.Y, angle).setTranslation(position);
	}
	
	public void rotateToAnimation(float angle, float time) { rotateToAnimation(angle, time, null); }
	public void rotateToAnimation(float angle, float time, final GameObject.OnAnimationComplete onAnimationComplete) {
		final GameObject object = this;
	    Tween.registerAccessor(ModelInstance.class, new GameObjectAccessor());
	    
	    context.tweenManager.killTarget(this, GameObjectAccessor.ROTATION);
		Tween.to(this, GameObjectAccessor.ROTATION, time)
			.target(angle)
			.ease(TweenEquations.easeNone)
			.start(context.tweenManager)
			.setCallback(new TweenCallback() {
				@Override public void onEvent(int arg0, BaseTween<?> arg1) {
					if(onAnimationComplete != null) onAnimationComplete.run(object);
				}
			});
	}
	
	public void alphaTo(float alpha) {
		if(alpha > 1) alpha = 1;
		if(alpha < 0) alpha = 0;
		blending.opacity = alpha;
	}
	
	public void alphaToAnimation(float alpha, float time) { alphaToAnimation(alpha, time, null); }
	public void alphaToAnimation(float alpha, float time, final GameObject.OnAnimationComplete onAnimationComplete) {
		final GameObject object = this;
	    Tween.registerAccessor(ModelInstance.class, new GameObjectAccessor());
		if(alpha > 1) alpha = 1;
		if(alpha < 0) alpha = 0;
	    
		context.tweenManager.killTarget(this, GameObjectAccessor.ALPHA);
		Tween.to(this, GameObjectAccessor.ALPHA, time)
			.target(alpha)
			.ease(TweenEquations.easeNone)
			.start(context.tweenManager)
			.setCallback(new TweenCallback() {
				@Override public void onEvent(int arg0, BaseTween<?> arg1) {
					if(onAnimationComplete != null) onAnimationComplete.run(object);
				}
			});
	}
	
	public interface OnAnimationComplete{
		void run(GameObject object);
	}
	
}