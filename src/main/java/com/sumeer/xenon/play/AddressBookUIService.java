package com.sumeer.xenon.play;

import com.vmware.xenon.common.FactoryService;
import com.vmware.xenon.common.Service;
import com.vmware.xenon.common.ServiceDocument;
import com.vmware.xenon.services.common.ServiceUriPaths;
import com.vmware.xenon.services.common.UiContentService;

/**
 * Created by sumeers on 3/6/17.
 */
public class AddressBookUIService extends UiContentService {
    public static final String SELF_LINK = ServiceUriPaths.CORE + "/ui/addressbook";
}
