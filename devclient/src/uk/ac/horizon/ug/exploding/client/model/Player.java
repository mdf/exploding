
package uk.ac.horizon.ug.exploding.client.model;
/** 
 * Identity element is ID.
 * 
 * Autogenerated by bean2java.xsl */
public class Player
{

  /** no-arg cons */
  public Player()
  {
  }

  /* implements ., i.e. 
    uk.ac.horizon.ug.exploding.db.Player */
  
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
   * max length = 20.
  
   */
  protected java.lang.String _gameID;
  
  /** getter - 
   * max length = 20.
  
   */
  public java.lang.String getGameID()
  {
  
    return _gameID;
    
  }

  /** setter - 
   * max length = 20.
  
   */
  public void setGameID(java.lang.String gameID)
  {
  
    this._gameID = gameID;
    
  }

  /** is set?
   */
  public boolean isSetGameID() {
    return this._gameID != null; 
  }

  /** unset
   */
  public void unsetGameID()  {
    this._gameID = null; 
  }

  /** internal value - 
   */
  protected Position _position;
  
  /** getter - 
   */
  public Position getPosition()
  {
  
    return _position;
    
  }

  /** setter - 
   */
  public void setPosition(Position position)
  {
  
    this._position = position;
    
  }

  /** is set?
   */
  public boolean isSetPosition() {
    return this._position != null; 
  }

  /** unset
   */
  public void unsetPosition()  {
    this._position = null; 
  }

  /** internal value - 
   */
  protected java.lang.Long _positionUpdateTime;
  
  /** getter - 
   */
  public java.lang.Long getPositionUpdateTime()
  {
  
    return _positionUpdateTime;
    
  }

  /** setter - 
   */
  public void setPositionUpdateTime(java.lang.Long positionUpdateTime)
  {
  
    this._positionUpdateTime = positionUpdateTime;
    
  }

  /** is set?
   */
  public boolean isSetPositionUpdateTime() {
    return this._positionUpdateTime != null; 
  }

  /** unset
   */
  public void unsetPositionUpdateTime()  {
    this._positionUpdateTime = null; 
  }

  /** internal value - 
   */
  protected java.lang.Integer _points;
  
  /** getter - 
   */
  public java.lang.Integer getPoints()
  {
  
    return _points;
    
  }

  /** setter - 
   */
  public void setPoints(java.lang.Integer points)
  {
  
    this._points = points;
    
  }

  /** is set?
   */
  public boolean isSetPoints() {
    return this._points != null; 
  }

  /** unset
   */
  public void unsetPoints()  {
    this._points = null; 
  }

  /** internal value - 
   */
  protected java.lang.Boolean _canAuthor;
  
  /** getter - 
   */
  public java.lang.Boolean getCanAuthor()
  {
  
    return _canAuthor;
    
  }

  /** setter - 
   */
  public void setCanAuthor(java.lang.Boolean canAuthor)
  {
  
    this._canAuthor = canAuthor;
    
  }

  /** is set?
   */
  public boolean isSetCanAuthor() {
    return this._canAuthor != null; 
  }

  /** unset
   */
  public void unsetCanAuthor()  {
    this._canAuthor = null; 
  }

  /** internal value - 
   */
  protected java.lang.Integer _newMemberQuota;
  
  /** getter - 
   */
  public java.lang.Integer getNewMemberQuota()
  {
  
    return _newMemberQuota;
    
  }

  /** setter - 
   */
  public void setNewMemberQuota(java.lang.Integer newMemberQuota)
  {
  
    this._newMemberQuota = newMemberQuota;
    
  }

  /** is set?
   */
  public boolean isSetNewMemberQuota() {
    return this._newMemberQuota != null; 
  }

  /** unset
   */
  public void unsetNewMemberQuota()  {
    this._newMemberQuota = null; 
  }

  /** equals */
  public boolean equals(Object o) {
    if (o==null) return false;
    if (!(o instanceof Player)) return false;
    Player oo = (Player)o;
      if (_ID!=oo._ID &&
        (_ID==null || oo._ID==null ||
         !_ID.equals(oo._ID)))
      return false;
    if (_name!=oo._name &&
        (_name==null || oo._name==null ||
         !_name.equals(oo._name)))
      return false;
    if (_gameID!=oo._gameID &&
        (_gameID==null || oo._gameID==null ||
         !_gameID.equals(oo._gameID)))
      return false;
    if (_position!=oo._position &&
        (_position==null || oo._position==null ||
         !_position.equals(oo._position)))
      return false;
    if (_positionUpdateTime!=oo._positionUpdateTime &&
        (_positionUpdateTime==null || oo._positionUpdateTime==null ||
         !_positionUpdateTime.equals(oo._positionUpdateTime)))
      return false;
    if (_points!=oo._points &&
        (_points==null || oo._points==null ||
         !_points.equals(oo._points)))
      return false;
    if (_canAuthor!=oo._canAuthor &&
        (_canAuthor==null || oo._canAuthor==null ||
         !_canAuthor.equals(oo._canAuthor)))
      return false;
    if (_newMemberQuota!=oo._newMemberQuota &&
        (_newMemberQuota==null || oo._newMemberQuota==null ||
         !_newMemberQuota.equals(oo._newMemberQuota)))
      return false;

    return true;
  }
  /** hashcode */
  public int hashCode() {
    int val = 0;
      if (_ID!=null) val = val ^ _ID.hashCode();
    if (_name!=null) val = val ^ _name.hashCode();
    if (_gameID!=null) val = val ^ _gameID.hashCode();
    if (_position!=null) val = val ^ _position.hashCode();
    if (_positionUpdateTime!=null) val = val ^ _positionUpdateTime.hashCode();
    if (_points!=null) val = val ^ _points.hashCode();
    if (_canAuthor!=null) val = val ^ _canAuthor.hashCode();
    if (_newMemberQuota!=null) val = val ^ _newMemberQuota.hashCode();

    return val;
  }
  /** tostring */
  public String toString() {
    StringBuilder str = new StringBuilder("Player:");
    
    str.append("{");
    
    
	str.append("ID=");
	if (_ID!=null) {
	    str.append(_ID.toString());
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
	str.append("gameID=");
	if (_gameID!=null) {
	    str.append(_gameID.toString());
	} else {
	    str.append("null");
	}
    str.append(",");
	str.append("position=");
	if (_position!=null) {
	    str.append(_position.toString());
	} else {
	    str.append("null");
	}
    str.append(",");
	str.append("positionUpdateTime=");
	if (_positionUpdateTime!=null) {
	    str.append(_positionUpdateTime.toString());
	} else {
	    str.append("null");
	}
    str.append(",");
	str.append("points=");
	if (_points!=null) {
	    str.append(_points.toString());
	} else {
	    str.append("null");
	}
    str.append(",");
	str.append("canAuthor=");
	if (_canAuthor!=null) {
	    str.append(_canAuthor.toString());
	} else {
	    str.append("null");
	}
    str.append(",");
	str.append("newMemberQuota=");
	if (_newMemberQuota!=null) {
	    str.append(_newMemberQuota.toString());
	} else {
	    str.append("null");
	}
    
    str.append("}");

    return str.toString();
  }

}
