package id.k3pg.external.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.compiere.util.DB;

public class KPG_MPekerjaanDetail extends X_KPG_PekerjaanDetail {
	
	private static final long serialVersionUID = 4031588805541512760L;

	public KPG_MPekerjaanDetail(Properties ctx, int KPG_PekerjaanDetail_ID, String trxName) {
		super(ctx, KPG_PekerjaanDetail_ID, trxName);
	}

	public KPG_MPekerjaanDetail(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		List<Object> params = new ArrayList<>();
		params.clear();
	    params.add(getKPG_Pekerjaan_ID());
	    params.add(get_ID());
	    int seq = DB.getSQLValueEx(get_TrxName(),
	        "select " + 
	        "    (coalesce(max(\"sequence\"),0) + 10)::int maxid " + 
	        "from " +KPG_MPekerjaanDetail.Table_Name + 
	        " where "+COLUMNNAME_KPG_Pekerjaan_ID+" = ? " +
	        "and  "+COLUMNNAME_KPG_PekerjaanDetail_ID + "!=? ", params);
	    setSequence(seq);
		return super.beforeSave(newRecord);
	}
	
	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {
		BigDecimal progress = DB.getSQLValueBDEx(get_TrxName(),
				"select "
				+ "	max(pd.kpg_progresspercent) progress "
				+ "from kpg_pekerjaandetail pd "
				+ "where pd.kpg_pekerjaan_id = ? ", getKPG_Pekerjaan_ID());
		KPG_MPekerjaan p = (KPG_MPekerjaan) getKPG_Pekerjaan();
		p.setKPG_ProgressPercent(progress);
		p.saveEx();
		return super.afterSave(newRecord, success);
	}
}
