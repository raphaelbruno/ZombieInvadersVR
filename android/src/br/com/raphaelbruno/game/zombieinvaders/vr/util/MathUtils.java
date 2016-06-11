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

package br.com.raphaelbruno.game.zombieinvaders.vr.util;

import com.badlogic.gdx.math.Vector3;

import android.graphics.Point;

public class MathUtils {
	public static final int PRECISION = 1000;
	
	public static double aleatoryRadianAngle(){
		return Math.random() * (Math.PI * 2);
	}
	
	public static double aleatoryDegreesAngle(){ return aleatoryDegreesAngle(false); }
	public static double aleatoryDegreesAngle(boolean precision){
		double degrees = fixesPrecision(Math.random() * 360);
		return precision ? degrees : Math.round(degrees);
	}
	
	public static double hypotenuse(double a, double b){
		return Math.sqrt((a*a)+(b*b));
	}
	
	public static double fixesPrecision(double value) {
        return (double) Math.round(value * PRECISION) / PRECISION;
    }
	
	public static Vector3 positionFromAngle(double angle, double raio){ return positionFromAngle((float) angle, new Point(), (float) raio); }
	public static Vector3 positionFromAngle(double angle, Point center, double raio){ return positionFromAngle((float) angle, center, (float) raio); }
	public static Vector3 positionFromAngle(float angle, float raio){ return positionFromAngle(angle, new Point(), raio); }
	public static Vector3 positionFromAngle(float angle, Point center, float raio){
		Vector3 point = new Vector3();
		point.x = (float) fixesPrecision((Math.cos(angle) * raio) + center.x);
		point.y = (float) fixesPrecision((Math.sin(angle) * raio) + center.y);
		return point;
	}
}
