<?xml version="1.0"?> 
<!--
 <COPYRIGHT>

Copyright (c) 2006, University of Nottingham
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 - Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

 - Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

 - Neither the name of the University of Nottingham
   nor the names of its contributors may be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</COPYRIGHT>

Created by: Chris Greenhalgh (University of Nottingham)
 -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="text"/>
<xsl:template match="bean">
<xsl:if test="@interface='true'">
  <xsl:message terminate="true">ERROR: interfaces should not have helpers - generate helpers only for the concrete implementation classes</xsl:message>
</xsl:if>package <xsl:value-of select="@package"/>;

import equip2.core.objectsupport.IArrayFactory;
import equip2.core.objectsupport.IElement;
import equip2.core.objectsupport.impl.StaticStructuredObjectHelper;
import equip2.core.objectsupport.impl.DelegatingElement;

/** Autogenerated EQUIP2 helper class. */
public class <xsl:value-of select="@class"/>_helper extends StaticStructuredObjectHelper implements IArrayFactory
{
    /** no arg cons
     */
    public <xsl:value-of select="@class"/>_helper() throws ClassNotFoundException 
    {
	// j2me cldc1.0 workaround
	super("<xsl:value-of select="@package"/>.<xsl:value-of select="@class"/>", 
	    new String[] {
<xsl:for-each select="implements">
    <xsl:if test="count(@beanfile)!=1">
      <xsl:message terminate="yes">ERROR: The 'implements' element must have one beanfile attribute specified (interface bean definition filename)</xsl:message>
    </xsl:if>
</xsl:for-each>
<xsl:call-template name="implement-interface-property-list">
  <xsl:with-param name="beanfile">.</xsl:with-param>
  <xsl:with-param name="bean" select="/bean"/>
</xsl:call-template>
	    }, 
	    Class.forName("<xsl:value-of select="@package"/>.<xsl:value-of select="@class"/>"),
	    <xsl:if test="count(@id)!=0">"<xsl:value-of select="@id"/>"</xsl:if> 
	    <xsl:if test="count(@id)=0">null</xsl:if>);
    }
    /** new instance array of given size
     */
    public Object newInstance(int length) 
    {
	return new <xsl:value-of select="@class"/>[length];
    }
    /** return value 
     * Note helper instances may be shared
     */
    public Object getElementValue(Object object, IElement info) 
    {
	<xsl:value-of select="@class"/> to1 = (<xsl:value-of select="@class"/>)object;
	String name = (String)info.getKey();
<xsl:call-template name="implement-interface-property-get">
  <xsl:with-param name="beanfile">.</xsl:with-param>
  <xsl:with-param name="bean" select="/bean"/>
</xsl:call-template>
	throw new RuntimeException("getElementValue for unknown element "+name);
    }
    /** set value
     * Note helper instances may be shared
     */
    public void setElementValue(Object object, IElement info, Object value) 
    {
	<xsl:value-of select="@class"/> to1 = (<xsl:value-of select="@class"/>)object;
	String name = (String)info.getKey();
<xsl:call-template name="implement-interface-property-set">
  <xsl:with-param name="beanfile">.</xsl:with-param>
  <xsl:with-param name="bean" select="/bean"/>
</xsl:call-template>
	    throw new RuntimeException("setElementValue for unknown element "+name);
    }
}
</xsl:template>

<xsl:template name="property-type">
  <xsl:param name="bean"/>
  <xsl:choose>
    <xsl:when test="count(@type)!=0"><xsl:variable name="type" select="@type"/><xsl:value-of select="$bean/type[@name=$type]/@javatype"/></xsl:when>
    <xsl:otherwise><xsl:value-of select="@javatype"/></xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="property-field-type">
  <xsl:param name="bean"/>
  <xsl:variable name="javatype"><xsl:call-template name="property-type"><xsl:with-param name="bean" select="$bean"/></xsl:call-template></xsl:variable>
  <xsl:variable name="isprimitive" select="$javatype='boolean' or $javatype='byte' or $javatype='char' or $javatype='short' or $javatype='int' or $javatype='long' or $javatype='float' or $javatype='double'"/>
  <xsl:choose>
    <xsl:when test="$javatype='int'">Integer</xsl:when>
    <xsl:when test="$javatype='char'">Character</xsl:when>
    <xsl:when test="$isprimitive"><xsl:value-of select="concat(translate(substring(@javatype,1,1),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),substring(@javatype,2))"/></xsl:when>
    <xsl:otherwise><xsl:value-of select="$javatype"/></xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- find defining beanfile using depth-first search -->
<xsl:template name="find-defining-beanfile">
  <xsl:param name="property-name"/>
  <xsl:param name="bean"/>
  <xsl:param name="beanfile"/>
  <xsl:param name="beanfile-list"/>
  <!-- <xsl:message>Checking for <xsl:value-of select="$property-name"/> in <xsl:value-of select="$beanfile"/> with list size <xsl:value-of select="count($beanfile-list)"/>...</xsl:message> -->
  <xsl:choose>
    <xsl:when test="$bean/property/@name=$property-name">
      <!-- <xsl:message>Found!</xsl:message> -->
      <xsl:value-of select="$beanfile"/>
    </xsl:when>
    <xsl:when test="count($bean/implements/@beanfile)>0">
      <xsl:call-template name="find-defining-beanfile">
        <xsl:with-param name="property-name" select="$property-name"/>
        <xsl:with-param name="bean" select="document($bean/implements[1]/@beanfile)/bean"/>
        <xsl:with-param name="beanfile" select="$bean/implements[1]/@beanfile[1]"/>
        <xsl:with-param name="beanfile-list" select="$bean/implements[position()>1]/@beanfile[1]|$beanfile-list"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="count($beanfile-list)>0">
      <xsl:call-template name="find-defining-beanfile">
        <xsl:with-param name="property-name" select="$property-name"/>
        <xsl:with-param name="bean" select="document($beanfile-list[1])/bean"/>
        <xsl:with-param name="beanfile" select="$beanfile-list[1]"/>
        <xsl:with-param name="beanfile-list" select="$beanfile-list[position()>1]"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="true">Did not find property <xsl:value-of select="$property-name"/></xsl:message>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- part of list property for this interface -->
<xsl:template name="implement-interface-property-list">
 <xsl:param name="beanfile"/>
 <xsl:param name="bean"/>
  <!-- the trick is to handle each property only once, even when defined from multiple interfaces -->
  <xsl:if test="count($bean)=0">
    <xsl:message terminate="true">ERROR: could not find bean definition '<xsl:value-of select="$beanfile"/>'</xsl:message>
  </xsl:if> 
  <xsl:for-each select="$bean/property">
    <xsl:variable name="defining-beanfile">
      <xsl:call-template name="find-defining-beanfile">
	<xsl:with-param name="property-name" select="@name"/>
        <xsl:with-param name="bean" select="/bean"/>
        <xsl:with-param name="beanfile">.</xsl:with-param>
        <xsl:with-param name="beanfile-list" select="non-existant"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$defining-beanfile=$beanfile">
        <!-- the work... -->
<xsl:text>                      "</xsl:text><xsl:value-of select="@name"/><xsl:text>",
</xsl:text>
      </xsl:when>
      <xsl:otherwise>
    /* property <xsl:value-of select="@name"/> defined in <xsl:value-of select="$defining-beanfile"/> */
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each> 
  <!-- hope there are no inheritance loops!! -->
  <xsl:for-each select="$bean/implements">
    <xsl:if test="count(./@beanfile)!=1">
      <xsl:message terminate="true">ERROR: Implemented beanfile <xsl:value-of select="$beanfile"/> has an implements element with no 'beanfile' attribute</xsl:message>
    </xsl:if>
    <xsl:call-template name="implement-interface-property-list">
      <xsl:with-param name="beanfile" select="@beanfile[1]"/>
      <xsl:with-param name="bean" select="document(@beanfile[1])/bean"/>
    </xsl:call-template>
  </xsl:for-each>
</xsl:template>

<!-- part of get property for this interface -->
<xsl:template name="implement-interface-property-get">
 <xsl:param name="beanfile"/>
 <xsl:param name="bean"/>
  <!-- the trick is to handle each property only once, even when defined from multiple interfaces -->
  <xsl:if test="count($bean)=0">
    <xsl:message terminate="true">ERROR: could not find bean definition '<xsl:value-of select="$beanfile"/>'</xsl:message>
  </xsl:if> 
  <xsl:for-each select="$bean/property">
    <xsl:variable name="defining-beanfile">
      <xsl:call-template name="find-defining-beanfile">
	<xsl:with-param name="property-name" select="@name"/>
        <xsl:with-param name="bean" select="/bean"/>
        <xsl:with-param name="beanfile">.</xsl:with-param>
        <xsl:with-param name="beanfile-list" select="non-existant"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$defining-beanfile=$beanfile">
        <!-- the work... -->
<xsl:variable name="javatype"><xsl:call-template name="property-type"><xsl:with-param name="bean" select="$bean"/></xsl:call-template></xsl:variable>
<xsl:variable name="pname" select="concat(translate(substring(@name,1,1),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),substring(@name,2))"/>
        if (name.equals("<xsl:value-of select="@name"/>"))
<xsl:variable name="isprimitive" select="$javatype='boolean' or $javatype='byte' or $javatype='char' or $javatype='short' or $javatype='int' or $javatype='long' or $javatype='float' or $javatype='double'"/>
<xsl:choose>
  <xsl:when test="$isprimitive">
<xsl:variable name="ptype"><xsl:call-template name="property-field-type"><xsl:with-param name="bean" select="$bean"/></xsl:call-template></xsl:variable>
          return to1.isSet<xsl:value-of select="$pname"/>() ? new <xsl:value-of select="$ptype"/>(to1.get<xsl:value-of select="$pname"/>()) : null;
  </xsl:when>
  <xsl:otherwise>
          return to1.get<xsl:value-of select="$pname"/>();
  </xsl:otherwise>
</xsl:choose>

      </xsl:when>
      <xsl:otherwise>
    /* property <xsl:value-of select="@name"/> defined in <xsl:value-of select="$defining-beanfile"/> */
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each> 
  <!-- hope there are no inheritance loops!! -->
  <xsl:for-each select="$bean/implements">
    <xsl:if test="count(./@beanfile)!=1">
      <xsl:message terminate="true">ERROR: Implemented beanfile <xsl:value-of select="$beanfile"/> has an implements element with no 'beanfile' attribute</xsl:message>
    </xsl:if>
    <xsl:call-template name="implement-interface-property-get">
      <xsl:with-param name="beanfile" select="@beanfile[1]"/>
      <xsl:with-param name="bean" select="document(@beanfile[1])/bean"/>
    </xsl:call-template>
  </xsl:for-each>
</xsl:template>

<!-- part of set property for this interface -->
<xsl:template name="implement-interface-property-set">
 <xsl:param name="beanfile"/>
 <xsl:param name="bean"/>
  <!-- the trick is to handle each property only once, even when defined from multiple interfaces -->
  <xsl:if test="count($bean)=0">
    <xsl:message terminate="true">ERROR: could not find bean definition '<xsl:value-of select="$beanfile"/>'</xsl:message>
  </xsl:if> 
  <xsl:for-each select="$bean/property">
    <xsl:variable name="defining-beanfile">
      <xsl:call-template name="find-defining-beanfile">
	<xsl:with-param name="property-name" select="@name"/>
        <xsl:with-param name="bean" select="/bean"/>
        <xsl:with-param name="beanfile">.</xsl:with-param>
        <xsl:with-param name="beanfile-list" select="non-existant"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$defining-beanfile=$beanfile">
        <!-- the work... -->
<xsl:variable name="javatype"><xsl:call-template name="property-type"><xsl:with-param name="bean" select="$bean"/></xsl:call-template></xsl:variable>
<xsl:variable name="pname" select="concat(translate(substring(@name,1,1),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),substring(@name,2))"/>
<xsl:variable name="isprimitive" select="$javatype='boolean' or $javatype='byte' or $javatype='char' or $javatype='short' or $javatype='int' or $javatype='long' or $javatype='float' or $javatype='double'"/>
<xsl:variable name="isarray" select="contains($javatype,'[]')"/>
        if (name.equals("<xsl:value-of select="@name"/>"))
<xsl:choose>
  <xsl:when test="$isprimitive">
<xsl:variable name="ptype"><xsl:call-template name="property-field-type"><xsl:with-param name="bean" select="$bean"/></xsl:call-template></xsl:variable>
        {
            if (value==null)
                to1.unset<xsl:value-of select="$pname"/>();
            else
                to1.set<xsl:value-of select="$pname"/>(((<xsl:value-of select="$ptype"/>)coerce(value,"java.lang.<xsl:value-of select="$ptype"/>")).<xsl:value-of select="$javatype"/>Value());
        }
  </xsl:when>
  <xsl:when test="$isarray">
    <xsl:variable name="arrayeltype" select="substring-before($javatype,'[')"/>
    <xsl:variable name="arrayelname">
      <xsl:choose>
        <xsl:when test="$arrayeltype='boolean'">Z</xsl:when>
        <xsl:when test="$arrayeltype='byte'">B</xsl:when>
        <xsl:when test="$arrayeltype='char'">C</xsl:when>
        <xsl:when test="$arrayeltype='short'">S</xsl:when>
        <xsl:when test="$arrayeltype='int'">I</xsl:when>
        <xsl:when test="$arrayeltype='long'">L</xsl:when>
        <xsl:when test="$arrayeltype='float'">F</xsl:when>
        <xsl:when test="$arrayeltype='double'">D</xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="concat('L',$arrayeltype,';')"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="arrayname" select="concat('[',translate(substring-after($javatype,'['),'[]','['),$arrayelname)"/>
            to1.set<xsl:value-of select="$pname"/>((<xsl:value-of select="$javatype"/>)coerce(value,"<xsl:value-of select="$arrayname"/>"));  
  </xsl:when>
  <xsl:otherwise>
            to1.set<xsl:value-of select="$pname"/>((<xsl:value-of select="$javatype"/>)coerce(value,"<xsl:value-of select="$javatype"/>"));
  </xsl:otherwise>
</xsl:choose>
	else

      </xsl:when>
      <xsl:otherwise>
    /* property <xsl:value-of select="@name"/> defined in <xsl:value-of select="$defining-beanfile"/> */
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each> 
  <!-- hope there are no inheritance loops!! -->
  <xsl:for-each select="$bean/implements">
    <xsl:if test="count(./@beanfile)!=1">
      <xsl:message terminate="true">ERROR: Implemented beanfile <xsl:value-of select="$beanfile"/> has an implements element with no 'beanfile' attribute</xsl:message>
    </xsl:if>
    <xsl:call-template name="implement-interface-property-set">
      <xsl:with-param name="beanfile" select="@beanfile[1]"/>
      <xsl:with-param name="bean" select="document(@beanfile[1])/bean"/>
    </xsl:call-template>
  </xsl:for-each>
</xsl:template>


</xsl:stylesheet>
