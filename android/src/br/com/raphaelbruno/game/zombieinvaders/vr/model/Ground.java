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

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

import br.com.raphaelbruno.game.zombieinvaders.vr.util.AssetRepository;

public class Ground extends GameObject {
	
	public Ground(ScreenBase context, Model model) {
		super(context, model);
		this.enabled = false;
	}

	public static class Builder {
		private Vector3 position;
		private float rotation;
		
		public Builder(){
			this(null, 0f, 0f);
		}
		
		public Builder(Vector3 position, float rotation, float scale){
			this.position = position;
			this.rotation = rotation;
		}
		
		public Ground.Builder setPosition(float x, float y, float z){ return setPosition(new Vector3(x, y, z)); }
		public Ground.Builder setPosition(Vector3 position){
			this.position = position;
			return this;
		}
		
		public Ground.Builder setRotation(float rotation){
			this.rotation = rotation;
			return this;
		}
		
		
		public Ground build(ScreenBase context, String urlG3d){
			Ground level = new Ground(context, AssetRepository.getInstance().load(urlG3d, Model.class));
			
			if(position != null) level.moveTo(position);
			if(rotation != 0f) level.rotateTo(rotation);
			
			return level;
		}
		
	}

}