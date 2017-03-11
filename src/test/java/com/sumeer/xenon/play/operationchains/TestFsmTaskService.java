package com.sumeer.xenon.play.operationchains;

import com.sumeer.xenon.play.taskfsm.FsmTaskService;
import com.vmware.xenon.common.BasicReusableHostTestCase;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.TaskState;
import com.vmware.xenon.common.UriUtils;
import com.vmware.xenon.services.common.TaskService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.UUID;

/**
 * Created by sumeersinha on 3/11/17.
 */
public class TestFsmTaskService extends BasicReusableHostTestCase {

    @Before
    public void setup() throws Exception{
        try{
            // If factory already started, return
            if(this.host.getServiceStage(FsmTaskService.FACTORY_LINK) != null){
                return;
            }

            // Else, start service factory
            this.host.startServiceAndWait(FsmTaskService.createFactory(), FsmTaskService.FACTORY_LINK, null);
        }
        catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFsmService(){
        // factory URI
        URI factoryUri = UriUtils.buildFactoryUri(this.host, FsmTaskService.class);

        // start test context
        this.host.testStart(1);

        // initial state for the child service, set doc self link
        FsmTaskService.FsmTaskServiceState initialState = new FsmTaskService.FsmTaskServiceState();
        initialState.documentSelfLink = UUID.randomUUID().toString();
        initialState.taskInfo = TaskState.create();

        // Child Service URI
        URI childUri = UriUtils.buildUri(this.host, FsmTaskService.FACTORY_LINK+"/"+initialState.documentSelfLink);

        // responses, one in this case
        FsmTaskService.FsmTaskServiceState responses[] = new FsmTaskService.FsmTaskServiceState[1];

        // Create service
        Operation createFsmOp = Operation
                .createPost(factoryUri)
                .setBody(initialState)
                .setCompletion((o, e) -> {
                   if(e != null){
                       this.host.failIteration(e);
                       return;
                   }
                    responses[0] = o.getBody(FsmTaskService.FsmTaskServiceState.class);
                    this.host.completeIteration();
                });
        this.host.send(createFsmOp);
        this.host.testWait();

        Assert.assertEquals(TaskState.TaskStage.CREATED, responses[0].taskInfo.stage);

        // transition to 'Started'
        TaskState.TaskStage expectedStage = TaskState.TaskStage.STARTED;
        FsmTaskService.FsmTaskServiceState finishedState = new FsmTaskService.FsmTaskServiceState();
        finishedState.taskInfo = TaskState.createAsStarted();

        // start test context
        this.host.testStart(1);

        Operation transitionToFinishedOp = Operation
                .createPatch(childUri)
                .setBody(finishedState)
                .setCompletion((o, e) -> {
                    if(e != null){
                        this.host.failIteration(e);
                        return;
                    }
                    responses[0] = o.getBody(FsmTaskService.FsmTaskServiceState.class);
                    this.host.completeIteration();
                });
        this.host.send(transitionToFinishedOp);
        this.host.testWait();

        Assert.assertEquals(expectedStage, responses[0].taskInfo.stage);
    }

}
