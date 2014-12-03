package com.beep_boop.Beep.levelSelect;

public class MapNode
{
	///-----Interfaces-----
	// None
	
	///-----Members-----
	/** Holds the X coordinate of the node in map space */
	private float mX;
	/** Holds the Y coordinate of the node in map space */
	private float mY;
	/** Holds the level key of the level this node represents */
	private String mLevelKey;
	
	///-----Constructors-----
	/** Constructor for MapNode.
	* @param aX - X coord of node in map space
	* @param aY - Y coord of node in map space
	* @param aLevelKey - Level the node represents
	*/
	public MapNode(float aX, float aY, String aLevelKey)
	{
		mX = aX;
		mY = aY;
		mLevelKey = aLevelKey;
	}
	
	///-----Functions-----
	/** Returns the X coord of the node in map space
	* @return float - X coord
	*/
	public float getX()
	{
		return mX;
	}
	
	/** Returns the Y coord of the node in map space
	* @return float - Y coord
	*/
	public float getY()
	{
		return mY;
	}
	
	/** Returns the level key of the node
	* @return String - level key
	*/
	public String getLevelKey()
	{
		return mLevelKey; 
	}
}
