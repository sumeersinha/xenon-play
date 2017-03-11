package com.sumeer.xenon.play.taskfsm;

import com.vmware.xenon.common.*;
import com.vmware.xenon.common.fsm.TaskFSMTracker;

/**
 * Created by sumeersinha on 3/11/17.
 */
public class FsmTaskService extends StatefulService {

    // Factory Link
    public static final String FACTORY_LINK="/xenon-play/fsmTask";

    // Factory
    public static Service createFactory(){
        return FactoryService.create(FsmTaskService.class);
    }

    // State Document
    public static class FsmTaskServiceState extends ServiceDocument{
        // tracker
        public TaskFSMTracker fsmInfo;
        //state
        public TaskState taskInfo;
    }

    // Constructor
    public FsmTaskService(){
        super(FsmTaskServiceState.class);
        super.toggleOption(ServiceOption.PERSISTENCE, true);
    }

    @Override
    public void handleStart(Operation start){
        try{
            validateAndFixInitialState(start);
            start.complete();
        }
        catch(Throwable ex){
            start.fail(ex);
        }
    }

    @Override
    public void handlePatch(Operation patch){
        // current state
        FsmTaskServiceState currentState = getState(patch);

        // new state
        FsmTaskServiceState newState = patch.getBody(FsmTaskServiceState.class);

        // validate
        try{
            validateStateTransitionAndInput(currentState, newState);
        }
        catch (Throwable e){
            patch.fail(e);
            return;
        }

        // Kick off anything that is suppose to happen before transition

        // transition
        adjustState(patch, currentState, newState.taskInfo.stage);

        // Complete Patch
        patch.complete();

        // kick off anything that is suppose to happen after transition
    }

    private void validateAndFixInitialState(Operation start) {
        FsmTaskServiceState state = null;

        // Client doesn't have to provide initial state, but if it does, validate it
        if(start.hasBody()){
            state = start.getBody(FsmTaskServiceState.class);
            if(state == null || state.taskInfo == null){
                throw new IllegalArgumentException("attempt to initialize service with empty state");
            }
            if (!TaskState.TaskStage.CREATED.equals(state.taskInfo.stage)){
                throw new IllegalArgumentException("can't initialize service with stage != CREATED");
            }
        }
        else{
            state = new FsmTaskServiceState();
            state.taskInfo = new TaskState();
            state.taskInfo.stage = TaskState.TaskStage.CREATED;
        }

        state.fsmInfo = new TaskFSMTracker();

        start.setBody(state);
    }

    private void validateStateTransitionAndInput(FsmTaskServiceState currentState, FsmTaskServiceState newState) {
        // new state should be valid
        if(newState == null || newState.taskInfo == null || newState.taskInfo.stage == null){
            throw new IllegalArgumentException("new state cannot be null");
        }

        // transition should be valid
        if(!currentState.fsmInfo.isTransitionValid(newState.taskInfo.stage)){
            throw new IllegalArgumentException(String.format("Illegal State Transition: current stage=%s, desired state=%s",currentState.taskInfo.stage, newState.taskInfo.stage));
        }
    }

    private void adjustState(Operation patch, FsmTaskServiceState currentState, TaskState.TaskStage stage) {
        currentState.fsmInfo.adjustState(stage);
        currentState.taskInfo.stage = stage;
        super.setState(patch, currentState);
    }
}
