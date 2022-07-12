package id.k3pg.external.factory;

import java.util.List;

import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventManager;
import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MImage;
import org.compiere.model.MOrder;
import org.compiere.model.PO;
import org.osgi.service.event.Event;

import id.k3pg.external.event.model.KPG_ImagePOEvent;

public class KPG_EventFactory extends AbstractEventHandler {

    @Override
    protected void doHandleEvent(Event event) {
        List<String> errors = (List<String>) event.getProperty(IEventManager.EVENT_ERROR_MESSAGES);
        for(int a = 0; a < errors.size(); a++) {
            String error = errors.get(a);
            if (error == null) {
                errors.remove(a);
            }
        }
        
        if (event.getTopic().contains(IEventTopics.DOC_EVENT_PREFIX))
            doHandleDocEvent(event);
        else if (event.getTopic().contains(IEventTopics.MODEL_EVENT_PREFIX))
            doHandleModelEvent(event);
    }

    @Override
    protected void initialize() {
        registerEvent(IEventTopics.PO_ALL);
        registerEvent(IEventTopics.DOC_ALL);
    }

    private void doHandleModelEvent(Event event) {
        PO po = getPO(event);
        switch (po.get_TableName()) {
            case MImage.Table_Name:
            	KPG_ImagePOEvent.validate(event, po);
            	
              break;
          
            default:
                break;
        }
    }

    private void doHandleDocEvent(Event event) {
        PO po = getPO(event);
        
        switch (po.get_TableName()) {
            case MOrder.Table_Name:
//              KPG_OrderDocEvent.validate(event, po);
              break;
          
            default:
                  break;
        }
    }

}
