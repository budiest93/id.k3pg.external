package id.k3pg.external.model;

import java.sql.ResultSet;
import java.util.Properties;

public class KPG_MPekerjaan extends X_KPG_Pekerjaan {

	private static final long serialVersionUID = 8586420619534026898L;

	public KPG_MPekerjaan(Properties ctx, int KPG_Pekerjaan_ID, String trxName) {
		super(ctx, KPG_Pekerjaan_ID, trxName);
	}

	public KPG_MPekerjaan(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

}
