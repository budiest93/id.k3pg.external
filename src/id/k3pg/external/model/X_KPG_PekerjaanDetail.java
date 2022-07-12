/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package id.k3pg.external.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for KPG_PekerjaanDetail
 *  @author iDempiere (generated) 
 *  @version Release 9 - $Id$ */
@org.adempiere.base.Model(table="KPG_PekerjaanDetail")
public class X_KPG_PekerjaanDetail extends PO implements I_KPG_PekerjaanDetail, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20220708L;

    /** Standard Constructor */
    public X_KPG_PekerjaanDetail (Properties ctx, int KPG_PekerjaanDetail_ID, String trxName)
    {
      super (ctx, KPG_PekerjaanDetail_ID, trxName);
      /** if (KPG_PekerjaanDetail_ID == 0)
        {
        } */
    }

    /** Standard Constructor */
    public X_KPG_PekerjaanDetail (Properties ctx, int KPG_PekerjaanDetail_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, KPG_PekerjaanDetail_ID, trxName, virtualColumns);
      /** if (KPG_PekerjaanDetail_ID == 0)
        {
        } */
    }

    /** Load Constructor */
    public X_KPG_PekerjaanDetail (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_KPG_PekerjaanDetail[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	/** Set Transaction Date.
		@param DateTrx Transaction Date
	*/
	public void setDateTrx (Timestamp DateTrx)
	{
		set_Value (COLUMNNAME_DateTrx, DateTrx);
	}

	/** Get Transaction Date.
		@return Transaction Date
	  */
	public Timestamp getDateTrx()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateTrx);
	}

	/** Set Description.
		@param Description Optional short description of the record
	*/
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription()
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Pekerjaan Detail.
		@param KPG_PekerjaanDetail_ID Pekerjaan Detail
	*/
	public void setKPG_PekerjaanDetail_ID (int KPG_PekerjaanDetail_ID)
	{
		if (KPG_PekerjaanDetail_ID < 1)
			set_ValueNoCheck (COLUMNNAME_KPG_PekerjaanDetail_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_KPG_PekerjaanDetail_ID, Integer.valueOf(KPG_PekerjaanDetail_ID));
	}

	/** Get Pekerjaan Detail.
		@return Pekerjaan Detail	  */
	public int getKPG_PekerjaanDetail_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_KPG_PekerjaanDetail_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set KPG_PekerjaanDetail_UU.
		@param KPG_PekerjaanDetail_UU KPG_PekerjaanDetail_UU
	*/
	public void setKPG_PekerjaanDetail_UU (String KPG_PekerjaanDetail_UU)
	{
		set_Value (COLUMNNAME_KPG_PekerjaanDetail_UU, KPG_PekerjaanDetail_UU);
	}

	/** Get KPG_PekerjaanDetail_UU.
		@return KPG_PekerjaanDetail_UU	  */
	public String getKPG_PekerjaanDetail_UU()
	{
		return (String)get_Value(COLUMNNAME_KPG_PekerjaanDetail_UU);
	}

	public I_KPG_Pekerjaan getKPG_Pekerjaan() throws RuntimeException
	{
		return (I_KPG_Pekerjaan)MTable.get(getCtx(), I_KPG_Pekerjaan.Table_ID)
			.getPO(getKPG_Pekerjaan_ID(), get_TrxName());
	}

	/** Set Pekerjaan.
		@param KPG_Pekerjaan_ID Pekerjaan
	*/
	public void setKPG_Pekerjaan_ID (int KPG_Pekerjaan_ID)
	{
		if (KPG_Pekerjaan_ID < 1)
			set_ValueNoCheck (COLUMNNAME_KPG_Pekerjaan_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_KPG_Pekerjaan_ID, Integer.valueOf(KPG_Pekerjaan_ID));
	}

	/** Get Pekerjaan.
		@return Pekerjaan	  */
	public int getKPG_Pekerjaan_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_KPG_Pekerjaan_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Photo.
		@param KPG_Photo_ID Photo
	*/
	public void setKPG_Photo_ID (int KPG_Photo_ID)
	{
		if (KPG_Photo_ID < 1)
			set_Value (COLUMNNAME_KPG_Photo_ID, null);
		else
			set_Value (COLUMNNAME_KPG_Photo_ID, Integer.valueOf(KPG_Photo_ID));
	}

	/** Get Photo.
		@return Photo	  */
	public int getKPG_Photo_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_KPG_Photo_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Progress (%).
		@param KPG_ProgressPercent Progress (%)
	*/
	public void setKPG_ProgressPercent (BigDecimal KPG_ProgressPercent)
	{
		set_Value (COLUMNNAME_KPG_ProgressPercent, KPG_ProgressPercent);
	}

	/** Get Progress (%).
		@return Progress (%)	  */
	public BigDecimal getKPG_ProgressPercent()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_KPG_ProgressPercent);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Name.
		@param Name Alphanumeric identifier of the entity
	*/
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName()
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Set Sequence.
		@param Sequence Sequence
	*/
	public void setSequence (int Sequence)
	{
		set_Value (COLUMNNAME_Sequence, Integer.valueOf(Sequence));
	}

	/** Get Sequence.
		@return Sequence	  */
	public int getSequence()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Sequence);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Search Key.
		@param Value Search key for the record in the format required - must be unique
	*/
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue()
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}