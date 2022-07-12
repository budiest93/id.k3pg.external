package id.k3pg.external.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.exceptions.DBException;
import org.compiere.model.MBPartner;
import org.compiere.model.MColumn;
import org.compiere.model.MElement;
import org.compiere.model.MOrder;
import org.compiere.model.MRequisition;
import org.compiere.model.MTable;
import org.compiere.model.M_Element;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;

public class KPG_GenerateStandardColumn extends SvrProcess {
    private int p_AD_Table_ID = 0;
    boolean p_IsTransaction = false;
    int p_ParentID = 0;
    
    @Override
    protected void prepare() {
        // Get Parameter
        ProcessInfoParameter[] para = getParameter();
        for (int i = 0; i < para.length; i++) {
            String name = para[i].getParameterName();
            if (name.equals("AD_Table_ID")) {
              p_AD_Table_ID = para[i].getParameterAsInt();
            } else if (name.equals("IsTransaction")) {
              p_IsTransaction = para[i].getParameterAsBoolean();
            } else if (name.equals("ParentID")) {
              p_ParentID = para[i].getParameterAsInt();
            } else {
                log.log(Level.SEVERE, "Unknown Parameter: " + name);
            }
        }
    }

    @Override
    protected String doIt() throws Exception {
      MTable table = new MTable(getCtx(), p_AD_Table_ID, get_TrxName());
      if (table.getColumn(MBPartner.COLUMNNAME_AD_Client_ID) != null) {
          throw new AdempiereException("Kolom harus kosong!");
      }
      
      MColumn column = null;
      M_Element element = null;
      
      //table column id
      column = new MColumn(table);
      element = M_Element.get(getCtx(), table.getTableName().concat("_ID"));
      if (element == null) {
        element = new M_Element(getCtx(), 0, get_TrxName());
      }
      element.setColumnName(table.getTableName().concat("_ID"));
      element.setName(table.getName());
      element.setPrintName(element.getName());
      element.setEntityType(MElement.ENTITYTYPE_UserMaintained);
      element.saveEx();
      setElement(column, element);
      setColumn(column);
      column.saveEx();
      
      createStandardColumn(table, MBPartner.COLUMNNAME_AD_Client_ID);
      createStandardColumn(table, MBPartner.COLUMNNAME_AD_Org_ID);
      createStandardColumn(table, MBPartner.COLUMNNAME_IsActive);
      createStandardColumn(table, MBPartner.COLUMNNAME_Created);
      createStandardColumn(table, MBPartner.COLUMNNAME_CreatedBy);
      createStandardColumn(table, MBPartner.COLUMNNAME_Updated);
      createStandardColumn(table, MBPartner.COLUMNNAME_UpdatedBy);
      createStandardColumn(table, MBPartner.COLUMNNAME_Description);
      
      if (!p_IsTransaction) {
        createStandardColumn(table, MBPartner.COLUMNNAME_Name);
        createStandardColumn(table, MBPartner.COLUMNNAME_Value);
        if (p_ParentID > 0) {
          MTable parent = new MTable(getCtx(), p_ParentID, get_TrxName());
          createStandardColumn(table, parent.getTableName().concat("_ID"));
        }
        
      } else {
        if (p_ParentID > 0) {
          MTable parent = new MTable(getCtx(), p_ParentID, get_TrxName());
          createStandardColumn(table, parent.getTableName().concat("_ID"));
          createStandardColumn(table, MOrder.COLUMNNAME_Processed);
          createStandardColumn(table, "LineNo");
        } else {
            createStandardColumn(table, MOrder.COLUMNNAME_C_DocType_ID);
            createStandardColumn(table, MOrder.COLUMNNAME_DateAcct);
            createStandardColumn(table, MRequisition.COLUMNNAME_DateDoc);
            createStandardColumn(table, MOrder.COLUMNNAME_DocAction);
            createStandardColumn(table, MOrder.COLUMNNAME_DocStatus);
            createStandardColumn(table, MOrder.COLUMNNAME_DocumentNo);
            createStandardColumn(table, MOrder.COLUMNNAME_IsApproved);
            createStandardColumn(table, MOrder.COLUMNNAME_Processed);
            createStandardColumn(table, MOrder.COLUMNNAME_User1_ID);
        }
      }
      
      return "Success!";
    }
    
    void createStandardColumn(MTable table, String columnName) {
      MColumn column = new MColumn(table);
      M_Element element = M_Element.get(getCtx(), columnName);
      setElement(column, element);
      setColumn(column);
      column.saveEx();
    }
    
    void setElement(MColumn column, M_Element element) {
      column.setAD_Element_ID(element.get_ID());
      column.setColumnName(element.getColumnName());
      column.setName(element.getName());
      column.setDescription(element.getDescription());
      column.setHelp(element.getHelp());
    }
    
    void setColumn(MColumn column) {
   // IDEMPIERE-1011
      if (PO.getUUIDColumnName(column.getAD_Table().getTableName()).equals(column.getColumnName())) {
          // UUID column
          column.setAD_Reference_ID(DisplayType.String);
          column.setAD_Val_Rule_ID(0);
          column.setAD_Reference_Value_ID(0);
          column.setFieldLength(36);
          column.setDefaultValue(null);
          column.setMandatoryLogic(null);
          column.setReadOnlyLogic(null);
          column.setIsIdentifier(false);
          column.setIsUpdateable(false);
          column.setIsAlwaysUpdateable(false);
          column.setIsKey(false);
      } else if (column.getAD_Table().getTableName().concat("_ID").equals(column.getColumnName())) {
          // ID key column
          column.setAD_Reference_ID(DisplayType.ID);
          column.setAD_Val_Rule_ID(0);
          column.setAD_Reference_Value_ID(0);
          column.setFieldLength(22);
          column.setDefaultValue(null);
          column.setMandatoryLogic(null);
          column.setReadOnlyLogic(null);
          column.setIsIdentifier(false);
          column.setIsUpdateable(false);
          column.setIsAlwaysUpdateable(false);
          column.setIsKey(true);
      } else {
          // get defaults from most used case
          String sql = ""
                  + "SELECT AD_Reference_ID, "
                  + "       AD_Val_Rule_ID, "
                  + "       AD_Reference_Value_ID, "
                  + "       FieldLength, "
                  + "       DefaultValue, "
                  + "       MandatoryLogic, "
                  + "       ReadOnlyLogic, "
                  + "       IsIdentifier, "
                  + "       IsUpdateable, "
                  + "       IsAlwaysUpdateable, "
                  + "       FKConstraintType,"    
                  + "       COUNT(*) "
                  + "FROM   AD_Column "
                  + "WHERE  ColumnName = ? "
                  + "GROUP  BY AD_Reference_ID, "
                  + "          AD_Val_Rule_ID, "
                  + "          AD_Reference_Value_ID, "
                  + "          FieldLength, "
                  + "          DefaultValue, "
                  + "          MandatoryLogic, "
                  + "          ReadOnlyLogic, "
                  + "          IsIdentifier, "
                  + "          IsUpdateable, "
                  + "          IsAlwaysUpdateable, "
                  + "          FKConstraintType "
                  + "ORDER  BY COUNT(*) DESC ";
          PreparedStatement pstmt = null;
          ResultSet rs = null;
          try
          {
              pstmt = DB.prepareStatement(sql, get_TrxName());
              pstmt.setString(1, column.getColumnName());
              rs = pstmt.executeQuery();
              if (rs.next()) {
                  int ad_reference_id = rs.getInt(1);
                  if (ad_reference_id == DisplayType.ID)
                      ad_reference_id = DisplayType.TableDir;
                  column.setAD_Reference_ID(ad_reference_id);
                  column.setAD_Val_Rule_ID(rs.getInt(2));
                  column.setAD_Reference_Value_ID(rs.getInt(3));
                  column.setFieldLength(rs.getInt(4));
                  column.setDefaultValue(rs.getString(5));
                  column.setMandatoryLogic(rs.getString(6));
                  column.setReadOnlyLogic(rs.getString(7));
                  column.setIsIdentifier("Y".equals(rs.getString(8)));
                  column.setIsUpdateable("Y".equals(rs.getString(9)));
                  column.setIsAlwaysUpdateable("Y".equals(rs.getString(10)));
                  column.setFKConstraintType(rs.getString(11));
              }
          }
          catch (SQLException e)
          {
              throw new DBException(e);
          }
          finally
          {
              DB.close(rs, pstmt);
              rs = null;
              pstmt = null;
          }
      }
    }
}
