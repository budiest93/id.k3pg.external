package id.k3pg.external.factory;

import java.lang.reflect.Method;

import org.adempiere.base.ICalloutFactory;
import org.adempiere.base.ServiceQuery;
import org.adempiere.base.equinox.EquinoxExtensionLocator;
import org.compiere.model.Callout;

public class KPG_BaseCalloutFactory implements ICalloutFactory {


    public KPG_BaseCalloutFactory() {
        // default constructors
    }

    @Override
    public Callout getCallout(String className, String methodName) {
        if (className.contains("org.k3pg.callout")) {
            Callout callout = null;
            callout = EquinoxExtensionLocator.instance()
                    .locate(Callout.class, Callout.class.getName(), className, (ServiceQuery) null)
                    .getExtension();

            if (callout == null) {
                Class<?> calloutClass = null;
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                if (classLoader != null) {
                    try {
                        calloutClass = classLoader.loadClass(className);
                    } catch (ClassNotFoundException ex) {

                    }
                }
                if (calloutClass == null) {
                    classLoader = this.getClass().getClassLoader();
                    try {
                        calloutClass = classLoader.loadClass(className);
                    } catch (ClassNotFoundException ex) {

                    }
                }

                if (calloutClass == null) {
                    return null;
                }

                try {
                    callout = (Callout) calloutClass.newInstance();
                } catch (Exception ex) {
                    return null;
                }

                Method[] methods = calloutClass.getDeclaredMethods();
                for (int i = 0; i < methods.length; i++) {
                    if (methods[i].getName().equals(methodName)) {
                        return callout;
                    }
                }
            }
        }
        return null;
    }
}
