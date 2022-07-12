package id.k3pg.external.factory;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Properties;

import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.Env;

import id.k3pg.external.model.KPG_MPekerjaan;
import id.k3pg.external.model.KPG_MPekerjaanDetail;


public class KPG_ModelFactory implements IModelFactory {

	private static HashMap<String, String> mapTableModels = new HashMap<String, String>();
	static {
		mapTableModels.put(KPG_MPekerjaan.Table_Name, getPackageName(KPG_MPekerjaan.Table_Name));
		mapTableModels.put(KPG_MPekerjaanDetail.Table_Name, getPackageName(KPG_MPekerjaanDetail.Table_Name));
		
	}
	
	public static String getPackageName(String tablename) {
		return "id.k3pg.external.model.KPG_M"+tablename.replace("KPG_", "");
	}
	
	@Override
	public Class<?> getClass(String tableName) {
		if (mapTableModels.containsKey(tableName)) {
			Class<?> act = null;
			try {
				act = Class.forName(mapTableModels.get(tableName));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return act;

		} else
			return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		if (mapTableModels.containsKey(tableName)) {
			Class<?> clazz = null;
			Constructor<?> ctor = null;
			PO object = null;
			try {
				clazz = Class.forName(mapTableModels.get(tableName));
				ctor = clazz.getConstructor(Properties.class, int.class, String.class);
				object = (PO) ctor.newInstance(new Object[] { Env.getCtx(), Record_ID, trxName });

			} catch (Exception e) {
				e.printStackTrace();
			}
			return object;
		} else
			return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
		if (mapTableModels.containsKey(tableName)) {
			Class<?> clazz = null;
			Constructor<?> ctor = null;
			PO object = null;
			try {
				clazz = Class.forName(mapTableModels.get(tableName));
				ctor = clazz.getConstructor(Properties.class, ResultSet.class, String.class);
				object = (PO) ctor.newInstance(new Object[] { Env.getCtx(), rs, trxName });

			} catch (Exception e) {
				e.printStackTrace();
			}
			return object;

		} else
			return null;
	}

}
