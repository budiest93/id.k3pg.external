package id.k3pg.external.factory;

import org.compiere.grid.ICreateFrom;
import org.compiere.grid.ICreateFromFactory;
import org.compiere.model.GridTab;


public class KPG_CreateFromFactory implements ICreateFromFactory {

	@Override
	public ICreateFrom create(GridTab mTab) {
		String tableName = mTab.getTableName();
		
//		if (tableName.equals(X_C_Order.Table_Name)) { 
//			return new KPG_WCreateFromOrder(mTab);
//		}
		
		return null;
	}

}
