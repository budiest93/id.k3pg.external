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

/** Generated Model for KPG_Pekerjaan
 *  @author iDempiere (generated) 
 *  @version Release 9 - $Id$ */
@org.adempiere.base.Model(table="KPG_Pekerjaan")
public class X_KPG_Pekerjaan extends PO implements I_KPG_Pekerjaan, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20220708L;

    /** Standard Constructor */
    public X_KPG_Pekerjaan (Properties ctx, int KPG_Pekerjaan_ID, String trxName)
    {
      super (ctx, KPG_Pekerjaan_ID, trxName);
      /** if (KPG_Pekerjaan_ID == 0)
        {
        } */
    }

    /** Standard Constructor */
    public X_KPG_Pekerjaan (Properties ctx, int KPG_Pekerjaan_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, KPG_Pekerjaan_ID, trxName, virtualColumns);
      /** if (KPG_Pekerjaan_ID == 0)
        {
        } */
    }

    /** Load Constructor */
    public X_KPG_Pekerjaan (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_KPG_Pekerjaan[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_User getAD_User() throws RuntimeException
	{
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
			.getPO(getAD_User_ID(), get_TrxName());
	}

	/** Set User/Contact.
		@param AD_User_ID User within the system - Internal or Business Partner Contact
	*/
	public void setAD_User_ID (int AD_User_ID)
	{
		if (AD_User_ID < 1)
			set_Value (COLUMNNAME_AD_User_ID, null);
		else
			set_Value (COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
	}

	/** Get User/Contact.
		@return User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_ID)
			.getPO(getC_BPartner_ID(), get_TrxName());
	}

	/** Set Business Partner.
		@param C_BPartner_ID Identifies a Business Partner
	*/
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1)
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner.
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Date From.
		@param DateFrom Starting date for a range
	*/
	public void setDateFrom (Timestamp DateFrom)
	{
		set_Value (COLUMNNAME_DateFrom, DateFrom);
	}

	/** Get Date From.
		@return Starting date for a range
	  */
	public Timestamp getDateFrom()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateFrom);
	}

	/** Set Date To.
		@param DateTo End date of a date range
	*/
	public void setDateTo (Timestamp DateTo)
	{
		set_Value (COLUMNNAME_DateTo, DateTo);
	}

	/** Get Date To.
		@return End date of a date range
	  */
	public Timestamp getDateTo()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateTo);
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

	/** Set Nomor OK.
		@param KPG_NoOK Nomor OK
	*/
	public void setKPG_NoOK (String KPG_NoOK)
	{
		set_Value (COLUMNNAME_KPG_NoOK, KPG_NoOK);
	}

	/** Get Nomor OK.
		@return Nomor OK	  */
	public String getKPG_NoOK()
	{
		return (String)get_Value(COLUMNNAME_KPG_NoOK);
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

	/** Set KPG_Pekerjaan_UU.
		@param KPG_Pekerjaan_UU KPG_Pekerjaan_UU
	*/
	public void setKPG_Pekerjaan_UU (String KPG_Pekerjaan_UU)
	{
		set_Value (COLUMNNAME_KPG_Pekerjaan_UU, KPG_Pekerjaan_UU);
	}

	/** Get KPG_Pekerjaan_UU.
		@return KPG_Pekerjaan_UU	  */
	public String getKPG_Pekerjaan_UU()
	{
		return (String)get_Value(COLUMNNAME_KPG_Pekerjaan_UU);
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