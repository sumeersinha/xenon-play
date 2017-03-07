package com.sumeer.xenon.play;

import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.StatelessService;
import com.vmware.xenon.common.UriUtils;

import java.util.Map;

/**
 * Get count of all address book entries by name, if name not specified in URL, get all.
 *
 * URI_NAMESPACE_OWNER results in this service handling requests that are directed towards url that contains the service's
 * SELF_LINK url i.e. a request to
 * - http://url:port/core/addressbookcount/{A}
 * - http://url:port/core/addressbookcount/{A}/{B}
 * will be routed to this service
 *
 * Created by sumeers on 3/7/17.
 */
// TODO: Change name of class once you figure out querying
public class AddressBookCountStatelessService extends StatelessService {
    public static final String FACTORY_LINK="/core/addressbookcount";

    public AddressBookCountStatelessService() {
        super();
        super.toggleOption(ServiceOption.URI_NAMESPACE_OWNER, true);
    }

    /**
     * Get request to /core/addressbookcount , check for parameters if any and filter the result accordingly
     * or just return all
     *
     * @param getOp - xenon get operation
     */
    @Override
    public void handleGet(Operation getOp){
        String template = "/core/addressbookcount/{servicename}/{name}";

        // UriUtils will help us here to map the params out of string
        Map<String, String> parameters = UriUtils.parseUriPathSegments(getOp.getUri(), template);

        String name = parameters.get("name");

        // TODO: This is juvenile, in a future commit once I figure out querying in xenon, change it to do something meaningful
        System.out.println("name:"+name);
        parameters.put("fake-count", "3");

        getOp.setBody(parameters);
        getOp.complete();
    }

}
