package id.k3pg.external.businessprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MImage;
import org.compiere.model.MSysConfig;
import org.compiere.model.PO;
import org.compiere.util.DB;
import org.k3pg.interfaces.KPG_I_PerformExecution;

import id.k3pg.external.util.KPG_Utils;

public class KPG_ImageBeforeSaveCompress implements KPG_I_PerformExecution{
	List<Object> params = new ArrayList<>();
	 private Properties ctx;
	 private PO po = null;
	 private String trxName;
	 KPG_Utils ku = new KPG_Utils();

	 public KPG_ImageBeforeSaveCompress(Properties ctx, PO po, String trxName) {
	     this.ctx = ctx;
	     this.po = po;
	     this.trxName = trxName;
	 }

	 @Override
	 public void execute() {
	     MImage img = (MImage)po;
			if (img.getBinaryData() == null
					|| MSysConfig.getBooleanValue(MSysConfig.IMAGE_DB_STORAGE_SAVE_AS_ZIP, false)) {
				return;
			}
	     byte[] arrPhoto = img.getBinaryData();
	     List<Object> params = new ArrayList<Object>();
	     params.add(ku.compressImage(arrPhoto, 0.5f));
	     params.add(img.get_ID());
	     DB.executeUpdateEx(
    		 	"update ad_image "
    		 	+ "set binarydata = ? "
    		 	+ "where ad_image_id = ? ", params.toArray(), trxName);
	 }
}
