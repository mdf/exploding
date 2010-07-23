
package uk.ac.horizon.ug.exploding.client.model;
/** 
 * Identity element is ID.
 * 
 * Autogenerated by bean2java.xsl */
public class Game
{

  /** constants */

  /** constant -  */
  public static final java.lang.String UNKNOWN = "UNKNOWN";
  /** constant -  */
  public static final java.lang.String NOT_STARTED = "NOT_STARTED";
  /** constant -  */
  public static final java.lang.String ACTIVE = "ACTIVE";
  /** constant -  */
  public static final java.lang.String ENDING = "ENDING";
  /** constant -  */
  public static final java.lang.String ENDED = "ENDED";
  /** no-arg cons */
  public Game()
  {
  }

  /* implements ., i.e. 
    uk.ac.horizon.ug.exploding.db.Game */
  
  /** internal value - 
   * max length = 20.
  
   */
  protected java.lang.String _ID;
  
  /** getter - 
   * max length = 20.
  
   */
  public java.lang.String getID()
  {
  
    return _ID;
    
  }

  /** setter - 
   * max length = 20.
  
   */
  public void setID(java.lang.String ID)
  {
  
    this._ID = ID;
    
  }

  /** is set?
   */
  public boolean isSetID() {
    return this._ID != null; 
  }

  /** unset
   */
  public void unsetID()  {
    this._ID = null; 
  }

  /** internal value - 
   * max length = 20.
  
   */
  protected java.lang.String _contentGroupID;
  
  /** getter - 
   * max length = 20.
  
   */
  public java.lang.String getContentGroupID()
  {
  
    return _contentGroupID;
    
  }

  /** setter - 
   * max length = 20.
  
   */
  public void setContentGroupID(java.lang.String contentGroupID)
  {
  
    this._contentGroupID = contentGroupID;
    
  }

  /** is set?
   */
  public boolean isSetContentGroupID() {
    return this._contentGroupID != null; 
  }

  /** unset
   */
  public void unsetContentGroupID()  {
    this._contentGroupID = null; 
  }

  /** internal value - 
   */
  protected java.lang.String _name;
  
  /** getter - 
   */
  public java.lang.String getName()
  {
  
    return _name;
    
  }

  /** setter - 
   */
  public void setName(java.lang.String name)
  {
  
    this._name = name;
    
  }

  /** is set?
   */
  public boolean isSetName() {
    return this._name != null; 
  }

  /** unset
   */
  public void unsetName()  {
    this._name = null; 
  }

  /** internal value - 
   */
  protected java.lang.Long _timeCreated;
  
  /** getter - 
   */
  public java.lang.Long getTimeCreated()
  {
  
    return _timeCreated;
    
  }

  /** setter - 
   */
  public void setTimeCreated(java.lang.Long timeCreated)
  {
  
    this._timeCreated = timeCreated;
    
  }

  /** is set?
   */
  public boolean isSetTimeCreated() {
    return this._timeCreated != null; 
  }

  /** unset
   */
  public void unsetTimeCreated()  {
    this._timeCreated = null; 
  }

  /** internal value - 
   * max length = 20.
  
   */
  protected java.lang.String _gameTimeID;
  
  /** getter - 
   * max length = 20.
  
   */
  public java.lang.String getGameTimeID()
  {
  
    return _gameTimeID;
    
  }

  /** setter - 
   * max length = 20.
  
   */
  public void setGameTimeID(java.lang.String gameTimeID)
  {
  
    this._gameTimeID = gameTimeID;
    
  }

  /** is set?
   */
  public boolean isSetGameTimeID() {
    return this._gameTimeID != null; 
  }

  /** unset
   */
  public void unsetGameTimeID()  {
    this._gameTimeID = null; 
  }

  /** internal value - 
   */
  protected java.lang.String _year;
  
  /** getter - 
   */
  public java.lang.String getYear()
  {
  
    return _year;
    
  }

  /** setter - 
   */
  public void setYear(java.lang.String year)
  {
  
    this._year = year;
    
  }

  /** is set?
   */
  public boolean isSetYear() {
    return this._year != null; 
  }

  /** unset
   */
  public void unsetYear()  {
    this._year = null; 
  }

  /** internal value - 
   */
  protected java.lang.String _state;
  
  /** getter - 
   */
  public java.lang.String getState()
  {
  
    return _state;
    
  }

  /** setter - 
   */
  public void setState(java.lang.String state)
  {
  
    this._state = state;
    
  }

  /** is set?
   */
  public boolean isSetState() {
    return this._state != null; 
  }

  /** unset
   */
  public void unsetState()  {
    this._state = null; 
  }

  /** equals */
  public boolean equals(Object o) {
    if (o==null) return false;
    if (!(o instanceof Game)) return false;
    Game oo = (Game)o;
      if (_ID!=oo._ID &&
        (_ID==null || oo._ID==null ||
         !_ID.equals(oo._ID)))
      return false;
    if (_contentGroupID!=oo._contentGroupID &&
        (_contentGroupID==null || oo._contentGroupID==null ||
         !_contentGroupID.equals(oo._contentGroupID)))
      return false;
    if (_name!=oo._name &&
        (_name==null || oo._name==null ||
         !_name.equals(oo._name)))
      return false;
    if (_timeCreated!=oo._timeCreated &&
        (_timeCreated==null || oo._timeCreated==null ||
         !_timeCreated.equals(oo._timeCreated)))
      return false;
    if (_gameTimeID!=oo._gameTimeID &&
        (_gameTimeID==null || oo._gameTimeID==null ||
         !_gameTimeID.equals(oo._gameTimeID)))
      return false;
    if (_year!=oo._year &&
        (_year==null || oo._year==null ||
         !_year.equals(oo._year)))
      return false;
    if (_state!=oo._state &&
        (_state==null || oo._state==null ||
         !_state.equals(oo._state)))
      return false;

    return true;
  }
  /** hashcode */
  public int hashCode() {
    int val = 0;
      if (_ID!=null) val = val ^ _ID.hashCode();
    if (_contentGroupID!=null) val = val ^ _contentGroupID.hashCode();
    if (_name!=null) val = val ^ _name.hashCode();
    if (_timeCreated!=null) val = val ^ _timeCreated.hashCode();
    if (_gameTimeID!=null) val = val ^ _gameTimeID.hashCode();
    if (_year!=null) val = val ^ _year.hashCode();
    if (_state!=null) val = val ^ _state.hashCode();

    return val;
  }
  /** tostring */
  public String toString() {
    StringBuilder str = new StringBuilder("Game:");
    
    str.append("{");
    
    
	str.append("ID=");
	if (_ID!=null) {
	    str.append(_ID.toString());
	} else {
	    str.append("null");
	}
    str.append(",");
	str.append("contentGroupID=");
	if (_contentGroupID!=null) {
	    str.append(_contentGroupID.toString());
	} else {
	    str.append("null");
	}
    str.append(",");
	str.append("name=");
	if (_name!=null) {
	    str.append(_name.toString());
	} else {
	    str.append("null");
	}
    str.append(",");
	str.append("timeCreated=");
	if (_timeCreated!=null) {
	    str.append(_timeCreated.toString());
	} else {
	    str.append("null");
	}
    str.append(",");
	str.append("gameTimeID=");
	if (_gameTimeID!=null) {
	    str.append(_gameTimeID.toString());
	} else {
	    str.append("null");
	}
    str.append(",");
	str.append("year=");
	if (_year!=null) {
	    str.append(_year.toString());
	} else {
	    str.append("null");
	}
    str.append(",");
	str.append("state=");
	if (_state!=null) {
	    str.append(_state.toString());
	} else {
	    str.append("null");
	}
    
    str.append("}");

    return str.toString();
  }

}
