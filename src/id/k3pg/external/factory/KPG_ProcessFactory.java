package id.k3pg.external.factory;

import org.adempiere.base.IProcessFactory;
import org.compiere.process.ProcessCall;

public class KPG_ProcessFactory implements IProcessFactory {

	@Override
	public ProcessCall newProcessInstance(String className) {
		ProcessCall process = null;
		try {
			Class<?> clazz = getClass().getClassLoader().loadClass(className);
			process = (ProcessCall) clazz.newInstance();
		} catch (Exception e) {
			
		}
		return process;
	}

}
