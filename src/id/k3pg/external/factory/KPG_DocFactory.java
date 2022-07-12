package id.k3pg.external.factory;

import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.logging.Level;

import org.adempiere.base.IDocFactory;
import org.compiere.acct.Doc;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MTable;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;


public class KPG_DocFactory  implements IDocFactory{

	private final static CLogger s_log = CLogger.getCLogger(KPG_DocFactory.class);

	private static HashMap<String, String> mapTableModels = new HashMap<String, String>();
    static {
//      mapTableModels.put(MJournal.Table_Name, "org.k3pg.doc.KPG_Doc_GLJournal");   
        
    }
	
	@Override
	public Doc getDocument(MAcctSchema as, int AD_Table_ID, int Record_ID, String trxName) {
		String tableName = MTable.getTableName(Env.getCtx(), AD_Table_ID);
		//
		Doc doc = null;
		StringBuffer sql = new StringBuffer("SELECT * FROM ").append(tableName).append(" WHERE ").append(tableName)
				.append("_ID=? AND Processed='Y'");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(sql.toString(), trxName);
			pstmt.setInt(1, Record_ID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				doc = getDocument(as, AD_Table_ID, rs, trxName);
			} else
				s_log.severe("Not Found: " + tableName + "_ID=" + Record_ID);
		} catch (Exception e) {
			s_log.log(Level.SEVERE, sql.toString(), e);
		} finally {
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}	
		return doc;
	}
	
	@Override
	public Doc getDocument(MAcctSchema as, int AD_Table_ID, ResultSet rs, String trxName) {
		String tableName = MTable.getTableName(Env.getCtx(), AD_Table_ID);
		if (mapTableModels.containsKey(tableName)) {
            Class<?> clazz = null;
            Constructor<?> ctor = null;
            Doc object = null;
            try {
                clazz = Class.forName(mapTableModels.get(tableName));
                ctor = clazz.getConstructor(MAcctSchema.class, ResultSet.class, String.class);
                object = (Doc) ctor.newInstance(new Object[] { as, rs, trxName });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return object;
        } else
            return null;
	}

}
