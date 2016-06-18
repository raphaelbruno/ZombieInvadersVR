package br.com.raphaelbruno.game.zombieinvaders.vr.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class LaserRenderer extends ShapeRenderer {
	
	private final float ALPHA_VELOCITY = 7.5f;
	private float alpha = 1f;
	private OnLaserFinished onLaserFinished;
	
	public boolean isPlaying = false;
	
	public void shows(float delta){
		if(!isPlaying) return;
		
		alpha -= ALPHA_VELOCITY*delta;
		if(alpha < 0f){
			stop();
			return;
		}
		int width = (int) ( ((float) Gdx.graphics.getWidth())*alpha );
		int height = (int) ( ((float) Gdx.graphics.getHeight()/4)*alpha );
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		begin(ShapeType.Filled);
		setColor(1f, 0f, 0f, alpha);
		rect((Gdx.graphics.getWidth()-width)/2, (Gdx.graphics.getHeight()-height)/2, width, height);
		end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	public void play(){ play(null); }
	public void play(OnLaserFinished onLaserFinished){
		this.onLaserFinished = onLaserFinished;
		isPlaying = true;
	}
	
	private void stop(){
		alpha = 1;
		isPlaying = false;
		if(onLaserFinished != null) onLaserFinished.run();
	}
	
	
	public interface OnLaserFinished {
		public void run();
	}
}