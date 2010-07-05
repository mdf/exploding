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
  <xsl:output method="xml" indent="yes" doctype-public="-//Hibernate/Hibernate Mapping DTD 3.0//EN" doctype-system="http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd"/>

<xsl:template name="property-type">
  <xsl:param name="bean"/>
  <xsl:choose>
    <xsl:when test="count(@type)!=0">
    <xsl:variable name="type" select="@type"/>
    <xsl:if test="count($bean/type[@name=$type]/@javatype)!=1">
      <xsl:message terminate="yes">type element <xsl:value-of select="@type"/> must have javatype attribute</xsl:message>
    </xsl:if>
    <xsl:value-of select="$bean/type[@name=$type]/@javatype"/>
    </xsl:when>
    <xsl:otherwise><xsl:value-of select="@javatype"/></xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="property-maxlength">
  <xsl:param name="bean"/>
  <xsl:choose>
    <xsl:when test="count(@type)!=0"><xsl:variable name="type" select="@type"/><xsl:value-of select="$bean/type[@name=$type]/@maxlength"/></xsl:when>
    <xsl:otherwise><xsl:value-of select="@maxlength"/></xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="property-elementtype">
  <xsl:param name="bean"/>
  <xsl:choose>
    <xsl:when test="count(@elementtype)!=0"><xsl:variable name="type" select="@elementtype"/><xsl:value-of select="$bean/type[@name=$type]/@javatype"/></xsl:when>
    <xsl:otherwise><xsl:value-of select="@elementjavatype"/></xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="property-elementmaxlength">
  <xsl:param name="bean"/>
  <xsl:choose>
    <xsl:when test="count(@elementtype)!=0"><xsl:variable name="type" select="@elementtype"/><xsl:value-of select="$bean/type[@name=$type]/@maxlength"/></xsl:when>
    <xsl:otherwise><xsl:value-of select="@elementmaxlength"/></xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="hibernate-type">
  <xsl:param name="javatype"/>
  <xsl:choose>
    <xsl:when test="$javatype='byte[]'">binary</xsl:when>
    <xsl:when test="$javatype='int' or $javatype='Integer' or $javatype='java.lang.Integer'">integer</xsl:when>
    <xsl:when test="$javatype='char' or $javatype='Character' or $javatype='java.lang.Character'">character</xsl:when>
    <xsl:when test="$javatype='short' or $javatype='Short' or $javatype='java.lang.Short'">short</xsl:when>
    <xsl:when test="$javatype='byte' or $javatype='Byte' or $javatype='java.lang.Byte'">byte</xsl:when>
    <xsl:when test="$javatype='long' or $javatype='Long' or $javatype='java.lang.Long'">long</xsl:when>
    <xsl:when test="$javatype='float' or $javatype='Float' or $javatype='java.lang.Float'">float</xsl:when>
    <xsl:when test="$javatype='double' or $javatype='Double' or $javatype='java.lang.Double'">double</xsl:when>
    <xsl:when test="$javatype='boolean' or $javatype='Boolean' or $javatype='java.lang.Boolean'">boolean</xsl:when>
    <xsl:when test="$javatype='java.util.Date'">timestamp</xsl:when>
    <xsl:when test="$javatype='java.util.Set'">set</xsl:when> <!-- set actually handled elsewhere -->
    <xsl:when test="$javatype='java.lang.String' or $javatype='String'">string</xsl:when>
    <xsl:when test="contains($javatype,'[]')">array</xsl:when> <!-- array actually handled elsewhere -->
    <xsl:otherwise>
      <xsl:message>Assuming <xsl:value-of select="$javatype"/> is to be nested component value...</xsl:message>
      <!--
      <xsl:variable name="filename"><xsl:value-of select="$javatype"/>.xml</xsl:variable>
      <xsl:message> <xsl:value-of select="$filename"/> = <xsl:value-of select="document($filename)"/></xsl:message>
       -->
      <!-- .... -->
      <!-- <xsl:message terminate="yes">Don't know how to map java type <xsl:value-of select="$javatype"/> to hibernate - sorry.</xsl:message> -->
      <xsl:text>component</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="bean">
<xsl:if test="@interface='true'">
  <xsl:message terminate="true">ERROR: interfaces should not have hibernate mapping files - generate mapping files only for the concrete implementation classes</xsl:message>
</xsl:if>
<hibernate-mapping> 
  <class>
    <xsl:attribute name="name"><xsl:value-of select="@package"/>.<xsl:value-of select="@class"/></xsl:attribute>
    <xsl:variable name="table">
      <xsl:choose>
        <xsl:when test="count(@table)!=0"><xsl:value-of select="@table"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="translate(@class,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:attribute name="table"><xsl:value-of select="$table"/></xsl:attribute>

      <xsl:if test="count(@primarykey)=0">
	<!-- default primary key -->
        <xsl:comment>Default (hibernate internal) primary key</xsl:comment>
        <id column="hibernatekey" type="long"> 
          <generator>
	   <xsl:attribute name="class">
             <xsl:choose>
               <xsl:when test="count(@primarykeygenerator)!=0">
	         <xsl:value-of select="@primarykeygenerator"/>
	       </xsl:when>
	       <xsl:otherwise>native</xsl:otherwise>
	     </xsl:choose>
	   </xsl:attribute>
          </generator>
        </id> 
      </xsl:if>
  
    <xsl:variable name="primarykey">
      <xsl:choose>
        <xsl:when test="count(@primarykey)!=0"><xsl:value-of select="@primarykey[1]"/></xsl:when>
        <xsl:otherwise><xsl:text></xsl:text></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <!-- bean file defining primary key? -->
    <xsl:variable name="primarykey-beanfile">
     <xsl:if test="count(@primarykey)!=0">
      <xsl:call-template name="find-defining-beanfile">
 	<xsl:with-param name="property-name" select="$primarykey"/>
  	<xsl:with-param name="bean" select="/bean"/>
  	<xsl:with-param name="beanfile"><xsl:value-of select="@package"/>.<xsl:value-of select="@class"/>.xml</xsl:with-param>
        <xsl:with-param name="beanfile-list" select="non-existant"/>
      </xsl:call-template>
     </xsl:if>
    </xsl:variable>
    <xsl:variable name="primarykeybean" select="document($primarykey-beanfile)/bean"/>
    <xsl:message>count primarykeybean=<xsl:value-of select="count($primarykeybean)"/>, beanfile=<xsl:value-of select="$primarykey-beanfile"/></xsl:message>
    <!-- primary key max length, used in set, etc. -->
    <xsl:variable name="primarykey-maxlength">
      <xsl:if test="count(@primarykey)!=0">
        <xsl:choose>
          <xsl:when test="count($primarykeybean[1]/property[@name=$primarykey]/@type)!=0">
            <xsl:variable name="typename" select="$primarykeybean[1]/property[@name=$primarykey]/@type"/>
            <xsl:value-of select="$primarykeybean[1]/type[@name=$typename]/@maxlength"/>
          </xsl:when>
          <xsl:otherwise><xsl:value-of select="$primarykeybean/property[@name=$primarykey]/@maxlength"/></xsl:otherwise>
        </xsl:choose>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="primarykey-javatype">
      <xsl:if test="count(@primarykey)!=0">
        <xsl:choose>
          <xsl:when test="count($primarykeybean/property[@name=$primarykey]/@type)!=0">
            <xsl:variable name="typename" select="$primarykeybean/property[@name=$primarykey]/@type"/>
            <xsl:value-of select="$primarykeybean/type[@name=$typename]/@javatype"/>
          </xsl:when>
          <xsl:otherwise><xsl:value-of select="$primarykeybean/property[@name=$primarykey]/@javatype"/></xsl:otherwise>
        </xsl:choose>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="primarykey-hibernatetype">
      <xsl:if test="count(@primarykey)!=0">
        <xsl:call-template name="hibernate-type">
          <xsl:with-param name="javatype" select="$primarykey-javatype"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:variable>

    <!-- primary key MUST be first -->
    <xsl:if test="count(@primarykey)!=0">
     <xsl:for-each select="$primarykeybean/property[@name=$primarykey]">
      <xsl:call-template name="handle-property">
        <xsl:with-param name="bean" select="$primarykeybean"/>
        <xsl:with-param name="table" select="$table"/>
        <xsl:with-param name="primarykey" select="$primarykey"/>
        <xsl:with-param name="columnnameprefix" select="''"/>
      </xsl:call-template>
     </xsl:for-each>
    </xsl:if>
    
    <xsl:call-template name="handle-bean-properties">
      <xsl:with-param name="beanfile">.</xsl:with-param>
      <xsl:with-param name="bean" select="/bean"/>
      <xsl:with-param name="table" select="$table"/>
      <xsl:with-param name="primarykey" select="$primarykey"/>
    </xsl:call-template>
<!--    <xsl:for-each select="property">
      <xsl:call-template name="handle-property">
        <xsl:with-param name="table" select="$table"/>
        <xsl:with-param name="primarykey" select="$primarykey"/>
        <xsl:with-param name="columnnameprefix" select="''"/>
      </xsl:call-template>
    </xsl:for-each> -->
  </class>
</hibernate-mapping> 
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
<xsl:template name="handle-bean-properties">
 <xsl:param name="beanfile"/>
 <xsl:param name="bean"/>
 <xsl:param name="table"/>
 <xsl:param name="primarykey"/>
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
        <!-- don't do primary key here -->
       <xsl:if test="@name!=$primarykey">
        <xsl:call-template name="handle-property">
        <xsl:with-param name="table" select="$table"/>
        <xsl:with-param name="primarykey" select="$primarykey"/>
        <xsl:with-param name="columnnameprefix" select="''"/>
        <xsl:with-param name="bean" select="$bean"/>
       </xsl:call-template>
      </xsl:if>

      </xsl:when>
      <xsl:otherwise>
        <xsl:comment>property <xsl:value-of select="@name"/> defined in <xsl:value-of select="$defining-beanfile"/></xsl:comment>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each> 
  <!-- hope there are no inheritance loops!! -->
  <xsl:for-each select="$bean/implements">
    <xsl:if test="count(./@beanfile)!=1">
      <xsl:message terminate="true">ERROR: Implemented beanfile <xsl:value-of select="$beanfile"/> has an implements element with no 'beanfile' attribute</xsl:message>
    </xsl:if>
    <xsl:call-template name="handle-bean-properties">
      <xsl:with-param name="beanfile" select="@beanfile[1]"/>
      <xsl:with-param name="bean" select="document(@beanfile[1])/bean"/>
      <xsl:with-param name="table" select="$table"/>
      <xsl:with-param name="primarykey" select="$primarykey"/>
    </xsl:call-template>
  </xsl:for-each>
</xsl:template>

<xsl:template name="handle-property">    
  <xsl:param name="table"/>
  <xsl:param name="primarykey"/>
  <xsl:param name="columnnameprefix"/>
  <xsl:param name="bean"/>
  
      <!-- each property... -->
      <xsl:variable name="isprimarykey">
        <xsl:choose>
          <xsl:when test="@name=/bean/@primarykey">yes</xsl:when>
          <xsl:otherwise>no</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:variable name="javatype"><xsl:call-template name="property-type"><xsl:with-param name="bean" select="$bean"/></xsl:call-template></xsl:variable>
      <xsl:variable name="hibernatetype">
        <xsl:call-template name="hibernate-type">
          <xsl:with-param name="javatype" select="$javatype"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="isaset">
        <xsl:choose>
          <xsl:when test="$javatype='java.util.Set'">yes</xsl:when>
          <xsl:otherwise>no</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:variable name="isacomponent">
        <xsl:choose>
          <xsl:when test="$hibernatetype='component'">yes</xsl:when>
          <xsl:otherwise>no</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:variable name="isanarray">
        <xsl:choose>
          <xsl:when test="$hibernatetype='array'">yes</xsl:when>
          <xsl:otherwise>no</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <!-- name of 'property' node -->
      <xsl:variable name="nodename">
        <xsl:choose>
          <xsl:when test="$isprimarykey='yes'">id</xsl:when>
          <xsl:when test="$isaset='yes'">set</xsl:when>
          <xsl:when test="$isacomponent='yes'">component</xsl:when>
          <xsl:when test="$isanarray='yes'">array</xsl:when>
          <xsl:otherwise>property</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      
      <!-- property node and normal attributes -->
      <xsl:element name="{$nodename}">
        <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
        <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
	<xsl:if test="$isaset!='yes' and $isacomponent!='yes' and $isanarray!='yes'">
	  <xsl:attribute name="type"><xsl:value-of select="$hibernatetype"/></xsl:attribute>
	</xsl:if>
	<!-- set -specific -->
	<xsl:if test="$isaset='yes'">
	  <xsl:attribute name="lazy">false</xsl:attribute>
	  <xsl:attribute name="table"><xsl:value-of select="$table"/>_<xsl:value-of select="@name"/></xsl:attribute>
	</xsl:if>
	<!-- array -specific -->
	<xsl:if test="$isanarray='yes'">
	  <xsl:attribute name="table"><xsl:value-of select="$table"/>_<xsl:value-of select="@name"/></xsl:attribute>
	  <xsl:attribute name="cascade">all</xsl:attribute> 
	</xsl:if>
        <!-- property column information, e.g. max length -->
	<xsl:if test="$isaset!='yes' and $isacomponent!='yes' and $isanarray!='yes'">
	  <column>
	    <xsl:attribute name="name"><xsl:value-of select="$columnnameprefix"/><xsl:value-of select="@name"/></xsl:attribute>
	    <!--<xsl:attribute name="type"><xsl:value-of select="$hibernatetype"/></xsl:attribute>-->
	    <xsl:variable name="maxlength"><xsl:call-template name="property-maxlength"><xsl:with-param name="bean" select="$bean"/></xsl:call-template></xsl:variable>
	    <xsl:if test="string-length($maxlength)!=0">
	      <xsl:attribute name="length"><xsl:value-of select="$maxlength"/></xsl:attribute>
	    </xsl:if>
	  </column>
	</xsl:if>
	<!-- component properties -->
	
	<xsl:if test="$isacomponent='yes'">
	  <xsl:variable name="filename"><xsl:value-of select="$javatype"/>.xml</xsl:variable>
	  <xsl:for-each select="document($filename)/bean/property">
	  <xsl:call-template name="handle-property">
	    <xsl:with-param name="bean" select="document($filename)/bean"/>
	    <xsl:with-param name="table" select="$table"/>
            <xsl:with-param name="primarykey" select="$primarykey"/>
            <xsl:with-param name="columnnameprefix"><xsl:value-of select="$columnnameprefix"/><xsl:value-of select="$name"/>_</xsl:with-param>
          </xsl:call-template>
          </xsl:for-each>
	</xsl:if>
	
	<!-- primary key generator -->
	<xsl:if test="$isprimarykey='yes'">
          <generator>
	    <xsl:attribute name="class">
              <xsl:choose>
                <xsl:when test="count(@primarykeygenerator)!=0">
	          <xsl:value-of select="@primarykeygenerator"/>
	        </xsl:when>
	        <!--  this is producing an error -->
                <xsl:when test="count(/bean/@primarykeygenerator)!=0">
	          <xsl:value-of select="/bean/@primarykeygenerator"/>
	        </xsl:when>
	        <xsl:otherwise>native</xsl:otherwise>
	      </xsl:choose>
	    </xsl:attribute>
          </generator>
        </xsl:if>

	<!-- set and array -specific -->
	<xsl:if test="$isaset='yes' or $isanarray='yes'">
	  <key>
	    <xsl:attribute name="column"><xsl:value-of select="$primarykey"/></xsl:attribute>
	    <!--<column>
	      <xsl:attribute name="name"><xsl:value-of select="$table"/>_id</xsl:attribute>
	      <xsl:attribute name="type"><xsl:value-of select="$primarykey-hibernatetype"/></xsl:attribute>
	      <xsl:if test="string-length($primarykey-maxlength)!=0">
	        <xsl:attribute name="length"><xsl:value-of select="$primarykey-maxlength"/></xsl:attribute>
	      </xsl:if>
	    </column>-->
	  </key>
	  <xsl:if test="$isanarray='yes'">
	    <xsl:element name="list-index"><xsl:attribute name="column">sortOrder</xsl:attribute></xsl:element>
	  </xsl:if> 
	  <xsl:variable name="elementjavatype">
	    <xsl:if test="$isaset='yes'">
	      <xsl:call-template name="property-elementtype"><xsl:with-param name="bean" select="$bean"/></xsl:call-template>
 	    </xsl:if>
	    <xsl:if test="$isanarray='yes'">
              <xsl:value-of select="substring($javatype,1,string-length($javatype)-2)"/>
	    </xsl:if>
	  </xsl:variable>
	  <xsl:variable name="elementmaxlength"><xsl:call-template name="property-elementmaxlength"><xsl:with-param name="bean" select="$bean"/></xsl:call-template></xsl:variable>
	  <xsl:variable name="elementhibernatetype">
	    <xsl:call-template name="hibernate-type">
	      <xsl:with-param name="javatype" select="$elementjavatype"/>
	    </xsl:call-template>
	  </xsl:variable>
          <xsl:variable name="elementisacomponent">
            <xsl:choose>
              <xsl:when test="$elementhibernatetype='component'">yes</xsl:when>
              <xsl:otherwise>no</xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <!-- name of 'property' node -->
          <xsl:variable name="elementnodename">
            <xsl:choose>
              <xsl:when test="$elementisacomponent='yes'">composite-element</xsl:when>
              <xsl:otherwise>element</xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
      
          <!-- property node and normal attributes -->
          <xsl:element name="{$elementnodename}">
	    <xsl:choose>
	      <xsl:when test="$elementisacomponent='yes'">
	        <!-- composite element -->
	        <xsl:attribute name="class"><xsl:value-of select="$elementjavatype"/></xsl:attribute>
	      
	      	<!-- component properties -->
	        <xsl:variable name="filename"><xsl:value-of select="$elementjavatype"/>.xml</xsl:variable>
		<xsl:for-each select="document($filename)/bean/property">
		  <xsl:call-template name="handle-property">
		    <xsl:with-param name="table" select="$table"/>
		    <xsl:with-param name="bean" select="document($filename)/bean"/>
		    <xsl:with-param name="primarykey" select="$primarykey"/>
		    <xsl:with-param name="columnnameprefix"><xsl:value-of select="$columnnameprefix"/></xsl:with-param>
		  </xsl:call-template>
		</xsl:for-each>

	      </xsl:when>
	      <xsl:otherwise>
	        <!-- primitive element -->
	        <xsl:attribute name="column"><xsl:value-of select="$columnnameprefix"/><xsl:value-of select="@name"/></xsl:attribute>
	        <xsl:attribute name="type"><xsl:value-of select="$elementhibernatetype"/></xsl:attribute>
	        <xsl:if test="string-length($elementmaxlength)!=0">
	          <xsl:attribute name="length"><xsl:value-of select="$elementmaxlength"/></xsl:attribute>
	        </xsl:if>
	      </xsl:otherwise>
	    </xsl:choose>
	  </xsl:element>
	</xsl:if>
      </xsl:element>
</xsl:template>

</xsl:stylesheet>
