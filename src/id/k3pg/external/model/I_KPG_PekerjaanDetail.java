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
package id.k3pg.external.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for KPG_PekerjaanDetail
 *  @author iDempiere (generated) 
 *  @version Release 9
 */
@SuppressWarnings("all")
public interface I_KPG_PekerjaanDetail 
{

    /** TableName=KPG_PekerjaanDetail */
    public static final String Table_Name = "KPG_PekerjaanDetail";

    /** AD_Table_ID=1000001 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name DateTrx */
    public static final String COLUMNNAME_DateTrx = "DateTrx";

	/** Set Transaction Date.
	  * Transaction Date
	  */
	public void setDateTrx (Timestamp DateTrx);

	/** Get Transaction Date.
	  * Transaction Date
	  */
	public Timestamp getDateTrx();

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name KPG_PekerjaanDetail_ID */
    public static final String COLUMNNAME_KPG_PekerjaanDetail_ID = "KPG_PekerjaanDetail_ID";

	/** Set Pekerjaan Detail	  */
	public void setKPG_PekerjaanDetail_ID (int KPG_PekerjaanDetail_ID);

	/** Get Pekerjaan Detail	  */
	public int getKPG_PekerjaanDetail_ID();

    /** Column name KPG_PekerjaanDetail_UU */
    public static final String COLUMNNAME_KPG_PekerjaanDetail_UU = "KPG_PekerjaanDetail_UU";

	/** Set KPG_PekerjaanDetail_UU	  */
	public void setKPG_PekerjaanDetail_UU (String KPG_PekerjaanDetail_UU);

	/** Get KPG_PekerjaanDetail_UU	  */
	public String getKPG_PekerjaanDetail_UU();

    /** Column name KPG_Pekerjaan_ID */
    public static final String COLUMNNAME_KPG_Pekerjaan_ID = "KPG_Pekerjaan_ID";

	/** Set Pekerjaan	  */
	public void setKPG_Pekerjaan_ID (int KPG_Pekerjaan_ID);

	/** Get Pekerjaan	  */
	public int getKPG_Pekerjaan_ID();

	public I_KPG_Pekerjaan getKPG_Pekerjaan() throws RuntimeException;

    /** Column name KPG_Photo_ID */
    public static final String COLUMNNAME_KPG_Photo_ID = "KPG_Photo_ID";

	/** Set Photo	  */
	public void setKPG_Photo_ID (int KPG_Photo_ID);

	/** Get Photo	  */
	public int getKPG_Photo_ID();

    /** Column name KPG_ProgressPercent */
    public static final String COLUMNNAME_KPG_ProgressPercent = "KPG_ProgressPercent";

	/** Set Progress (%)	  */
	public void setKPG_ProgressPercent (BigDecimal KPG_ProgressPercent);

	/** Get Progress (%)	  */
	public BigDecimal getKPG_ProgressPercent();

    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setName (String Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getName();

    /** Column name Sequence */
    public static final String COLUMNNAME_Sequence = "Sequence";

	/** Set Sequence	  */
	public void setSequence (int Sequence);

	/** Get Sequence	  */
	public int getSequence();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();

    /** Column name Value */
    public static final String COLUMNNAME_Value = "Value";

	/** Set Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value);

	/** Get Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public String getValue();
}
