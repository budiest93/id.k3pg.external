package id.k3pg.external.factory;

import java.util.logging.Level;

import org.adempiere.webui.factory.IFormFactory;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.IFormController;
import org.compiere.util.CLogger;

public class KPG_FormFactory  implements IFormFactory {
	
	protected transient CLogger log = CLogger.getCLogger(getClass());

	@Override
	public ADForm newFormInstance(String formName) {
		if (formName.startsWith("org.k3pg.form")) {
			Object form = null;
			Class<?> clazz = null;
			ClassLoader loader = getClass().getClassLoader();
			try {
				clazz = loader.loadClass(formName);
			} catch (Exception e) {
				log.log(Level.SEVERE, "Load Form Class Failed");
			}

			if (clazz != null) {
				try {
					form = clazz.newInstance();
				} catch (Exception e) {
					log.log(Level.SEVERE, "Class initialized error");
				}
			}
			if (form != null) {
				if (form instanceof ADForm) {
					return (ADForm) form;
				} else if (form instanceof IFormController) {
					IFormController controller = (IFormController) form;
					ADForm adform = controller.getForm();
					adform.setICustomForm(controller);
					return adform;
				}
			}
		}
		return null;
	}

}
