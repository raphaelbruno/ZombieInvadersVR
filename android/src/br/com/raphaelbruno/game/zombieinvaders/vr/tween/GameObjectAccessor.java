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

package br.com.raphaelbruno.game.zombieinvaders.vr.tween;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import aurelienribon.tweenengine.TweenAccessor;
import br.com.raphaelbruno.game.zombieinvaders.vr.model.GameObject;

public class GameObjectAccessor implements TweenAccessor<GameObject> {
	
	public static final int XYZ = 1;
    public static final int ROTATION = 2;
    public static final int ALPHA = 3;

	@Override
	public int getValues(GameObject target, int tweenType, float[] returnValues) {
		Vector3 position = target.transform.getTranslation(new Vector3());
		Vector3 rotation = new Vector3();
		
		switch (tweenType) {
        	case XYZ:
	            returnValues[0] = position.x;
	            returnValues[1] = position.y;
	            returnValues[2] = position.z;
	            return 3;
	        
	        case ROTATION:
                returnValues[0] = target.transform.getRotation(new Quaternion()).getAxisAngle(rotation) * rotation.nor().y;
                return 1;
	
	        case ALPHA:
	        	returnValues[0] = target.blending.opacity;
                return 1;
	
	        default: 
	            assert false; 
	            return -1;
	    }
	}

	@Override
	public void setValues(GameObject target, int tweenType, float[] newValues) {
		Vector3 position = target.transform.getTranslation(new Vector3());
		
		switch (tweenType) {
	        case XYZ:
	    		target.transform.setTranslation(new Vector3(newValues[0], newValues[1], newValues[2]));
	            break;
	
	        case ROTATION:
	        	target.transform.setToRotation(Vector3.Y, newValues[0]).setTranslation(position);
	            break;
	            
	        case ALPHA:
                target.blending.opacity = newValues[0];
	            break;
	
	        default: 
	            assert false;
	    }
		
	}
	
}