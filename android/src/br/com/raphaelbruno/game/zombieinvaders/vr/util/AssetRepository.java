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

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.utils.UBJsonReader;

public class AssetRepository {
	private static AssetRepository instance; 
	
	private static UBJsonReader jsonReader;
	private static G3dModelLoader modelLoader;
	private Map<String, Object> repository;
	
	private AssetRepository() {
		repository = new HashMap<String, Object>();
		jsonReader = new UBJsonReader();
		modelLoader = new G3dModelLoader(jsonReader);
	} 
	
	public static synchronized AssetRepository getInstance() {
		if (instance == null)
			instance = new AssetRepository(); 
		return instance; 
	}
	
	public synchronized <T> T load(String url, Class<T> clazz) {
		String local = clazz.getSimpleName() + ":" + url;
		T asset;
		
		if(repository.containsKey(local)){
			asset = (T) repository.get(local);
		}else{
			asset = (T) loadAsset(url, clazz);
			if(asset != null)
				repository.put(local, asset);
		}
		return asset;
	}
	
	private synchronized <T> T loadAsset(String url, Class<T> clazz) {
		T asset = null;
		
		if(clazz == Model.class)
			asset = (T) modelLoader.loadModel(Gdx.files.getFileHandle(url, Files.FileType.Internal));
		if(clazz == Texture.class)
			asset = (T) new Texture(Gdx.files.getFileHandle(url, Files.FileType.Internal));
		
		return asset;
	}
	
	public void dispose() {
		for (Map.Entry<String, Object> entry : repository.entrySet()){
			try {
				Object item = entry.getValue();
				Class.forName(item.getClass().getName()).getMethod("dispose").invoke(item);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		repository.clear();
	}
}
