package com.beep_boop.Beep.levelSelect;

import android.graphics.Bitmap;

public class EggNode extends MapNode
{
	public Bitmap icon;
	
	public EggNode(float aX, float aY, String aLevelKey, Bitmap aIcon)
	{
		super(aX, aY, aLevelKey);
		
		icon = aIcon;
	}
}
