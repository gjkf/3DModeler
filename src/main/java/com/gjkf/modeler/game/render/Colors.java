/*
  Created by Davide Cossu (gjkf), 7/9/2016
 */
package com.gjkf.modeler.game.render;

public enum Colors{

	WHITE(new Color4f(1f, 1f, 1f, 1f)),
	BLACK(new Color4f(0f, 0f, 0f, 1f)),
	RED(new Color4f(1f, 0f, 0f, 1f)),
	GREEN(new Color4f(0f, 1f, 0f, 1f)),
	BLUE(new Color4f(0f, 0f, 1f, 1f)),
	NULL(null);

	public final Color4f color;

	Colors(Color4f Color4f){
		this.color = Color4f;
	}

}
