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

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.CardBoardAndroidApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;
import br.com.raphaelbruno.game.zombieinvaders.vr.util.AssetRepository;

public class ZombieInvadersVR extends CardBoardAndroidApplication {
	public AndroidApplicationConfiguration config;
	private GameBase gameBase;
	private SharedPreferences sharedPreferences;
	private Vibrator vibrator;
	public int localScore;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AssetRepository.getInstance().dispose();
        
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        config = new AndroidApplicationConfiguration();
        config.numSamples = 2;
		
        gameBase = new GameBase(this);
        gameBase.highScore = sharedPreferences.getInt(getString(R.string.saved_high_score), 0);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        initialize(gameBase, config);
    }
    
    @Override public void onCardboardTrigger() {
    	gameBase.shoot();
    }

	public void toast(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void vibrate(final int time) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				vibrator.vibrate(time);
			}
		});
	}
	
	public void setHighScore(int score){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.saved_high_score), score);
        editor.commit();
	}

}
