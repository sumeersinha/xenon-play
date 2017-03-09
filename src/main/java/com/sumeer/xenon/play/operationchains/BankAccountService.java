package com.sumeer.xenon.play.operationchains;

import com.vmware.xenon.common.*;

/**
 * Created by sumeers on 3/8/17.
 */
public class BankAccountService extends StatefulService {
    public static final String FACTORY_LINK="/core/account";

    public static class BankAccountState extends ServiceDocument{
        // Primitive data type used
        public double balance;
    }

    // Factory
    public static Service createFactory(){
        /**
         * Idempotent: Service factory will convert a POST to a PUT if a child service is already present, and
         * forward it to the service. The service must handle PUT requests and should perform
         * validation on the request body. The child service can enable STRICT_UPDATE_CHECKING to
         * prevent POSTs from modifying state unless the version and signature match
         */
        return FactoryService.createIdempotent(BankAccountService.class);
    }

    public BankAccountService() {
        super(BankAccountState.class);
        // Persistence, indexing, searching, versioned
        super.toggleOption(ServiceOption.PERSISTENCE, true);
    }

    // Request
    public static class BankAccountServiceRequest{
        public enum Kind {
            DEPOSIT, WITHDRAW
        }

        public Kind kind;
        public double amount;
    }

    // Creation of a bankaccount should validate that the body is not empty and if the amopunt > 0
    @Override
    public void handleStart(Operation start){
        try{
            if(!start.hasBody()){
                throw new IllegalArgumentException("attempt to initialize service with empty state");
            }

            BankAccountState state = start.getBody(BankAccountState.class);
            if (state.balance < 0){
                throw new IllegalArgumentException("Balance can't be negative");
            }

            start.complete();
        }
        catch (Exception e) {
            start.fail(e);
        }
    }

    // For the kind of request, over-ride operation process chain to do appropriate calls for deposit and withdraw
    @Override
    public OperationProcessingChain getOperationProcessingChain(){
        // if the parent service has it, return that
        if(super.getOperationProcessingChain()!=null){
            return super.getOperationProcessingChain();
        }

        // Create a Request Router
        RequestRouter requestRouter = new RequestRouter();
        // On this router register a call for type of deposit request
        requestRouter.register(Action.PATCH, new RequestRouter.RequestBodyMatcher<BankAccountServiceRequest>(BankAccountServiceRequest.class, "kind", BankAccountServiceRequest.Kind.DEPOSIT),this::handlePatchForDeposit,"Deposit service request");
        // On this router register a call for type of withdraw request
        requestRouter.register(Action.PATCH, new RequestRouter.RequestBodyMatcher<BankAccountServiceRequest>(BankAccountServiceRequest.class, "kind", BankAccountServiceRequest.Kind.WITHDRAW),this::handlePatchForWithdraw,"Withdraw service request");

        // Create operation processing Chain for this service
        OperationProcessingChain operationProcessingChain = new OperationProcessingChain(this);

        // Add the router to this op chain
        operationProcessingChain.add(requestRouter);

        // set chain for this service instance
        setOperationProcessingChain(operationProcessingChain);

        return operationProcessingChain;
    }

    void handlePatchForDeposit(Operation patch){
        BankAccountState bankAccountState = getState(patch);
        BankAccountServiceRequest body = patch.getBody(BankAccountServiceRequest.class);

        bankAccountState.balance+=body.amount;

        setState(patch, bankAccountState);
        patch.setBody(bankAccountState);
        patch.complete();
    }

    void handlePatchForWithdraw(Operation patch){
        BankAccountState bankAccountState = getState(patch);
        BankAccountServiceRequest body = patch.getBody(BankAccountServiceRequest.class);

        if(body.amount > bankAccountState.balance){
            patch.fail(new IllegalArgumentException("Not enough funds"));
            return;
        }

        bankAccountState.balance-=body.amount;

        setState(patch, bankAccountState);
        patch.setBody(bankAccountState);
        patch.complete();
    }
}
