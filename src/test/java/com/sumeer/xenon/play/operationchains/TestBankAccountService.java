package com.sumeer.xenon.play.operationchains;

import com.vmware.xenon.common.BasicReusableHostTestCase;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.UriUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.UUID;

/**
 * Created by sumeers on 3/8/17.
 */
public class TestBankAccountService extends BasicReusableHostTestCase {

    @Before
    public void setup() throws Exception{
        try{
            // If factory already started, return
            if(this.host.getServiceStage(BankAccountService.FACTORY_LINK) != null){
                return;
            }

            // Start factory
            this.host.startServiceAndWait(BankAccountService.createFactory(), BankAccountService.FACTORY_LINK, null);
        }
        catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCRUD() throws Throwable{
        // locate factory and create service instance
        URI factoryURI = UriUtils.buildFactoryUri(this.host, BankAccountService.class);

        // Test Create

        // Starts a test context used for a single synchronous test execution for the entire host
        this.host.testStart(1);

        BankAccountService.BankAccountState initialState = new BankAccountService.BankAccountState();

        double initialBalance = 100.0;

        initialState.balance = initialBalance;

        initialState.documentSelfLink = UUID.randomUUID().toString();

        URI childURI = UriUtils.buildUri(this.host, BankAccountService.FACTORY_LINK+"/", initialState.documentSelfLink);

        BankAccountService.BankAccountState responses[] = new BankAccountService.BankAccountState[1];

        Operation post = Operation
                .createPost(factoryURI)
                .setBody(initialState)
                .setCompletion((o, e) -> {
                   if(e != null){
                       this.host.failIteration(e);
                       return;
                   }
                   responses[0] = o.getBody(BankAccountService.BankAccountState.class);
                   this.host.completeIteration();
                });

        this.host.send(post);
        this.host.testWait();
        Assert.assertEquals(initialBalance, responses[0].balance, 0);
    }
}
