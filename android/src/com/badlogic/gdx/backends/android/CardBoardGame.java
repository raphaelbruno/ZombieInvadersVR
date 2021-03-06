/*******************************************************************************
 * Copyright 2015 wei yang(yangweigbh@hotmail.com)
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
 ******************************************************************************/

package com.badlogic.gdx.backends.android;

import com.badlogic.gdx.Game;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

public abstract class CardBoardGame extends Game {

	public abstract void onNewFrame(HeadTransform paramHeadTransform);
	public abstract void onDrawEye(Eye paramEye);
	public abstract void onFinishFrame(Viewport paramViewport);
	public abstract void onRendererShutdown();
	public abstract void onCardboardTrigger();
}
