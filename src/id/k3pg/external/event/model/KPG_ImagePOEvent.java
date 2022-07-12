package id.k3pg.external.event.model;

import org.adempiere.base.event.IEventTopics;
import org.compiere.model.PO;
import org.osgi.service.event.Event;

import id.k3pg.external.businessprocess.KPG_ImageBeforeSaveCompress;

public class KPG_ImagePOEvent {

    public static void validate(Event event, PO po) {
        switch (event.getTopic()) {
            case IEventTopics.PO_BEFORE_NEW:
//            	imageBeforeSaveCompress(po);
              
                break;
            case IEventTopics.PO_AFTER_NEW:
            	imageBeforeSaveCompress(po);
                
                break;
            case IEventTopics.PO_AFTER_NEW_REPLICATION:

                break;
            case IEventTopics.PO_BEFORE_CHANGE:
//            	imageBeforeSaveCompress(po);
            	
                break;
            case IEventTopics.PO_AFTER_CHANGE:
            	imageBeforeSaveCompress(po);

                break;
            case IEventTopics.PO_AFTER_CHANGE_REPLICATION:

                break;
            case IEventTopics.PO_BEFORE_DELETE:

                break;
            case IEventTopics.PO_AFTER_DELETE:

                break;
            case IEventTopics.PO_BEFORE_DELETE_REPLICATION:

                break;
            case IEventTopics.PO_POST_CREATE:

                break;
            case IEventTopics.PO_POST_UPADTE:

                break;
            case IEventTopics.PO_POST_DELETE:

                break;
            case IEventTopics.PO_ALL:

                break;

            default:
                break;
        }
    }

    private static void imageBeforeSaveCompress(PO po) {
        new KPG_ImageBeforeSaveCompress(po.getCtx(), po, po.get_TrxName()).execute();
    }
}
