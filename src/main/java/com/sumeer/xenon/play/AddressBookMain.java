package com.sumeer.xenon.play;

import com.vmware.xenon.common.ServiceHost;
import com.vmware.xenon.services.common.RootNamespaceService;

/**
 * Created by sumeersinha on 3/5/17.
 */
public class AddressBookMain {
    public static void main(String[] args) throws Throwable {
        // Create Xenon service host
        ServiceHost serviceHost = ServiceHost.create("--sandbox=/tmp/multinode/xenondb");
        // Start host
        serviceHost.start();
        // Start default core services
        serviceHost.startDefaultCoreServicesSynchronously();
        // Start RootNameSpaceService in the host to enumerate
        // all factory services started in host
        serviceHost.startService(new RootNamespaceService());
        // Start AddressBook ServiceFactory
        serviceHost.startFactory(new AddressBookService());
    }
}
