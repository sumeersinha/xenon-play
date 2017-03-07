package com.sumeer.xenon.play;

import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.ServiceDocument;
import com.vmware.xenon.common.StatefulService;

/**
 * Created by sumeersinha on 3/6/17.
 */
public class AddressBookService extends StatefulService {

    public static final String FACTORY_LINK = "/core/addressbook";

    // Service document
    public static class AddressBook extends ServiceDocument {
        public String name;
        public Long cell;
        public String address;
    }

    public AddressBookService() {
        super(AddressBook.class);

        // Persistence, indexing, searching, versioned
        super.toggleOption(ServiceOption.PERSISTENCE, true);

        super.toggleOption(ServiceOption.REPLICATION, true);
        super.toggleOption(ServiceOption.INSTRUMENTATION, true);
        super.toggleOption(ServiceOption.OWNER_SELECTION, true);

        // Custom UI
        super.toggleOption(ServiceOption.HTML_USER_INTERFACE, true);
    }

    /**
     * handeCreate() is called when service is created with a POST on service factory
     * We want to validate the document before we create, or else we fail the op
     * This method won't be called when service is started (start after creation as well as restart),
     * for that override handleStart(Operation op). This method is primarily for validation, we can
     * skip if we don't want to validate
     * @param createOp - fetch state and body from this and call complete() on this when done
     */
    @Override
    public void handleCreate(Operation createOp){
        // Create op's body will have a addressbook record / PODO / State
        AddressBook bookStateOnCreate = getBody(createOp);

        // No body? -> fail
        if(bookStateOnCreate == null){
            createOp.fail(new IllegalArgumentException("Address Book create operation doesn't have a valid body"));
            return;
        }

        // Check required fields
        if(bookStateOnCreate.name == null || bookStateOnCreate.name.isEmpty()){
            createOp.fail(new IllegalArgumentException("Name cannot be null or empty"));
            return;
        }

        // Check required fields
        if(bookStateOnCreate.address == null || bookStateOnCreate.address.isEmpty()){
            createOp.fail(new IllegalArgumentException("Address cannot be null or empty"));
            return;
        }

        // Complete operation. Object Document will be created and REST POST operation will return
        createOp.complete();

        // We can do ops here as well, call complete at a later point in time. Caller will
        // be blocked though.
    }

    /**
     * PUT = Discard old doc and put new instead
     * PUT *cannot* be used to create new services but only to update and existing service
     *
     * @param putOp - Fetch put state and body, call complete when done
     */
    @Override
    public void handlePut(Operation putOp){
        // New state
        AddressBook newAddBookState = getBody(putOp);
        // Current state
        AddressBook currentAddBookState = getState(putOp);
        // Validate
        if (newAddBookState == null){
            putOp.fail(new IllegalArgumentException("Cannot put null address"));
            return;
        }

        if (newAddBookState.name == null || newAddBookState.address == null || newAddBookState.name.isEmpty() || newAddBookState.address.isEmpty()){
            putOp.fail(new IllegalArgumentException("Cannot put null name or address"));
            return;
        }

        // Set state
        setState(putOp, newAddBookState);
        // Complete
        putOp.complete();
    }

    /**
     * PATCH = update subset of fields on the service / document
     * PATCH specifies just the fields that need to be updated, rest of fields are unchanged
     *
     * @param patchOp - fetch state, body, call complete() when done
     */
    @Override
    public void handlePatch(Operation patchOp){
        // New state
        AddressBook newAddBookState = getBody(patchOp);
        // Current State
        AddressBook currentAddBookState = getState(patchOp);
        // Validate
        if (newAddBookState == null){
            patchOp.fail(new IllegalArgumentException("Cannot patch null address"));
            return;
        }

        if (newAddBookState.name != null || newAddBookState.address != null){
            patchOp.fail(new IllegalArgumentException("Cannot patch name or address for an address book state"));
            return;
        }

        // Patch phone number
        if(newAddBookState.cell != null){
            currentAddBookState.cell = newAddBookState.cell;
        }

        // Set Body
        patchOp.setBody(currentAddBookState);
        // Complete
        patchOp.complete();
    }
}
